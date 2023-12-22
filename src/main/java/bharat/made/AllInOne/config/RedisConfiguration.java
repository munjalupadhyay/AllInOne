package bharat.made.AllInOne.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@org.springframework.context.annotation.Configuration
public class RedisConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(RedisConfiguration.class);





  @Bean
  public JedisPool initialPool() {
    try {
      JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
      jedisPoolConfig.setMaxTotal( 100 );
      jedisPoolConfig.setMinIdle( 100 );
      jedisPoolConfig.setMaxIdle( 100 );
      JedisPool jedisPool = new JedisPool(jedisPoolConfig, "localhost", 6379);
      return jedisPool;
    } catch (Exception e) {
      logger.error("First create JedisPool error : " + e);
    }
    return null;
  }


}
