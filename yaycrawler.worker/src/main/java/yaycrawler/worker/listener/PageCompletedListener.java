package yaycrawler.worker.listener;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.CrawlerResult;
import yaycrawler.spider.listener.IPageCompletedListener;
import yaycrawler.worker.communication.MasterActor;

import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/5/17.
 */
@Component
public class PageCompletedListener implements IPageCompletedListener {

    @Autowired
    private MasterActor masterActor;

    @Override
    public void onSuccess(Request request, List<CrawlerRequest> childRequestList) {
        CrawlerResult crawlerResult = new CrawlerResult();
        String key = DigestUtils.shaHex(request.getUrl());
        crawlerResult.setKey(key);
        crawlerResult.setSuccess(true);
        crawlerResult.setCrawlerRequestList(childRequestList);

        masterActor.notifyTaskSuccess(crawlerResult);
    }

    @Override
    public void onError(Request request) {
        masterActor.notifyTaskFailure(new CrawlerResult(false, DigestUtils.shaHex(request.getUrl()), null));
    }
}
