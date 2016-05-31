package yaycrawler.worker.listener;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;
import yaycrawler.common.model.CrawlerResult;
import yaycrawler.worker.communication.MasterActor;

/**
 * Created by ucs_yuananyun on 2016/5/16.
 */
@Component
public class TaskDownloadFailureListener implements SpiderListener {

    @Autowired
    private MasterActor masterActor;

    @Override
    public void onSuccess(Request request) {

    }

    @Override
    public void onError(Request request) {
        masterActor.notifyTaskFailure(new CrawlerResult(false, DigestUtils.sha1Hex(request.getUrl()), null,"页面下载失败！"));
    }
}
