package yaycrawler.core.executor;

import us.codecraft.webmagic.Spider;
import yaycrawler.core.callback.ITaskExecuteEvent;
import yaycrawler.core.model.CrawlerTaskAttempt;
import yaycrawler.core.parser.GenericPageProcessor;

/**
 * Created by yuananyun on 2016/4/25.
 */
public class WebMagicSpiderExecutor implements ITaskExecutor {

    private Spider spider;
    private int threadCount;

    public WebMagicSpiderExecutor(int threadCount) {
        if(threadCount==0) threadCount = 1;
        Spider.create(new GenericPageProcessor()).thread(threadCount);
    }

    @Override
    public boolean canAccept() {
        return false;
    }

    @Override
    public boolean execute(CrawlerTaskAttempt taskAttempt, ITaskExecuteEvent event) {

        //TODO 待实现
        return false;
    }
}
