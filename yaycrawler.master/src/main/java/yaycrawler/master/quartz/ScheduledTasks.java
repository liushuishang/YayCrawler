package yaycrawler.master.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import yaycrawler.common.model.WorkerRegistration;
import yaycrawler.master.dispatcher.CrawlerTaskDispatcher;
import yaycrawler.master.model.MasterContext;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2015/12/29.
 */
@Component
@Configurable
@EnableScheduling
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Resource
    private CrawlerTaskDispatcher crawlerTaskDispatcher;


    @Scheduled(cron = "0 */1 * * * *")
    public void refreshWorker() {
        //移除已经超时的Worker
        ConcurrentHashMap<String, WorkerRegistration> workerRegistrationMap = MasterContext.workerRegistrationMap;
        long currentTime = System.currentTimeMillis();

        for (WorkerRegistration registration : workerRegistrationMap.values()) {
            Long lastTime = registration.getLastHeartbeatTime();
            if (currentTime - lastTime >= 2 * registration.getHeartbeatInteval()) {
                logger.info("{}心跳已经超时，Master移除该Worker！",registration.toString());
                workerRegistrationMap.remove(registration.getWorkerId());
            }
        }
    }


    @Scheduled(cron = "0 */5 * * * *")
    public void refreshQueueCurrentByCron() {
        logger.info("reflash Queue By Cron: The time is now " + dateFormat().format(new Date()));
        logger.info("Start job");
        ExecutorService exec = Executors.newFixedThreadPool(1);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("thread start");
                try {
                    crawlerTaskDispatcher.releaseQueue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                logger.info("thread end");
            }
        });
        exec.execute(thread);
        exec.shutdown();
        while (!exec.isTerminated()) {
            // 等待所有子线程结束，才退出主线程
        }
        logger.info("end job");

    }


    private SimpleDateFormat dateFormat() {
        return new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    }

}
