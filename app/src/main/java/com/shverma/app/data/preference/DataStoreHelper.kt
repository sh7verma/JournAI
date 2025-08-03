package com.shverma.app.data.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
        private val KEY_DAILY_PROMPT = stringPreferencesKey("daily_prompt")
        private val KEY_PROMPT_DATE = stringPreferencesKey("prompt_date")
        private val KEY_MOOD = stringPreferencesKey("mood")
        private val KEY_STREAK = intPreferencesKey("streak")
        private val KEY_JOURNAL_START_DATE = stringPreferencesKey("journal_start_date")
    }

    suspend fun saveUserSession(
        accessToken: String,
        tokenType: String,
        userId: String,
        userEmail: String,
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            prefs[KEY_TOKEN_TYPE] = tokenType
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USER_EMAIL] = userEmail
        }
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { it[KEY_ACCESS_TOKEN] }
    val tokenType: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN_TYPE] }
    val userId: Flow<String?> = context.dataStore.data.map { it[KEY_USER_ID] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[KEY_USER_EMAIL] }
    val dailyPrompt: Flow<String?> = context.dataStore.data.map { it[KEY_DAILY_PROMPT] }
    val promptDate: Flow<String?> = context.dataStore.data.map { it[KEY_PROMPT_DATE] }
    val mood: Flow<String?> = context.dataStore.data.map { it[KEY_MOOD] }
    val streak: Flow<Int?> = context.dataStore.data.map { it[KEY_STREAK] }
    val journalStartDate: Flow<String?> = context.dataStore.data.map { it[KEY_JOURNAL_START_DATE] }

    suspend fun saveDailyPrompt(prompt: String, date: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DAILY_PROMPT] = prompt
            prefs[KEY_PROMPT_DATE] = date
        }
    }

    suspend fun saveMoodSummary(mood: String?, streak: Int?) {
        context.dataStore.edit { prefs ->
            mood?.let { prefs[KEY_MOOD] = it }
            streak?.let { prefs[KEY_STREAK] = it }
        }
    }

    suspend fun saveJournalStartDate(startDate: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_JOURNAL_START_DATE] = startDate
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_TOKEN_TYPE)
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_USER_EMAIL)
        }
    }
}
