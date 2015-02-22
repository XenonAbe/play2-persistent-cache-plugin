package cache;

import java.util.List;
import java.util.concurrent.Callable;

import play.api.Application;
import play.api.cache.CacheAPI;
import plugins.PersistentCachePlugin;
import scala.Option;

public class PersistentCache {

    private static CacheAPI cacheAPI() {
        Application app = play.api.Play.unsafeApplication();
        Option<PersistentCachePlugin> plugin = app.plugin(PersistentCachePlugin.class);
        if (!plugin.isDefined())
            throw new RuntimeException("There is no cache plugin registered. Make sure at least one PersistentCachePlugin implementation is enabled.");
        return plugin.get().api();
    }

    /**
     * Retrieves an object by key.
     *
     * @return object
     */
    public static Object get(String key) {
        if (key == null)
            throw new IllegalArgumentException();
        return play.libs.Scala.orNull(cacheAPI().get(key));
    }

    /**
     * Returns a list of all elements in the cache.
     *
     * @return keys
     */
    public static List<String> getKeys() {
        @SuppressWarnings("unchecked")
        List<String> keys = (List<String>)play.libs.Scala.orNull(cacheAPI().get(null));
        return keys;
    }

    /**
     * Retrieve a value from the cache, or set it from a default Callable function.
     *
     * @param key Item key.
     * @param block block returning value to set if key does not exist
     * @param expiration expiration period in seconds.
     * @return value
     */
    @SuppressWarnings("unchecked")
    public static <T> T getOrElse(String key, Callable<T> block, int expiration) throws Exception{
        Object r = play.libs.Scala.orNull(cacheAPI().get(key));
        if (r == null) {
            T value = block.call();
            set(key,value,expiration);
            return value;
        } else return (T)r;

    }

    /**
     * Sets a value with expiration.
     *
     * @param expiration expiration in seconds
     */
    public static void set(String key, Object value, int expiration) {
        cacheAPI().set(key,value,expiration);
    }

    /**
     * Sets a value without expiration.
     *
     */
    public static void set(String key, Object value) {
        cacheAPI().set(key,value,0);
    }

    public static void remove(String key) {
        cacheAPI().remove(key);
    }
}
