package yaycrawler.common.resolver;

import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.selector.Selectable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuananyun on 2016/5/1.
 */
public class SelectorExpressionResolver {

    private static Pattern INVOKE_PATTERN = Pattern.compile("(\\w+)\\((.*)\\)");

    public static <T> T resolve(Selectable selector, String expression) {
        if (selector == null) return null;

        Object localObject = selector;
        String[] invokeArray = expression.split("\\)\\.");
        for (int i = 0; i < invokeArray.length; i++) {
            String invokeStr = invokeArray[i];
            if (!invokeStr.endsWith(")")) invokeStr += ")";

            Matcher matcher = INVOKE_PATTERN.matcher(invokeStr);
            if (matcher.find()) {
                String methodName = matcher.group(1);
                if (StringUtils.isBlank(methodName)) continue;

                String param = matcher.group(2);
                if (param != null) {
                    param = param.replaceAll("\"([^\"]*)\"", "$1");//去掉双引号
                }
                localObject = execute((Selectable) localObject, methodName, param);

                if (!(localObject instanceof Selectable))
                    return (T) localObject;
            }
        }
        if (localObject instanceof Selectable) {
            return (T) ((Selectable) localObject).all();
        } else return (T) localObject;
    }


    private static Object execute(Selectable selector, String methodName, String param) {
        String lowerMethodName = methodName.toLowerCase();
        Selectable selectable = selector;

        if ("xpath".equals(lowerMethodName))
            selectable = selectable.xpath(param);
        else if ("links".equals(lowerMethodName))
            selectable = selectable.links();
        else if ("regex".equals(lowerMethodName))
            selectable = selectable.regex(param);
        else if ("all".equals(lowerMethodName))
            return selectable.all();
        else if("get".equals(lowerMethodName))
            return selectable.get();

        return selectable;
    }
}
