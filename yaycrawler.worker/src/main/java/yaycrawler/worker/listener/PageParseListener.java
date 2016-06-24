package yaycrawler.worker.listener;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.CrawlerResult;
import yaycrawler.common.utils.UrlUtils;
import yaycrawler.spider.listener.IPageParseListener;
import yaycrawler.worker.communication.MasterActor;
import yaycrawler.worker.model.WorkerContext;
import yaycrawler.worker.service.TaskScheduleService;

import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/5/17.
 */
@Component
public class PageParseListener implements IPageParseListener {

    @Autowired
    private MasterActor masterActor;

    @Autowired
    private TaskScheduleService taskScheduleService;

    @Override
    public void onSuccess(Request request, List<CrawlerRequest> childRequestList) {
        CrawlerResult crawlerResult = new CrawlerResult(true, DigestUtils.sha1Hex(request.getUrl()), childRequestList, null);
        if (childRequestList != null && childRequestList.size() > 0) {
            masterActor.notifyTaskSuccess(crawlerResult);
        } else WorkerContext.completedResultMap.put(crawlerResult.getKey(),crawlerResult);
    }

    @Override
    public void onError(Request request, String failureInfo) {
        CrawlerResult crawlerResult = new CrawlerResult(false, DigestUtils.sha1Hex(request.getUrl()), null, failureInfo);
        WorkerContext.completedResultMap.put(crawlerResult.getKey(),crawlerResult);
    }

    @Override
    public void onCookieChanged(Request request) {
        String domain = UrlUtils.getDomain(request.getUrl());
        taskScheduleService.refreshSpiderSite(domain);
    }

}
