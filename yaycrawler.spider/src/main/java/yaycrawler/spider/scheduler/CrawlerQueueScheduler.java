package yaycrawler.spider.scheduler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ucs_yuananyun on 2016/5/16.
 */
public class CrawlerQueueScheduler implements MonitorableScheduler {
    private BlockingQueue<Request> queue = new LinkedBlockingQueue<Request>();


    @Override
    public void push(Request request, Task task) {
        queue.add(request);
    }

    @Override
    public synchronized Request poll(Task task) {
        return queue.poll();
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        return queue.size();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return queue.size();
    }
}
