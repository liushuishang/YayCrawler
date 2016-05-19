package yaycrawler.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;

/**
 * Created by ucs_yuananyun on 2016/5/19.
 */
public class SignatureUtils {

    /**
     * 使用SHA1算法生成签名字符串
     * @param paramMap
     * @return
     */
    public static String signWithSHA1(Map<String, Object> paramMap) {
        List<String> keyList = new ArrayList<>(paramMap.keySet());
        Collections.sort(keyList);

        StringBuilder sb = new StringBuilder();
        for (String key : keyList) {
            sb.append("&").append(key).append("=").append(paramMap.get(key));
        }
        return DigestUtils.sha1Hex(sb.substring(1, sb.length()));
    }
}
