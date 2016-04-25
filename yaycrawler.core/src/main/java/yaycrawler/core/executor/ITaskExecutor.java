package yaycrawler.core.executor;

import yaycrawler.core.callback.ITaskExecuteEvent;
import yaycrawler.core.model.CrawlerTaskAttempt;

/**
 * Created by yuananyun on 2016/4/25.
 */
public interface ITaskExecutor {

    boolean canAccept();
    /**
     * 执行一个任务
     * @param taskAttempt
     * @return
     */
    boolean execute(CrawlerTaskAttempt taskAttempt,ITaskExecuteEvent event);
}
