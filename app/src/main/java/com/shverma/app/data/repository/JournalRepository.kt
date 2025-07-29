package com.shverma.app.data.repository

import com.shverma.app.data.network.ApiService
import com.shverma.app.data.network.model.JournalByDateResponse
import com.shverma.app.data.network.model.JournalDetail
import com.shverma.app.data.network.model.JournalEntryCreate
import com.shverma.app.utils.Resource
import com.shverma.app.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject


interface JournalRepository {
    suspend fun createEntry(entry: JournalEntryCreate): Resource<JournalDetail>
    suspend fun getEntry(date: String): Resource<JournalDetail>
    suspend fun getHistory(): Resource<List<JournalDetail>>
    suspend fun updateEntry(id: String, entry: JournalEntryCreate): Resource<JournalDetail>
    suspend fun deleteEntry(id: String): Resource<Unit>
    suspend fun getEntriesByDate(date: String): Resource<JournalByDateResponse>
}


class JournalRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : JournalRepository {

    override suspend fun createEntry(entry: JournalEntryCreate): Resource<JournalDetail> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.createJournalEntry(entry) }
        )
    }

    override suspend fun getEntry(id: String): Resource<JournalDetail> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.getJournalEntry(id) }
        )
    }

    override suspend fun getHistory(): Resource<List<JournalDetail>> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.getJournalHistory() }
        )
    }

    override suspend fun updateEntry(
        id: String,
        entry: JournalEntryCreate
    ): Resource<JournalDetail> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.updateJournalEntry(id, entry) }
        )
    }

    override suspend fun deleteEntry(id: String): Resource<Unit> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.deleteJournalEntry(id) }
        )
    }

    override suspend fun getEntriesByDate(date: String): Resource<JournalByDateResponse> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.getJournalEntriesByDate(date) }
        )
    }
}
