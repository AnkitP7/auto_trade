package com.kite.automation.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kite.automation.KiteUtils;
import com.kite.automation.persistence.RedisClient;
import com.kite.automation.schedules.KiteAutomationTaskScheduler;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Strategies {

    @Autowired
    KiteUtils kiteUtils;

    @Autowired
    KiteAutomationTaskScheduler automationTaskScheduler;

    @Autowired
    RedisClient redisClient;

    ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init()
    {
        System.out.println("Strategies COMPONENT Initialized. Schedule strategies here now");
        final List<AbstractStrategy> strategies = getStrategies();
        for (AbstractStrategy strategy : strategies) {

                automationTaskScheduler.scheduleTask(strategy);

        }
    }

    @SneakyThrows
    public void add(AbstractStrategy strategy) {
        final String valueAsString = objectMapper.writeValueAsString(strategy);
        redisClient.saveData(RedisClient.STRATEGY_NAMESPACE, strategy.getKeyId(), valueAsString, 3600*12);
    }

    public List<AbstractStrategy> getStrategyList(StrategyTag tag) {
        return getStrategies().stream()
                .filter(abstractStrategy -> {return abstractStrategy.getStrategyTag() == tag;})
                .collect(Collectors.toList());
    }

    public List<AbstractStrategy> getStrategyList(String userId) {
        return getStrategies().stream()
                .filter(abstractStrategy -> {return abstractStrategy.getUserId().equalsIgnoreCase(userId);})
                .collect(Collectors.toList());
    }

    public AbstractStrategy getStrategy(String userId, StrategyTag strategyTag) {
        final List<AbstractStrategy> strategies = getStrategies();
        for (AbstractStrategy strategy:
                strategies) {
            if(strategy.getKeyId().equalsIgnoreCase(userId+"_"+strategyTag.name()))
            {
                return strategy;
            }
        }
        return null;
    }

    public AbstractStrategy getStrategy(String userId, String strategyTag) {
        final List<AbstractStrategy> strategies = getStrategies();
        for (AbstractStrategy strategy:
                strategies) {
            if(strategy.getKeyId().equalsIgnoreCase(userId+"_"+strategyTag))
            {
                return strategy;
            }
        }
        return null;
    }

    public void removeStrategies(String userId)
    {
        final List<AbstractStrategy> strategyList = getStrategyList(userId);
        for (AbstractStrategy strategy:
                strategyList) {
            if(System.getenv(strategy.getUserId() + "_API_KEY") == null
                    || System.getenv(strategy.getUserId() + "_API_KEY").isEmpty())
            {
                continue;
            }
            redisClient.removeData(RedisClient.STRATEGY_NAMESPACE, strategy.getKeyId());
        }
    }

    @SneakyThrows
    private List<AbstractStrategy> getStrategies()
    {
        List<AbstractStrategy> strategies = new ArrayList<>();
        final Set<String> allKeys = redisClient.getAllKeys(RedisClient.STRATEGY_NAMESPACE);
        for (String key:
             allKeys) {
            final String data = redisClient.getData(RedisClient.STRATEGY_NAMESPACE, key);
            try {
                final AbstractStrategy abstractStrategy = objectMapper.readValue(data, AbstractStrategy.class);
                if(System.getenv(abstractStrategy.getUserId() + "_API_KEY") == null
                        || System.getenv(abstractStrategy.getUserId() + "_API_KEY").isEmpty())
                {
                    continue;
                }
                abstractStrategy.setStrategies(this);
                abstractStrategy.setKiteUtils(kiteUtils);
                strategies.add(abstractStrategy);
            } catch (Exception ex)
            {

                log.info("Since not able to parse strategy removing key {}, value {}", key, data);
                redisClient.removeData(RedisClient.STRATEGY_NAMESPACE, key);
            }
        }
        return strategies;
    }

}

