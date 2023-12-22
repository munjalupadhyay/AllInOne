package bharat.made.AllInOne;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.JedisPool;

@SpringBootApplication
public class AllInOneApplication implements CommandLineRunner {

	@Autowired
	private JedisPool jedisPool;

//	@Autowired
//	JedisCluster jedisCluster;

	public static void main(String[] args) {
		SpringApplication.run(AllInOneApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		int threadPool =100;
		Long from = 1l;
		Long to = 100l;

		System.out.println("startrd");

		ExecutorService executorService = Executors.newFixedThreadPool(threadPool);

		long start = System.currentTimeMillis();


		for (long i = from ; i <= to ; i ++ ){
			long finalI = i;
			executorService.submit( () -> {
				try{

					//System.out.println("doing for "+ finalI);
					Map<String,String> localMap = new HashMap<>();


					String team1 = "teamId123:"+finalI+":"+(1*finalI)+":"+""+(10*finalI);
					String team2 = "teamId123:"+finalI+":"+(2*finalI)+":"+""+(20*finalI);
					String team3 = "teamId123:"+finalI+":"+(3*finalI)+":"+""+(30*finalI);

					localMap.put(Long.toString(finalI),team1 + team2+ team3 );
					// jedisCluster.hmset("myKey1", localMap);
					jedisPool.getResource().hmset("myKey1",localMap);
					//System.out.println("done for "+ finalI);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			});

		}

		executorService.shutdown();

		executorService.awaitTermination(
				100, TimeUnit.MINUTES);

		System.out.println("total time taken is == > "+ (System.currentTimeMillis()- start));

	}

}
