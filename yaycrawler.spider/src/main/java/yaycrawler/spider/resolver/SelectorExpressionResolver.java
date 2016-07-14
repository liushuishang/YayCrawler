package yaycrawler.spider.resolver;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.Selectable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuananyun on 2016/5/1.
 */
public class SelectorExpressionResolver {
    private static Logger logger = LoggerFactory.getLogger(SelectorExpressionResolver.class);
    private static Pattern INVOKE_PATTERN = Pattern.compile("(\\w+)\\((.*)\\)");


    public static <T> T resolve(Request request, Selectable selector, String expression) {
        if (selector == null) return null;

//        logger.info("开始解析表达式{},解析上下文为：\r\n{}", expression, selector.toString());
        Object localObject = selector;
        String[] invokeArray = expression.split("\\)\\.");
        for (int i = 0; i < invokeArray.length; i++) {
            String invokeStr = invokeArray[i];
            if (!invokeStr.endsWith(")")) invokeStr += ")";

            Matcher matcher = INVOKE_PATTERN.matcher(invokeStr);
            if (matcher.find()) {
                String methodName = matcher.group(1);
                if (StringUtils.isBlank(methodName)) continue;

                String[] paramArray = null;
                String param = matcher.group(2);
                if (param != null) {
//                    param = param.replaceAll("\"([^\"]*)\"", "$1");//去掉双引号
                    paramArray = param.split("\\$\\$");
                }
                if (paramArray == null) {
                    paramArray = new String[1];
                    paramArray[0] = param;
                }
//                logger.info("开始执行函数:{},参数为:{}", methodName, JSON.toJSONString(paramArray));
                localObject = execute(request, localObject, methodName, paramArray);
//                logger.info("函数:{} 执行成功，结果为:{}\r\n", methodName, JSON.toJSONString(localObject));
            }
        }
//        logger.info("表达式{}解析完成", expression);
        return (T) localObject;
    }


    private static Object execute(Request request, Object localObject, String methodName, Object... paramArray) {
        String lowerMethodName = methodName.toLowerCase();
        /**
         * 参数处理
         */
        String[] params = new String[paramArray.length];
        for (int i = 0; i < paramArray.length; i++) {
            String p = String.valueOf(paramArray[i]);
            if (p.startsWith("\""))
                p = p.substring(1, p.length());
            if (p.endsWith("\""))
                p = p.substring(0, p.length() - 1);
            params[i] = p;
        }
        try {
            /**
             * 自定义常量字段
             */
            if ("constant".equals(lowerMethodName)) {
                return params[0];
            }
            /**
             * 自定义Url解析
             */
            if ("customurl".equals(lowerMethodName)) {
                String url = params[0];
                if(localObject == null)
                    return null;
                if (url.contains("REQUEST("))
                    url = ParamResolver.resolverFromRequest(request, url);
                else if(url.contains("REPLACE(") && localObject != null)
                    url = ParamResolver.resolverReplaceRequest(request,url,localObject);
                return url;
            }
            //应该有四个参数（template,varName,start,end)
            //
            if ("paging".equals(lowerMethodName)) {
                List<String> dl = new LinkedList<>();
                String template = String.valueOf(params[0]);
                String varName = String.valueOf(params[1]);
                int start = Integer.parseInt((String) params[2]);
                int end = Integer.parseInt((String) params[3]);
                for (int i = start; i <= end; i++) {
                    dl.add(template.replace(varName + "=?", varName + "=" + i));
                }
                return dl;
            }
            if("tostring".equals(lowerMethodName))
                return localObject.toString();

            if (localObject instanceof Selectable)
                return executeSelectable(request, (Selectable) localObject, lowerMethodName, params);
            else
                return executeScalar(request, localObject, lowerMethodName, params);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    private static Object executeScalar(Request request, Object localObject, String lowerMethodName, String[] params) {

        if("stringreplace".equals(lowerMethodName)&&params.length==2)
        {
            String oldValue = params[0];
            String newValue = params[1];
            if (localObject instanceof Collection) {
                Collection itemCollection = (Collection) localObject;
                List<String> itemList = new LinkedList<>();
                for (Object o : itemCollection) {
                    itemList.add(String.valueOf(o).replace(oldValue, newValue));
                }
                return itemList;
            } else return String.valueOf(localObject).replace(oldValue, newValue);
        }
        if ("prefix".equals(lowerMethodName)) {
            //附加一个前缀
            String prefixValue = params[0];
            if (localObject instanceof Collection) {
                Collection itemCollection = (Collection) localObject;
                if (itemCollection.size() == 0) return localObject;
                List<String> itemList = new LinkedList<>();
                for (Object o : itemCollection) {
                    itemList.add(prefixValue + String.valueOf(o));
                }
                return itemList;
            } else return prefixValue + String.valueOf(localObject);
        }
        if("suffix".equals(lowerMethodName)) {
            //附加一个后缀缀
            String suffixValue = params[0];
            if (localObject instanceof Collection) {
                Collection itemCollection = (Collection) localObject;
                if (itemCollection.size() == 0) return localObject;
                List<String> itemList = new LinkedList<>();
                for (Object o : itemCollection) {
                    itemList.add( String.valueOf(o) + suffixValue);
                }
                return itemList;
            } else return String.valueOf(localObject) + suffixValue ;
        }
        return localObject;
    }

    private static Object executeSelectable(Request request, Selectable selectable, String lowerMethodName, String[] params) {
        if (selectable == null) return null;

        if ("getjson".equals(lowerMethodName))
            return new Json(selectable.get());

        if ("css".equals(lowerMethodName)) {
            if (params.length == 1)
                return selectable.$(String.valueOf(params[0]));
            return selectable.$(String.valueOf(params[0]), String.valueOf(params[1]));
        }

        if ("replace".equals(lowerMethodName)) {
            return selectable.replace(String.valueOf(params[0]), String.valueOf(params[1]).trim());
        }

        if ("xpath".equals(lowerMethodName))
            return selectable.xpath((String) params[0]);

        if ("links".equals(lowerMethodName))
            return selectable.links();

        if ("regex".equals(lowerMethodName)) {
            if (params.length == 1)
                return selectable.regex((String) params[0]);
            else
                return selectable.regex((String) params[0], Integer.parseInt(String.valueOf(params[1])));
        }

        if ("jsonpath".equals(lowerMethodName)) {
            if(!(selectable instanceof  Json))
               return  new Json(selectable.get()).jsonPath(params[0]);
            return selectable.jsonPath(params[0]);
        }

        if ("all".equals(lowerMethodName))
            return selectable.all();

        if ("get".equals(lowerMethodName)) {
            String r = selectable.get();
            return r == null ? null : r.trim();
        }
        return selectable;
    }


}
