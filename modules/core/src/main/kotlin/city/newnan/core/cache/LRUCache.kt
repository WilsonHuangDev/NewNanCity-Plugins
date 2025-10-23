package city.newnan.core.cache

import city.newnan.core.terminable.Terminable
import java.util.LinkedHashMap

/**
 * 简单线程安全的 LRU 缓存实现，基于 LinkedHashMap 的访问顺序特性
 * 提供 getOrPut、get、put、remove、clear 等方法，满足项目中常用 API
 */
class LRUCache<K, V>(override val capacity: Int) : Cache<K, V>, Terminable {

    private val map: LinkedHashMap<K, V> = object : LinkedHashMap<K, V>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: Map.Entry<K, V>?): Boolean {
            return capacity > 0 && size > capacity
        }
    }

    override val size: Int
        get() = synchronized(map) { map.size }

    override val keys: Set<K>
        get() = synchronized(map) { HashSet(map.keys) }

    override val values: Collection<V>
        get() = synchronized(map) { ArrayList(map.values) }

    override val entries: Set<Map.Entry<K, V>>
        get() = synchronized(map) { HashSet(map.entries) }

    override fun put(key: K, value: V): V? = synchronized(map) {
        map.put(key, value)
    }

    /**
     * 将另一个 map 的所有条目放入此缓存
     */
    fun putAll(entries: Map<K, V>) = synchronized(map) {
        map.putAll(entries)
    }

    override operator fun get(key: K): V? = synchronized(map) {
        map[key]
    }

    override fun remove(key: K): V? = synchronized(map) {
        map.remove(key)
    }

    override fun clear() = synchronized(map) {
        map.clear()
    }

    /**
     * 关闭缓存，等同于 clear()
     */
    override fun close() = clear()

    override fun getOrDefault(key: K, defaultValue: V): V = synchronized(map) {
        map.getOrDefault(key, defaultValue)
    }

    override fun getOrPut(key: K, defaultValue: () -> V): V = synchronized(map) {
        val existing = map[key]
        if (existing != null) return existing
        val v = defaultValue()
        map[key] = v
        v
    }

    override fun forEach(action: (key: K, value: V) -> Unit) = synchronized(map) {
        val snapshot = ArrayList(map.entries)
        for (e in snapshot) action(e.key, e.value)
    }

    override operator fun set(key: K, value: V) { put(key, value) }
}
