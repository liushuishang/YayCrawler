package yaycrawler.admin.service;

import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.spider.listener.IPageCompletedListener;

import java.util.List;

/**
 * Created by Administrator on 2016/5/17.
 */
@Component
public class FilePageCompletedListener implements IPageCompletedListener{
    @Override
    public void onSuccess(Request request, List<CrawlerRequest> childRequestList) {

    }

    @Override
    public void onError(Request request) {

    }
}
