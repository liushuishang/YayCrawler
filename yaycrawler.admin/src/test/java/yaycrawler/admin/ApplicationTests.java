package yaycrawler.admin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import yaycrawler.admin.communication.MasterActor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ApplicationTests {

	@Autowired
	private MasterActor masterActor;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testQueue() {
//		Map map = new HashMap<>();
//		String data = masterActor.retrievedFailQueueRegistrations();
//		System.out.println(data);
//		data = masterActor.retrievedItemQueueRegistrations();
//		System.out.println(data);
//		data = masterActor.retrievedRunningQueueRegistrations();
//		System.out.println(data);
//		data = masterActor.retrievedSuccessQueueRegistrations();
//		System.out.println(data);
	}

}
