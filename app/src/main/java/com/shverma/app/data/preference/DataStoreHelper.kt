package com.shverma.app.data.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


private val Context.dataStore by preferencesDataStore(name = "journai_datastore")

@Singleton
class DataStoreHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object Companion {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_TOKEN_TYPE = stringPreferencesKey("token_type")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_CREATED_AT = stringPreferencesKey("user_created_at")
    }

    suspend fun saveUserSession(
        accessToken: String,
        tokenType: String,
        userId: String,
        userEmail: String,
        userCreatedAt: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            prefs[KEY_TOKEN_TYPE] = tokenType
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USER_EMAIL] = userEmail
            prefs[KEY_USER_CREATED_AT] = userCreatedAt
        }
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { it[KEY_ACCESS_TOKEN] }
    val tokenType: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN_TYPE] }
    val userId: Flow<String?> = context.dataStore.data.map { it[KEY_USER_ID] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[KEY_USER_EMAIL] }
    val userCreatedAt: Flow<String?> = context.dataStore.data.map { it[KEY_USER_CREATED_AT] }

    suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_TOKEN_TYPE)
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_USER_EMAIL)
            prefs.remove(KEY_USER_CREATED_AT)
        }
    }
}
