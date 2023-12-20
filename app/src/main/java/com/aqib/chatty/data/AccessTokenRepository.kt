package com.aqib.chatty.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * AccessTokenPreferencesRepository is a class that manages access tokens and usernames in a data store.
 * It uses the DataStore Preferences API, which is a part of Android Jetpack.
 *
 * @property dataStore An instance of DataStore for reading and writing data.
 */
class AccessTokenPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val ACCESS_TOKEN =
            stringPreferencesKey("access_token")  // Key for storing and retrieving the access token
        val USERNAME =
            stringPreferencesKey("username")  // Key for storing and retrieving the username
        const val TAG = "AccessTokenPreferencesRepo"  // Tag for logging
    }

    /**
     * Set the access token in the data store.
     * @param accessToken The access token to be stored.
     */
    suspend fun setAccessToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    /**
     * Set the username in the data store.
     * @param username The username to be stored.
     */
    suspend fun setUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME] = username
        }
    }

    /**
     * A flow that emits the access token stored in the data store.
     * It catches any IOException that might occur during the reading of preferences, logs the error, and emits an empty preference in such a case.
     */
    val accessToken: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[ACCESS_TOKEN].orEmpty()
        }

    /**
     * A flow that emits the username stored in the data store.
     * It catches any IOException that might occur during the reading of preferences, logs the error, and emits an empty preference in such a case.
     */
    val username: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[USERNAME].orEmpty()
        }
}