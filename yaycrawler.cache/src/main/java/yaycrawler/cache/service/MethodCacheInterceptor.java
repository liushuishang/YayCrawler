package yaycrawler.cache.service;

/**
 * Created by ucs_guoguibiao on 6/28 0028.
 */

import org.ehcache.Cache;
import org.springframework.beans.factory.InitializingBean;
import java.io.Serializable;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * AOP方法拦截实现缓存的更新的
 * <p>
 * MethodInterceptor：在方法调用会自动调用
 * InitializingBean：系统初始化时会自动初始化此类的特定的方法afterPropertiesSet（）
 *
 * @author longgangbai
 */
public class MethodCacheInterceptor implements MethodInterceptor, InitializingBean {
    private static final Log logger = LogFactory.getLog(MethodCacheInterceptor.class);


    private Cache cache;

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    //invoke方法会在spring配置文件里的，指明的cache拦截的方法的调用时，自动触发它，如这个项目里，
    //当运行HelloEhcacheSpring.java类时，在showPersonsInfo方法里调用到personManager.getList()方法时，
    //它就会先调到这里来执行，执行完才行下执行它的业务
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //这个表示哪个类调用（或触发）了这个MethodCacheInterceptor，
        String targetName = invocation.getThis().getClass().getName();
        //这个表示哪个方法触发了这个类（MethodCacheInterceptor）方法（invoke）的调用，
        String methodName = invocation.getMethod().getName();
        //调用的参数,这里没有参数
        Object[] arguments = invocation.getArguments();
        Object result;

        //这里得出的是:manager.PersonManagerImpl.getList
        String cacheKey = getCacheKey(targetName, methodName, arguments);
        Object element = cache.get(cacheKey);
        if (element == null) {
            // call target/sub-interceptor
            //这个就是调用数据访问方法，
            result = invocation.proceed();
            //如这里调用了getList()方法，会先打印出"get Person from DB" ，
            //然后将结果集放入到result里面去，这里由于使用的是自己配置只能放入10个元素的ehcache，
            //所以这里的result是ArrayList<E> ，它里面存放的是elementData[10]，并将getList得到的结果放入到elementData里面去了
            System.out.println("set into cache");
            // cache method result
            //下面方法执行后，将cacheKey与数据集连起来，cacheKey是用来标识这个element的标志，我们可以有多个element(各自是来自不同的数据访问方法而形成的)，区分它们就是用cacheKey，
            //这里的新生成后的element，含有cacheKey，还在element创建时间，访问时间，还有命令次数等cache的属性，我觉得它就像是一个小cache一样，下次要不要更新它就要看它的这些属性来决定。
//            element = new Element(cacheKey, (Serializable) result);
//            放入cache中
//            cache.put(element);
        } else {
            logger.debug("come from cache ...!");
        }
        //完成cache操作
        System.out.println("out cache");
        return null;
    }

    /**
     * 缓存特定的类：
     *
     * @param targetName
     * @param methodName
     * @param arguments
     * @return
     */
    private String getCacheKey(String targetName, String methodName,
                               Object[] arguments) {
        StringBuffer sb = new StringBuffer();
        sb.append(targetName).append(".").append(methodName);
        if ((arguments != null) && (arguments.length != 0)) {
            for (int i = 0; i < arguments.length; i++) {
                sb.append(".").append(arguments[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 初始化时调用
     */
    public void afterPropertiesSet() throws Exception {
        if (null == cache) {
            throw new IllegalArgumentException("Cache should not be null.");
        }
    }

}



