package yaycrawler.worker.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.WebApplicationContext;
import yaycrawler.worker.communication.MasterActor;
import yaycrawler.worker.model.WorkerContext;

/**
 * Created by ucs_yuananyun on 2016/5/27.
 */
public class WorkerRegisterListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        WebApplicationContext context = (WebApplicationContext) contextRefreshedEvent.getApplicationContext();
        WorkerContext.webApplicationContext = context;
        MasterActor masterActor = context.getBean(MasterActor.class);
        WorkerContext.isSuccessRegisted = masterActor.register();
    }
}
