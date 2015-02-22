package cache;

import java.net.URL;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import play.Application;
import play.Logger;
import play.Logger.ALogger;
import play.Plugin;
import play.api.cache.CacheAPI;
import scala.Option;


public class PersistentCachePlugin extends Plugin {
    @SuppressWarnings("unused")
    private static final ALogger logger = Logger.of(PersistentCachePlugin.class);

    private final Application app;
    private CacheManager manager;
    private Ehcache cache;
    private CacheAPI api;

    public PersistentCachePlugin(Application app) {
        this.app = app;
    }

    @Override
    public void onStart() {
        URL ehcacheXml = app.classloader().getResource("persistentEhcache.xml");
        if (ehcacheXml == null)
            ehcacheXml = app.classloader().getResource("persistentEhcache-default.xml");
        manager = CacheManager.newInstance(ehcacheXml);
        manager.addCache("playPersistent");
        //logger.debug("persistentEhcache config = {}", manager.getActiveConfigurationText());
        cache = manager.getEhcache("playPersistent");
        api = new EhCacheImpl(cache);
    }

    @Override
    public void onStop() {
        if (manager != null) {
            manager.shutdown();
        }

    }

    @Override
    public boolean enabled() {
        return !"disabled".equals(app.configuration().getString("persistentehcacheplugin"));
    }

    public CacheAPI api() {
        return api;
    }

    private static class EhCacheImpl implements CacheAPI {

        private final Ehcache cache;

        public EhCacheImpl(Ehcache cache) {
            this.cache = cache;
        }

        @Override
        public void set(String key, Object value, int expiration) {
            Element element = new Element(key, value);
            if (expiration == 0)
                element.setEternal(true);
            element.setTimeToLive(expiration);
            cache.put(element);
        }

        @Override
        public Option<Object> get(String key) {
            if (key == null) {
                // すべてのキーを取得
                return Option.apply((Object)cache.getKeys());
            }

            Element element;
            try {
                element = cache.get(key);
            } catch (CacheException e) {
                element = null;
            }
            Object value = element == null ? null : element.getObjectValue();
            return Option.apply(value);
        }

        @Override
        public void remove(String key) {
            try {
                cache.remove(key);
            } catch (CacheException e) {
            }
        }
    }
}
