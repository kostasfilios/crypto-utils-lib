package com.crp.system.utils.models

interface Cache<K,V> {
    fun setValueForKey(value: V, key: K)
    fun getValueFromKey(key: K): V?
}