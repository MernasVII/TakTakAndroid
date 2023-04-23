package tn.esprit.taktakandroid.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import tn.esprit.taktakandroid.utils.Constants.USER_PREF

object AppDataStore {

    private lateinit var _dataStore: DataStore<Preferences>

    fun init(context: Context) {
        _dataStore = context.dataStore
    }
    fun isInitiated():Boolean{
        return true
    }
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREF)

    suspend fun writeString(key: String, value: String) {
        _dataStore.edit { pref -> pref[stringPreferencesKey(key)] = value }
    }

    suspend fun readString(key: String): String? {
        return _dataStore.data.first()[stringPreferencesKey(key)]
    }

    suspend fun deleteString(key: String) {
        _dataStore.edit { pref -> pref.remove(stringPreferencesKey(key)) }
    }

    suspend fun writeInt(key: String, value: Int) {
        _dataStore.edit { pref -> pref[intPreferencesKey(key)] = value }
    }

    suspend fun readInt(key: String): Int? {
        return _dataStore.data.first()[intPreferencesKey(key)]
    }

    suspend fun writeDouble(key: String, value: Double) {
        _dataStore.edit { pref -> pref[doublePreferencesKey(key)] = value }
    }

    suspend fun readDouble(key: String): Double? {
        return _dataStore.data.first()[doublePreferencesKey(key)]
    }

    suspend fun writeLong(key: String, value: Long) {
        _dataStore.edit { pref -> pref[longPreferencesKey(key)] = value }
    }

    suspend fun readLong(key: String): Long? {
        return _dataStore.data.first()[longPreferencesKey(key)]
    }

    suspend fun writeBool(key: String, value: Boolean) {
        _dataStore.edit { pref -> pref[booleanPreferencesKey(key)] = value }
    }

    suspend fun readBool(key: String): Boolean? {
        return _dataStore.data.first()[booleanPreferencesKey(key)]
    }
}
