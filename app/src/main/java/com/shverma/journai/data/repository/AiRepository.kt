package com.shverma.journai.data.repository

import com.shverma.journai.data.network.ApiService
import com.shverma.journai.data.network.model.AiGrammarResponse
import com.shverma.journai.data.network.model.AiPromptResponse
import com.shverma.journai.data.network.model.AiSentimentResponse
import com.shverma.journai.data.network.model.AiTextRequest
import com.shverma.journai.data.network.model.AiTipsResponse
import com.shverma.journai.utils.Resource
import com.shverma.journai.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

interface AiRepository {
    suspend fun getPrompt(): Resource<AiPromptResponse>
    suspend fun correctGrammar(text: String, journalId: String? = null): Resource<AiGrammarResponse>
    suspend fun getTips(text: String, journalId: String? = null): Resource<AiTipsResponse>
    suspend fun analyzeSentiment(
        text: String,
        journalId: String? = null
    ): Resource<AiSentimentResponse>
}

class AiRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AiRepository {

    override suspend fun getPrompt(): Resource<AiPromptResponse> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.getAiPrompt() }
        )
    }

    override suspend fun correctGrammar(
        text: String,
        journalId: String?
    ): Resource<AiGrammarResponse> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.correctGrammar(AiTextRequest(text, journalId)) }
        )
    }

    override suspend fun getTips(text: String, journalId: String?): Resource<AiTipsResponse> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.getAiTips(AiTextRequest(text, journalId)) }
        )
    }

    override suspend fun analyzeSentiment(
        text: String,
        journalId: String?
    ): Resource<AiSentimentResponse> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.analyzeSentiment(AiTextRequest(text, journalId)) }
        )
    }
}
