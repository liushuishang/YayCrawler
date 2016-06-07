package yaycrawler.spider.resolver;

import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Request;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ucs_yuananyun on 2016/5/20.
 */
public class ParamResolver {
    private static Pattern REQUEST_PATTERN = Pattern.compile("REQUEST\\((.*?)\\)");
    private static Pattern REPLACE_PATTERN = Pattern.compile("REPLACE\\((.*?)\\)");
    public static String resolverFromRequest(Request request, String origin) {
        Matcher mather = REQUEST_PATTERN.matcher(origin);
        while (mather.find()) {
            origin = origin.replace(mather.group(), parseValue(request, mather.group(1)));
        }
        return origin;
    }

    private static String parseValue(Request request,String key )
    {
        if(StringUtils.isBlank(key)) return "";

        Object v=request.getExtra(key);
        if(v!=null) return String.valueOf(v);
        String url = request.getUrl();
        Pattern paramPattern = Pattern.compile(key + "=([^&|?.]*)&?");
        Matcher matcher = paramPattern.matcher(url);
        if(matcher.find())
        {
            return matcher.group(1);
        }
        return "";
    }

    public static String resolverReplaceRequest(Request request, String origin, Object localObject) {
        Matcher mather = REPLACE_PATTERN.matcher(origin);
        String url = request.getUrl();
        if(mather.find()) {
            String key = mather.group(1);
            url = url.replaceAll(key + "=([^&|?.]*)",key + "=" + localObject);
        }
        return url;
    }

//    public static void main(String[] args)
//    {
//        Request request=new Request("http://www.baidu.com?id=123&name=yuananyun");
//        resolverFromRequest(request, "http://floor.0731fdc.com/data.php?_=1463712836318&id=REQUEST(id)&name=REQUEST(name)");
//    }
}
