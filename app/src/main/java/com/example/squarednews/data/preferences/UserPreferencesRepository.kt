package com.example.squarednews.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val prefsDataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_SELECTED_COUNTRY_CODE = stringPreferencesKey("country_code")
    }

    fun getCountry(): Flow<String> = prefsDataStore.data.map {
        it[KEY_SELECTED_COUNTRY_CODE] ?: "IN"
    }

    suspend fun setCountry(countryCode: String) {
        prefsDataStore.edit {
            it[KEY_SELECTED_COUNTRY_CODE] = countryCode
        }
    }
}