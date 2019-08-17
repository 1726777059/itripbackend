package cn.itrip.common;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class JredisApi {

    Jedis redis = new Jedis("127.0.0.1",6379);

    public void SetRedis(String key,int second,String value){
        redis.auth("123456");
        redis.setex(key,second,value);

    }
    public String getRedis(String key){
      return   redis.get(key);

    }

    public static void main(String[] args) {
        JredisApi jredisApi =new JredisApi();
        jredisApi.SetRedis("Code:"+"13015340451",69,"134452");
        System.out.println(jredisApi.getRedis("Code:"+"13015340451")); ;
    }
}
