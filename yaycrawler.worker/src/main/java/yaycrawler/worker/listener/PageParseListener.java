package yaycrawler.worker.listener;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.CrawlerResult;
import yaycrawler.spider.listener.IPageParseListener;
import yaycrawler.worker.communication.MasterActor;

import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/5/17.
 */
@Component
public class PageParseListener implements IPageParseListener {

    @Autowired
    private MasterActor masterActor;

    @Override
    public void onSuccess(Request request, List<CrawlerRequest> childRequestList) {
        masterActor.notifyTaskSuccess(new CrawlerResult(true, DigestUtils.sha1Hex(request.getUrl()), childRequestList, null));
    }

    @Override
    public void onError(Request request, String failureInfo) {
        masterActor.notifyTaskFailure(new CrawlerResult(false, DigestUtils.sha1Hex(request.getUrl()), null, failureInfo));
    }
}
