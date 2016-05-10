package yaycrawler.common.resolver;

import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.selector.Selectable;

import java.util.LinkedList;
import java.util.List;
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

                String [] paramArray=null;
                String param = matcher.group(2);
                if (param != null) {
                    param = param.replaceAll("\"([^\"]*)\"", "$1");//去掉双引号
                    paramArray = StringUtils.split(param, "$$");
                }
                if(paramArray==null) {
                    paramArray=new String[1];
                    paramArray[0] = param;
                }

                localObject = execute((Selectable) localObject, methodName, paramArray);

                if (!(localObject instanceof Selectable))
                    return (T) localObject;
            }
        }
        return (T) localObject;
    }


    private static Object execute(Selectable selector, String methodName, Object... params) {
        String lowerMethodName = methodName.toLowerCase();
        Selectable selectable = selector;
        try {

            /**
             * 自定义
             */
            if ("constant".equals(lowerMethodName)) {
                return params[0];
            }
            //应该有四个参数（template,varName,start,end)
            if ("paging".equals(lowerMethodName)) {
                List<String> dl = new LinkedList<>();
                String template = String.valueOf(params[0]);
                String varName = String.valueOf(params[1]);
                int start=Integer.parseInt((String)params[2]);
                int end = Integer.parseInt((String) params[3]);
                for(int i=start;i<=end;i++) {
                    dl.add(template.replace(varName + "=?", varName + "=" + i));
                }
                return dl;
            }

            if ("css".equals(lowerMethodName)) {
                if (params.length == 1)
                    selectable = selectable.$(String.valueOf(params[0]));
                else selectable = selectable.$(String.valueOf(params[0]), String.valueOf(params[1]));
            } else if ("xpath".equals(lowerMethodName))
                selectable = selectable.xpath((String) params[0]);
            else if ("links".equals(lowerMethodName))
                selectable = selectable.links();
            else if ("regex".equals(lowerMethodName)) {
                if (params.length == 1)
                    selectable = selectable.regex((String) params[0]);
                else
                    selectable = selectable.regex((String) params[0], (int) params[1]);
            } else if ("all".equals(lowerMethodName))
                return selectable.all();
            else if ("get".equals(lowerMethodName))
                return selectable.get();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return selectable;
    }
}
