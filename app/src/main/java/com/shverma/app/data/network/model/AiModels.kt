package com.shverma.app.data.network.model

// Request models
data class AiTextRequest(
    val text: String,
    val journalId: String? = null
)

// Response models
data class AiPromptResponse(
    val prompt: String
)

data class AiGrammarResponse(
    val corrected: String
)

data class AiTipsResponse(
    val tips: List<String>
)

data class AiSentimentResponse(
    val mood: String,
    val analysis: String
)
