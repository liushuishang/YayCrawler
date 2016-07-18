package yaycrawler.cache.manager;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.UserManagedCache;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.UserManagedCacheBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import yaycrawler.cache.model.Business;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by ucs_guoguibiao on 7/1 0001.
 */
public class CacheManagerTest {

    @Test
    public void testCache() {
        CacheManager cacheManager
                = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("preConfigured",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)))
                .build();
        cacheManager.init();

        Cache<Long, String> preConfigured =
                cacheManager.getCache("preConfigured", Long.class, String.class);

        Cache<Long, String> myCache = cacheManager.createCache("myCache",
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)).build());

        myCache.put(1L, "da one!");
        String value = myCache.get(1L);
        System.out.println("----------------" + value + "-----------------------");
        cacheManager.removeCache("preConfigured");
        System.out.println("----------------" +  myCache.get(1L) + "-----------------------");
        cacheManager.close();
    }

    @Test
    public void testUserManager() {
        UserManagedCache<String, String> userManagedCache =
                UserManagedCacheBuilder.newUserManagedCacheBuilder(String.class, String.class)
                        .build(false);
        userManagedCache.init();
        userManagedCache.put("key1", "da one!");
        userManagedCache.put("key2","hello ehcache 3");
        System.out.println("----------------" + userManagedCache.get("key1") + "-----------------------");
        userManagedCache.remove("key1");
        System.out.println("----------------" + userManagedCache.get("key2") + "-----------------------");
        System.out.println("----------------" + userManagedCache.get("key1") + "-----------------------");
        userManagedCache.close();
    }

    @Test
    public void testHeap() {
        CacheConfiguration<Long, String> usesConfiguredInCacheConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .heap(10, MemoryUnit.KB)
                        .offheap(10, MemoryUnit.MB))
                .withSizeOfMaxObjectGraph(1000)
                .withSizeOfMaxObjectSize(1000, MemoryUnit.B)
                .build();

        CacheConfiguration<Long, String> usesDefaultSizeOfEngineConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .heap(10, MemoryUnit.KB)
                        .offheap(10, MemoryUnit.MB))
                .build();

        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withDefaultSizeOfMaxObjectSize(500, MemoryUnit.B)
                .withDefaultSizeOfMaxObjectGraph(2000)
                .withCache("usesConfiguredInCache", usesConfiguredInCacheConfig)
                .withCache("usesDefaultSizeOfEngine", usesDefaultSizeOfEngineConfig)
                .build(true);

        Cache<Long, String> usesConfiguredInCache = cacheManager.getCache("usesConfiguredInCache", Long.class, String.class);

        usesConfiguredInCache.put(1L, "one");
        assertThat(usesConfiguredInCache.get(1L), equalTo("one"));

        Cache<Long, String> usesDefaultSizeOfEngine = cacheManager.getCache("usesDefaultSizeOfEngine", Long.class, String.class);

        usesDefaultSizeOfEngine.put(1L, "one");
        assertThat(usesDefaultSizeOfEngine.get(1L), equalTo("one"));

        cacheManager.close();
    }

    @Test
    public void testUpdatePool() {
        ApplicationContext appCtx = new ClassPathXmlApplicationContext("/spring/applicationContext.xml");
        Business business = (Business) appCtx.getBean("business");
        business.delete("猫");
        business.delete("猫");
    }
}
