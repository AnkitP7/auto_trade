package com.kite.automation;

import com.kite.automation.persistence.RedisClient;
import com.zerodhatech.kiteconnect.KiteConnect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Component
@Slf4j
public class KiteSessions {

    @Autowired
    RedisClient redisClient;

    public KiteConnect getSession(String userId){
//        return kiteSessionMap.get(userId);
        final String accessToken = redisClient.getData(RedisClient.KITE_SESSION_NAMESPACE, userId);
        KiteConnect kiteConnect = new KiteConnect(System.getenv(userId + "_API_KEY"));
        kiteConnect.setAccessToken(accessToken);
        kiteConnect.setUserId(userId);
        return kiteConnect;
    }

    public void addSession(KiteConnect kiteConnect) {
//        kiteSessionMap.put(kiteConnect.getUserId(), kiteConnect);
        redisClient.saveData(RedisClient.KITE_SESSION_NAMESPACE, kiteConnect.getUserId(), kiteConnect.getAccessToken(), 3600*12);
    }

    public Collection<KiteConnect> getKiteSessions()
    {
        Collection<KiteConnect> returnValue = new ArrayList<>();
        final Set<String> userIds = redisClient.getAllKeys(RedisClient.KITE_SESSION_NAMESPACE);
        for (String userID:
                userIds) {
            final String accessToken = redisClient.getData(RedisClient.KITE_SESSION_NAMESPACE, userID);
            KiteConnect kiteConnect = new KiteConnect(System.getenv(userID + "_API_KEY"));
            kiteConnect.setAccessToken(accessToken);
            kiteConnect.setUserId(userID);
            returnValue.add(kiteConnect);
        }
        return returnValue ;
    }


}

