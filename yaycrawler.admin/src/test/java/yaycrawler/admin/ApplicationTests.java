package yaycrawler.admin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import yaycrawler.admin.communication.MasterActor;
import yaycrawler.quartz.model.ScheduleJobInfo;
import yaycrawler.quartz.model.SimpleExecutableJob;
import yaycrawler.quartz.service.QuartzScheduleService;

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

    @Autowired
    public  QuartzScheduleService quartzScheduleService;

    @Test
    public void testCrawlerQuartz() {
        ScheduleJobInfo jobInfo = new ScheduleJobInfo("testJob", "testGroup", "*/10 * * * * ?");
        quartzScheduleService.addJob(new SimpleExecutableJob(jobInfo));
        while (true) {

        }
    }
}
