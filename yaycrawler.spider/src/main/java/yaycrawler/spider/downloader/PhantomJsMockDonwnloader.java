package yaycrawler.spider.downloader;

import com.google.common.io.Files;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.selector.PlainText;

import java.io.*;

/**
 * Created by ucs_yuananyun on 2016/5/27.
 */
public class PhantomJsMockDonwnloader extends AbstractDownloader {
    @Override
    public Page download(Request request, Task task) {

        boolean flag = true;
        Page page = new Page();
        while (flag) {
            Process p = null;//这里我的codes.js是保存在c盘下面的phantomjs目录
            String path =this.getClass().getClassLoader().getResource("/").getPath().substring(1);
            ProcessBuilder processBuilder = new ProcessBuilder( path + "bin/window/phantomjs","phantomDownload.js",request.getUrl());
            processBuilder.directory(new File(path));
            try {
                p = processBuilder.start();
                InputStream is = p.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                StringBuffer sbf = new StringBuffer();
                String tmp = "";
                while ((tmp = br.readLine()) != null) {
                    sbf.append(tmp);
                }
                if(sbf.indexOf("{\"contentType\":null,\"headers\":[],\"id\":30,\"redirectURL\":null,\"stage\":\"end\",\"status\":null,\"statusText\":null") > -1)
                    continue;
                page.setRawText(sbf.toString());
                page.setUrl(new PlainText(request.getUrl()));
                page.setRequest(request);
                page.setStatusCode(200);
                flag = false;
            } catch (Exception e) {
                flag = false;
                e.printStackTrace();
            }

        }
        return page;
    }

    @Override
    public void setThread(int threadNum) {

    }
}
