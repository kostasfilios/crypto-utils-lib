package com.crp.system.utils.models.generic

import com.crp.system.utils.models.Cache
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.concurrent.locks.ReentrantReadWriteLock

@Service
@Qualifier("ReadWriteMapInMemoryCache")
class ReadWriteMapInMemoryCache<K, V>: Cache<K,V> {
    private val lock = ReentrantReadWriteLock()
    private var map = mutableMapOf<K,V>()

    override fun setValueForKey(value: V, key: K){
        try {
            lock.writeLock().lock()
            map[key] = value
        } finally {
            lock.writeLock().unlock()
        }
    }

    override fun getValueFromKey(key: K): V? {
        try {
            lock.readLock().lock()
            return map[key]
        } finally {
            lock.readLock().unlock()
        }
    }

    fun getAllData(): MutableMap<K, V> {
        try {
            lock.readLock().lock()
            return map
        } finally {
            lock.readLock().unlock()
        }
    }

    override fun toString(): String {
        try {
            lock.readLock().lock()
            return map.toString()
        } finally {
            lock.readLock().unlock()
        }
    }
}