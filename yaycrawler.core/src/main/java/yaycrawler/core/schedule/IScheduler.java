package yaycrawler.core.schedule;

import yaycrawler.core.model.CrawlerTask;
import yaycrawler.core.model.CrawlerTaskAttempt;
import yaycrawler.core.queue.ITaskQueue;

import java.util.Collection;

/**
 * Created by yuananyun on 2016/4/25.
 */
public interface IScheduler {

    /**
     * 获取待处理任务队列
     *
     * @return
     */
    ITaskQueue<CrawlerTask> getTaskQueue();

    /**
     * 获取处理中的任务队列
     *
     * @return
     */
    ITaskQueue<CrawlerTaskAttempt> getProcessingTaskQueue();

    /**
     * 获取处理失败的任务队列
     *
     * @return
     */
    ITaskQueue<CrawlerTaskAttempt> getFailureTaskQueue();


    /**
     * 发布一个任务
     *
     * @param task
     * @return
     */
    boolean publishTask(CrawlerTask task);

    /**
     * 发布多个任务
     * @param tasks
     * @return
     */
    boolean publishTasks(Collection<CrawlerTask> tasks);

    /**
     * 取消一个任务
     *
     * @param taskId
     * @return
     */
    boolean cancelTask(String taskId);

    /**
     * 调度任务
     */
    void doDispathch();


}
