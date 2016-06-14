package yaycrawler.master.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import yaycrawler.common.model.WorkerHeartbeat;
import yaycrawler.master.dispatcher.CrawlerTaskDispatcher;

/**
 * 心跳处理器
 * Created by ucs_yuananyun on 2016/5/17.
 */
@Component
public class WorkerHearbeatHandler {
    @Autowired
    private CrawlerTaskDispatcher taskDispatcher;

    public void handler(WorkerHeartbeat heartbeat) {
        /**
         * 判断是否需要分派任务
         */

        //分派任务
        taskDispatcher.assignTasks(heartbeat);
    }
}
