package yaycrawler.cache.manager;

import com.google.common.collect.Maps;
import org.ehcache.UserManagedCache;
import org.ehcache.config.builders.UserManagedCacheBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by shentong on 2016/7/13.
 */
@Component
public class CacheContext<T> {

    private UserManagedCache<String,Object> userManagedCache =
            UserManagedCacheBuilder.newUserManagedCacheBuilder(String.class,Object.class).build(false);

    public Object get(String key){
        return  userManagedCache.get(key);
    }

    public void addOrUpdateCache(String key,T value) {
        userManagedCache.put(key, value);
    }

    // 根据 key 来删除缓存中的一条记录
    public void evictCache(String key) {
        if(userManagedCache.containsKey(key)) {
            userManagedCache.remove(key);
        }
    }

    // 清空缓存中的所有记录
    public void evictCache() {
        userManagedCache.clear();
    }

}
