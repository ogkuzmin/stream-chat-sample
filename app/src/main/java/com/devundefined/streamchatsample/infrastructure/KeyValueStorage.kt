package com.devundefined.streamchatsample.infrastructure

import android.content.SharedPreferences

interface KeyValueStorage {
    fun contains(key: String): Boolean
    fun save(key: String, value: Int)
    fun save(key: String, value: Long)
    fun save(key: String, value: Double)
    fun save(key: String, value: String)
    fun get(key: String, defaultValue: Int): Int
    fun get(key: String, defaultValue: Long): Long
    fun get(key: String, defaultValue: Double): Double
    fun get(key: String, defaultValue: String): String
}

class KeyValueStorageImpl(private val sp: SharedPreferences) : KeyValueStorage {

    override fun contains(key: String) = sp.contains(key)

    override fun save(key: String, value: Int) = sp.edit().putInt(key, value).apply()

    override fun save(key: String, value: Long) = sp.edit().putLong(key, value).apply()

    override fun save(key: String, value: Double) = sp.edit().putLong(key, value.toBits()).apply()

    override fun save(key: String, value: String) = sp.edit().putString(key, value).apply()

    override fun get(key: String, defaultValue: Int) = sp.getInt(key, defaultValue)

    override fun get(key: String, defaultValue: Long) = sp.getLong(key, defaultValue)

    override fun get(key: String, defaultValue: Double) =
        if (sp.contains(key)) Double.fromBits(sp.getLong(key, 0)) else defaultValue

    override fun get(key: String, defaultValue: String) =
        sp.getString(key, defaultValue) ?: defaultValue
}