package bharat.made.AllInOne.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;

@Configuration
public class MongoConfiguration {
  
  public static final String MONGO_HOSTS="localhost";
  public static final String MONGO_PORTS="27017";

  public static final String MONGO_DB_NAME="myDB";

  public static final String MONGO_USER_NAME="carrom";
  public static final String MONGO_PASSWORD="carrom";

  private static final Logger logger = LoggerFactory.getLogger(MongoConfiguration.class);


  @Bean
  protected MongoTemplate getMongoTemplate() {
    MongoTemplate mongoTemplate = new MongoTemplate(
        makeMongoClient(),
        MONGO_DB_NAME);
    mongoTemplate.setWriteConcern(WriteConcern.W1);
    mongoTemplate.setReadPreference(ReadPreference.secondary());
    mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);

    return mongoTemplate;
  }

  private MongoClient makeMongoClient() {
    return MongoClients.create(this.mongoClientSettings());
  }

  private MongoClientSettings mongoClientSettings() {
    if (MONGO_HOSTS == null) {
      return null;
    } else {
      logger.info("Create mongo settings for Mongo Client");
      List<String> mongoHosts = new ArrayList();
      List<String> mongoPorts = new ArrayList();
      if (MONGO_HOSTS.indexOf(",") > 0) {
        String[] hosts = MONGO_HOSTS.split(",");
        String[] ports = this.MONGO_PORTS.split(",");
        mongoHosts = Arrays.asList(hosts);
        mongoPorts = Arrays.asList(ports);
      } else {
        ((List)mongoHosts).add(MONGO_HOSTS);
        ((List)mongoPorts).add(this.MONGO_PORTS);
      }

      List<ServerAddress> addr = new ArrayList();

      for(int i = 0; i < ((List)mongoHosts).size(); ++i) {
        ServerAddress address = new ServerAddress((String)((List)mongoHosts).get(i), Integer.parseInt((String)((List)mongoPorts).get(i)));
        addr.add(address);
      }

      logger.info("Mongo client server address list: {}", addr.toString());
      MongoClientSettings.Builder builder = MongoClientSettings.builder();
      if (MONGO_USER_NAME != null) {
        logger.info("Adding mongo credentials in settings");
        builder.credential(
            MongoCredential.createCredential(MONGO_USER_NAME, MONGO_DB_NAME, MONGO_PASSWORD.toCharArray()));
      }

      builder.applyToClusterSettings((b) -> {
        b.hosts(addr);
      });
      builder.applyToConnectionPoolSettings((b) -> {
        b.maxConnectionIdleTime(1000, TimeUnit.SECONDS);
        b.maxConnectionLifeTime(1000, TimeUnit.SECONDS);
        b.maxSize(20);
        b.minSize(10);
      });
      builder.retryWrites(true);
      return builder.build();
    }
  }

}
