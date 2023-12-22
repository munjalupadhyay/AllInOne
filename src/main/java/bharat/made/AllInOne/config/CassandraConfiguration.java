package bharat.made.AllInOne.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.QueryConsistencyException;
import com.datastax.driver.core.exceptions.TransportException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraConfiguration {



  private static final Logger logger = LoggerFactory.getLogger(CassandraConfiguration.class);

  private Session session;
  private Cluster cluster;

  @Bean(destroyMethod = "")
  public Session getCassandraSession() {

    try {
      PoolingOptions poolingOptions = new PoolingOptions();
      poolingOptions.setMaxConnectionsPerHost(HostDistance.LOCAL, 200);
      poolingOptions.setMaxConnectionsPerHost(HostDistance.REMOTE, 200);
      String[] cassandraIPs = "localhost".split(",");
      int cassandraPort =  9042;
      Builder builder = Cluster.builder();
      List<InetSocketAddress> addresses = new ArrayList<>();
      for (String ip : cassandraIPs) {
        addresses.add(new InetSocketAddress(ip, cassandraPort));
      }
      builder.addContactPointsWithPorts(addresses);
      logger.warn("cassandra contact points: {}", builder.getContactPoints());

      cluster = builder.withPoolingOptions(poolingOptions)
          .withQueryOptions(
              new QueryOptions().setFetchSize( 2000)
                  .setConsistencyLevel(ConsistencyLevel.LOCAL_ONE))
          .withoutJMXReporting()
          .build();
      session = cluster.connect();
      return session;
    } catch (Exception e) {
      logger.error("Error while createing cassandra connection", e);
    }
    return null;
  }

  public void insertOrUpdateOrDelete(Statement st) {
    int maxAttempts =  3;
    for (int i = 0; i < maxAttempts; i++) {
      try {
        session.execute(st);
        return;
      } catch (TransportException | QueryConsistencyException e) {
        if (i == maxAttempts - 1) {
          throw e;
        }
        try {
          Thread.sleep(100);
          logger.info("Retrying due to {} ", e.getMessage());
        } catch (Exception e1) {
          logger.error(e.getMessage(), e);
        }
      }
    }
  }

  public ResultSet get(Statement st) {
    int maxAttempts =  3;
    for (int i = 0; i < maxAttempts; i++) {
      try {
        return session.execute(st);
      } catch (TransportException | QueryConsistencyException e) {
        if (i == maxAttempts - 1) {
          throw e;
        }
        try {
          Thread.sleep(100);
          logger.info("Retrying due to {}", e.getMessage());
        } catch (Exception e1) {
          logger.error(e.getMessage(), e);
        }
      }
    }
    return null;
  }

  public String getKeySpaceName() {
    return  "name_of_keyspace";
  }

  public void destroy() {
    try {
      if (session != null) {
        session.close();
        logger.info("Scylla session destroyed");
      }
      if (cluster != null) {
        cluster.close();
        logger.info("Scylla session closed");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
