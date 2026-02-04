package com.shverma.journai.data.network

import com.shverma.journai.data.network.model.AiGrammarResponse
import com.shverma.journai.data.network.model.AiPromptResponse
import com.shverma.journai.data.network.model.AiSentimentResponse
import com.shverma.journai.data.network.model.AiTextRequest
import com.shverma.journai.data.network.model.AiTipsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // --- AI endpoints ---
    @GET("/ai/prompt")
    suspend fun getAiPrompt(): Response<AiPromptResponse>

    @POST("/ai/grammar")
    suspend fun correctGrammar(@Body request: AiTextRequest): Response<AiGrammarResponse>

    @POST("/ai/tips")
    suspend fun getAiTips(@Body request: AiTextRequest): Response<AiTipsResponse>

    @POST("/ai/sentiment")
    suspend fun analyzeSentiment(@Body request: AiTextRequest): Response<AiSentimentResponse>

    // Note: Journal endpoints have been removed and implemented using Room database
}
