package yaycrawler.monitor.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.common.utils.ImageUtils;
import yaycrawler.dao.domain.GeetestTrail;
import yaycrawler.dao.repositories.GeetestTrailRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yuananyun on 2016/6/19.
 */
@RestController
public class CaptchaController {

    @Autowired
    private GeetestTrailRepository geetestTrailRepository;

    /**
     * 解析验证码的位置，并返回移动轨迹数组
     * @param response
     * @param params
     * @return
     */
    @RequestMapping(value = "resolveGeetestSlicePosition", method = RequestMethod.POST)
    public RestFulResult resolveGeetestSlicePosition(HttpServletResponse response, String params) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        Map<String, Object> paramMap = (Map<String, Object>) JSON.parseObject(params, Map.class);
        if (paramMap == null)
          return RestFulResult.failure("参数不能为空！");
        List<String> fullbgSrcList = (List<String>) paramMap.get("fullbgSrcArray");
        List<String> fullbgPositionList = (List<String>) paramMap.get("fullbgPositionArray");
        List<String> bgSrcList = (List<String>) paramMap.get("bgSrcArray");
        List<String> bgPositionList = (List<String>) paramMap.get("bgPositionArray");
        int itemWidth = MapUtils.getIntValue(paramMap, "itemWidth");
        int itemHeight = MapUtils.getIntValue(paramMap, "itemHeight");
        int lineItemCount = MapUtils.getIntValue(paramMap, "lineItemCount");

        try {
            Assert.notEmpty(fullbgSrcList);
            Assert.notEmpty(fullbgPositionList);
            Assert.notEmpty(bgSrcList);
            Assert.notEmpty(bgPositionList);
            Assert.isTrue(fullbgSrcList.size() == 52);
            Assert.isTrue(bgSrcList.size() == 52);
            Assert.isTrue(itemWidth > 0);
            Assert.isTrue(lineItemCount > 0);
            Assert.isTrue(itemHeight > 0);

            String tmpFolder = System.getProperty("user.dir") + "/tmp/";
            File file = new File(tmpFolder);
            if (!file.exists() && !file.isDirectory()) file.mkdir();

            String identification = String.valueOf(System.currentTimeMillis());
            String imageSubfix = "jpg";

            List<String[]> fullbgPointList = new ArrayList<>();
            for (String positionStr : fullbgPositionList) {
                fullbgPointList.add(positionStr.replace("px", "").split(" "));
            }
            List<String[]> bgPointList = new ArrayList<>();
            for (String positionStr : bgPositionList) {
                bgPointList.add(positionStr.replace("px", "").split(" "));
            }
            String fullbgImagePath = tmpFolder + identification + "_fullbg." + imageSubfix;
            String bgImagePath = tmpFolder + identification + "_bg." + imageSubfix;
            if (ImageUtils.combineImages(fullbgSrcList, fullbgPointList, lineItemCount, itemWidth, itemHeight, fullbgImagePath, imageSubfix)
                    && ImageUtils.combineImages(bgSrcList, bgPointList, lineItemCount, itemWidth, itemHeight, bgImagePath, imageSubfix)) {
                int deltaX = ImageUtils.findXDiffRectangeOfTwoImage(fullbgImagePath, bgImagePath);
                deltaX-=7;
                //删除缓存的图片
//                deleteImage(fullbgImagePath);
//                deleteImage(bgImagePath);

                GeetestTrail geetestTrail=geetestTrailRepository.findOneByDeltaX(deltaX);
                if(geetestTrail==null) return RestFulResult.failure("没有匹配到位移为"+deltaX+"的轨迹");
                Map<String, Object> resultMap = new HashedMap();
                resultMap.put("trailId", geetestTrail.getId());
                resultMap.put("deltaX", deltaX);
                resultMap.put("trailArray", geetestTrail.getTrailArray());
                return RestFulResult.success(resultMap);
            } else {
                return RestFulResult.failure("求解轨迹失败！");
            }
        } catch (Exception ex) {
            return RestFulResult.failure(ex.getMessage());
        }
    }

    private void deleteImage(String fullbgImagePath) {
        File file = new File(fullbgImagePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

}
