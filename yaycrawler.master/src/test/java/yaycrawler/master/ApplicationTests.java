package yaycrawler.master;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void TestLoadingCache() throws Exception{
		LoadingCache<String,String> cahceBuilder= CacheBuilder
				.newBuilder()
				.build(new CacheLoader<String, String>(){
					@Override
					public String load(String key) throws Exception {
						String strProValue="hello "+key+"!";
						return strProValue;
					}

				});
		System.out.println("jerry value:"+cahceBuilder.apply("jerry"));
		System.out.println("jerry value:"+cahceBuilder.get("jerry"));
		System.out.println("peida value:"+cahceBuilder.get("peida"));
		System.out.println("peida value:"+cahceBuilder.apply("peida"));
		System.out.println("lisa value:"+cahceBuilder.apply("lisa"));
		cahceBuilder.put("harry", "ssdded");
		System.out.println("harry value:"+cahceBuilder.get("harry"));
	}


}
