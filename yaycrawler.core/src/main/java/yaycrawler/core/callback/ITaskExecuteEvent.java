package yaycrawler.core.callback;

import yaycrawler.core.model.CrawlerTaskAttempt;

/**
 * Created by yuananyun on 2016/4/25.
 */
public interface ITaskExecuteEvent {
    void onExecutionBegin(CrawlerTaskAttempt taskAttempt);
    void onExecutionCompleted(CrawlerTaskAttempt taskAttempt);
    void onExecutionException(CrawlerTaskAttempt taskAttempt,Throwable throwable);
}
