package twinkle.cache;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.ConfigurationFactory;
import play.Application;
import play.Logger;
import play.Logger.ALogger;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import play.inject.ApplicationLifecycle;
import scala.collection.Seq;

public class PersistentCacheModule extends Module {
    private static final ALogger logger = Logger.of(PersistentCacheModule.class);

    @Override
    public Seq<Binding<?>> bindings(Environment arg0, Configuration arg1) {
        return seq(
                bind(PersistentCacheApi.class).toProvider(PersistentCacheProvider.class)
        );
    }

    @Singleton
    public static class PersistentCacheProvider implements Provider<PersistentCacheApi> {
        private static final String CACHE_NAME = "playPersistent";
        private final Provider<Application> applicationProvider;
        private final ApplicationLifecycle lifecycle;
        private PersistentCacheApi persistentCache;

        @Inject
        public PersistentCacheProvider(Provider<Application> applicationProvider, ApplicationLifecycle lifecycle) {
            this.applicationProvider = applicationProvider;
            this.lifecycle = lifecycle;
        }

        @Override
        public PersistentCacheApi get() {
            if (persistentCache == null) {
                synchronized(this) {
                    if (persistentCache == null) {
                        Application application = applicationProvider.get();
                        URL ehcacheXml = application.classloader().getResource("persistentEhcache.xml");
                        if (ehcacheXml == null)
                            ehcacheXml = application.classloader().getResource("persistentEhcache-default.xml");
                        net.sf.ehcache.config.Configuration cacheManagerConfig = ConfigurationFactory.parseConfiguration(ehcacheXml);
                        CacheManager manager = new CacheManager(cacheManagerConfig);
                        logger.debug("PersistentCacheModule CacheManager create");
                        Cache cache = manager.getCache(CACHE_NAME);
                        if (cache == null) {
                            manager.addCache(CACHE_NAME);
                            cache = manager.getCache(CACHE_NAME);
                        }
                        persistentCache = new PersistentCacheApi(cache);

                        lifecycle.addStopHook(() -> {
                            logger.debug("PersistentCacheModule CacheManager shutdown");
                            manager.shutdown();
                            return CompletableFuture.completedFuture(null);
                        });
                    }
                }
            }
            return persistentCache;
        }
    }
}
