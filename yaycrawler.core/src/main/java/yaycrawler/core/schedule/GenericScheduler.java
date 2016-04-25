package yaycrawler.core.schedule;

import org.slf4j.Logger;
import yaycrawler.core.callback.ITaskExecuteEvent;
import yaycrawler.core.executor.ITaskExecutor;
import yaycrawler.core.model.CrawlerTask;
import yaycrawler.core.model.CrawlerTaskAttempt;
import yaycrawler.core.model.TaskStatusEnum;
import yaycrawler.core.queue.ITaskQueue;

import java.util.Collection;

/**
 * Created by yuananyun on 2016/4/25.
 */
public abstract class GenericScheduler implements IScheduler {
    private Logger logger = org.slf4j.LoggerFactory.getLogger(GenericScheduler.class);

    private ITaskExecutor taskExecutor;
    ITaskExecuteEvent taskExecutedEvent;

    public GenericScheduler() {
        taskExecutedEvent = new GenericTaskExecuteEvent();
    }

    @Override
    public boolean publishTask(CrawlerTask task) {
        return getTaskQueue().add(task);
    }

    @Override
    public boolean publishTasks(Collection<CrawlerTask> tasks) {
        return getTaskQueue().addAll(tasks);
    }

    @Override
    public boolean cancelTask(String taskId) {
        ITaskQueue<CrawlerTaskAttempt> taskAttempt = getProcessingTaskQueue();
        return taskAttempt.remove(taskId);
    }


    @Override
    public void doDispathch() {
        ITaskQueue<CrawlerTask> taskQueue = getTaskQueue();
        while (true) {
            if (!taskQueue.isEmpty() && taskExecutor.canAccept()) {
                CrawlerTask task = taskQueue.poll();
                if (task == null) continue;
                CrawlerTaskAttempt attempt = new CrawlerTaskAttempt(task);
                getProcessingTaskQueue().add(attempt);
                taskExecutor.execute(attempt, taskExecutedEvent);
            } else {
                logger.info("任务队列暂停分配任务，休眠%s秒钟", 10);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    class GenericTaskExecuteEvent implements ITaskExecuteEvent {
        @Override
        public void onExecutionBegin(CrawlerTaskAttempt taskAttempt) {

        }

        @Override
        public void onExecutionCompleted(CrawlerTaskAttempt taskAttempt) {
            if (taskAttempt.getStatus() == TaskStatusEnum.failure) {
                getFailureTaskQueue().add(taskAttempt);
            } else if (taskAttempt.getStatus() == TaskStatusEnum.success) {
                //TODO 执行成功的处理

            }
            getProcessingTaskQueue().remove(taskAttempt);
        }

        @Override
        public void onExecutionException(CrawlerTaskAttempt taskAttempt, Throwable throwable) {
            getFailureTaskQueue().add(taskAttempt);
        }
    }

}
