package twinkle.cache;

import java.util.List;
import java.util.concurrent.Callable;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import play.cache.CacheApi;

public class PersistentCacheApi implements CacheApi {

    private final Ehcache cache;

    PersistentCacheApi(Ehcache cache) {
        this.cache = cache;
    }

    private Object getObjectValue(String key) {
        Element element;
        try {
            element = cache.get(key);
        } catch (CacheException e) {
            element = null;
        }
        Object value = element == null ? null : element.getObjectValue();
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        Element element;
        try {
            element = cache.get(key);
        } catch (CacheException e) {
            element = null;
        }
        Object value = element == null ? null : element.getObjectValue();
        return (T)value;
    }

    /**
     * Returns a list of all elements in the cache.
     *
     * @return keys
     */
    public List<String> getKeys() {
        @SuppressWarnings("unchecked")
        List<String> keys = (List<String>)cache.getKeys();
        return keys;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getOrElse(String key, Callable<T> block, int expiration) {
        Object r = getObjectValue(key);
        if (r == null) {
            T value;
            try {
                value = block.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            set(key,value,expiration);
            return value;
        } else return (T)r;
    }

    @Override
    public <T> T getOrElse(String key, Callable<T> block) {
        return getOrElse(key, block, 0);
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
    public void set(String key, Object value) {
        set(key, value, 0);
    }

    @Override
    public void remove(String key) {
        try {
            cache.remove(key);
        } catch (CacheException e) {
        }
    }
}
