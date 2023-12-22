package bharat.made.AllInOne.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisClusterConfiguration {

//  @Bean
//  public JedisCluster initJedisCluster(){
//    final int THREAD_POOL_SIZE = 100;
//    Set<HostAndPort> set = new HashSet<>();
//    set.add(new HostAndPort("network-effect-write-heavy.jw9vpg.clustercfg.aps1.cache.amazonaws.com",Integer.parseInt("6379")));
//    JedisCluster jedisCluster = new JedisCluster(set,getPoolCOnfig(THREAD_POOL_SIZE));
//    return jedisCluster;
//  }



  private static GenericObjectPoolConfig getPoolCOnfig (int threads){
    GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
    poolConfig.setMinIdle(threads);
    poolConfig.setMaxIdle(threads);
    poolConfig.setMaxTotal(threads);
    poolConfig.setBlockWhenExhausted(true);
    return poolConfig;
  }

}
