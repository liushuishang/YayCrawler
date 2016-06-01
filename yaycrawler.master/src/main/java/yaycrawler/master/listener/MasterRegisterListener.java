package yaycrawler.master.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Created by ucs_yuananyun on 2016/5/27.
 */
public class MasterRegisterListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
//        WebApplicationContext context = (WebApplicationContext) contextRefreshedEvent.getApplicationContext();
//        CrawlerQueueService crawlerQueueService = context.getBean(CrawlerQueueService.class);
//        crawlerQueueService.reflash(0L);
    }
}
