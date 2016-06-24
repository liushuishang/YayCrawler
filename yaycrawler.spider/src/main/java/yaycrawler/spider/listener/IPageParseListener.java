package yaycrawler.spider.listener;

import us.codecraft.webmagic.Request;
import yaycrawler.common.model.CrawlerRequest;

import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/5/17.
 */
public interface IPageParseListener {

    void onSuccess(Request request, List<CrawlerRequest> childRequestList);

    void onError(Request request,String failureInfo);

    void onCookieChanged(Request request);

}
