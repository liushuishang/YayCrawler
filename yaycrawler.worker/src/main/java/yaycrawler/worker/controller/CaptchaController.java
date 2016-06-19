package yaycrawler.worker.controller;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import yaycrawler.common.model.RestFulResult;
import yaycrawler.common.utils.ImageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by yuananyun on 2016/6/19.
 */
@RestController
public class CaptchaController {

    @RequestMapping(value = "resolveGeetestSlicePosition", method = RequestMethod.POST)
    public RestFulResult resolveGeetestSlicePosition(@RequestBody Map paramMap) {
        if (paramMap == null) return RestFulResult.failure("参数不能为空！");
        String[] fullbgSrcArray = (String[]) paramMap.get("fullbgSrcArray");
        String[] fullbgPositionArray = (String[]) paramMap.get("fullbgPositionArray");
        String[] bgSrcArray = (String[]) paramMap.get("bgSrcArray");
        String[] bgPositionArray = (String[]) paramMap.get("bgPositionArray");
        int itemCount = MapUtils.getIntValue(paramMap, "itemCount");
        int itemHeight = MapUtils.getIntValue(paramMap, "itemHeight");
        int lineItemCount = MapUtils.getIntValue(paramMap, "lineItemCount");

        try {
            Assert.notEmpty(fullbgSrcArray);
            Assert.notEmpty(fullbgPositionArray);
            Assert.notEmpty(bgSrcArray);
            Assert.notEmpty(bgPositionArray);
            Assert.isTrue(itemCount > 0);
            Assert.isTrue(lineItemCount > 0);
            Assert.isTrue(itemHeight > 0);

            String tmpFolder = System.clearProperty("java.io.tmp");
            String identification = String.valueOf(System.currentTimeMillis());
            String imageSubfix = "jpg";

            List<String[]> fullbgPositionList = new ArrayList<>();
            for (String positionStr : fullbgPositionArray) {
                fullbgPositionList.add(positionStr.replace("px", "").split(" "));
            }
            List<String[]> bgPositionList = new ArrayList<>();
            for (String positionStr : bgPositionArray) {
                bgPositionList.add(positionStr.replace("px", "").split(" "));
            }
            String fullbgImagePath = tmpFolder + identification + "_fullbg." + imageSubfix;
            String bgImagePath = tmpFolder + identification + "_bg." + imageSubfix;
            if (ImageUtils.combineImages(Arrays.asList(fullbgSrcArray), fullbgPositionList, itemCount, lineItemCount, itemHeight, fullbgImagePath, imageSubfix)
                    && ImageUtils.combineImages(Arrays.asList(bgSrcArray), bgPositionList, itemCount, lineItemCount, itemHeight, bgImagePath, imageSubfix)) {
                int deltaX = ImageUtils.findXDiffRectangeOfTwoImage(fullbgImagePath, bgImagePath);
                Map<String,Object> resultMap=new HashedMap();
                resultMap.put("deltaX",deltaX);
                resultMap.put("deltaY",0);
                return RestFulResult.success(resultMap);
            } else {
                return RestFulResult.failure("合成图片失败！");
            }
        } catch (Exception ex) {
            return RestFulResult.failure(ex.getMessage());
        }
    }

}
