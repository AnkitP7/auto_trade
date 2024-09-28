package com.kite.automation.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.TreeSet;

@Component
@Slf4j
public class RedisClient {
    public static final String STRATEGY_NAMESPACE = "STRATEGY";
    public static final String KITE_SESSION_NAMESPACE = "KITE_SESSION";

    private JedisPool pool = null;
    private String password = null;
    
    public RedisClient()
    {
        init();
    }

    private void init() {
        try {
            final String redis_url = System.getenv("REDIS_URL");
            if(redis_url == null) {
                return;
            }
            URI redisUri = new URI(redis_url);
            pool = new JedisPool(new JedisPoolConfig(),
                    redisUri.getHost(),
                    redisUri.getPort(),
                    Protocol.DEFAULT_TIMEOUT);
            password = System.getenv("REDIS_PASSWORD");
        } catch (URISyntaxException e) {
            // URI couldn't be parsed.
            log.info("Redis URI could not be parsed");
        }
    }

    public void saveData(String namespace, String key, String value, int ttlInSeconds)
    {
        if(pool == null)
        {
            return;
        }
        final Jedis resource = pool.getResource();
        resource.auth(password);
        resource.set(namespace+":"+key, value);
        resource.expire(namespace+":"+key, ttlInSeconds);
        pool.returnResource(resource);
    }

    public String getData(String namespace, String key)
    {
        if(pool == null)
        {
            return null;
        }
        final Jedis resource = pool.getResource();
        resource.auth(password);
        String returnValue =  resource.get(namespace+":"+key);
        pool.returnResource(resource);
        return returnValue;
    }

    public void removeData(String namespace, String key)
    {
        if(pool == null)
        {
            return;
        }
        final Jedis resource = pool.getResource();
        resource.auth(password);
        resource.del(namespace+":"+key);
        pool.returnResource(resource);
    }

    public Set<String> getAllKeys(String namespace)
    {
        Set<String> returnSet = new TreeSet<>();
        if(pool == null)
        {
            log.error("Pool object is null");
            return null;
        }
        final Jedis resource = pool.getResource();
        resource.auth(password);
        Set<String> keys =  resource.keys(namespace+":*");
        for (String key:
             keys) {
            returnSet.add(key.split(":")[1]);
        }
        pool.returnResource(resource);
        return returnSet;
    }

    public synchronized boolean lockKey(String key, int timeToLockInSeconds)
    {
        key = "LOCK:"+key;
        if(pool == null)
        {
            return true;
        }
        final Jedis resource = pool.getResource();
        resource.auth(password);

        final Long result = resource.setnx(key, key);
        resource.expire(key , timeToLockInSeconds );
        pool.returnResource(resource);
        return result == 1;
    }

}
