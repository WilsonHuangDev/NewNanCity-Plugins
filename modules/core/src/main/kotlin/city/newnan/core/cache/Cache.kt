package city.newnan.core.cache

/**
 * 通用缓存接口，文档中定义的基本 API
 */
interface Cache<K, V> {
    fun put(key: K, value: V): V?
    operator fun get(key: K): V?
    fun remove(key: K): V?
    fun clear()

    val size: Int
    val capacity: Int
    val keys: Set<K>
    val values: Collection<V>
    val entries: Set<Map.Entry<K, V>>

    fun getOrDefault(key: K, defaultValue: V): V
    fun getOrPut(key: K, defaultValue: () -> V): V
    fun forEach(action: (key: K, value: V) -> Unit)

    operator fun set(key: K, value: V)
}
