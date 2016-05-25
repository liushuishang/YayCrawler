package yaycrawler.worker.service;

import com.google.common.io.Files;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yaycrawler.common.utils.HttpUtil;
import yaycrawler.common.utils.HttpUtils;
import yaycrawler.spider.persistent.IResultPersistentService;
import yaycrawler.spider.persistent.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/5/23.
 */
@Component
public class ImagePersistentService implements IResultPersistentService {

    @Value("${worker.spider.imagePath}")
    private String imagePath;

    @Override
    /**
     * param data {id:"",srcList:""}
     */
    public boolean saveCrawlerResult(String pageUrl, Map<String, Object> data) {
        //TODO 下载图片
        try {
            List<String> srcList = null;
            String id = "";
            HttpUtil httpUtil = HttpUtil.getInstance();
            List<Header> headers = new ArrayList<>();
//            headers.add(new BasicHeader("",""));
            for (Object o : data.values()) {
                Map<String,Object> regionData=(Map<String,Object>)o;
                if(regionData==null) continue;
                for (Object src : regionData.values()) {
                    if (src instanceof List)
                        srcList = (List<String>) src;
                    else
                        id = src !=null ?src.toString():"";
                }
                if (srcList == null || srcList.isEmpty())
                    continue;
                for (String src : srcList) {
                    byte[] bytes = EntityUtils.toByteArray(httpUtil.doGet(src,null,headers).getEntity());
                    String imgName = StringUtils.substringAfterLast(src,"/");
                    if (!StringUtils.contains(imgName,".")) {
                        imgName = imgName + ".jpg";
                    }
                    File img = new File(imagePath + "/" + id +  "/" + imgName);
                    Files.createParentDirs(img);
                    Files.write(bytes,img);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getSupportedDataType() {
        return PersistentDataType.IMAGE;
    }

}
