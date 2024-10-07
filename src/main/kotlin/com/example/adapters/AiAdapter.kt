package com.example.adapters

import com.example.adapters.openai.OpenAiAdapter
import com.example.adapters.yandex.YandexAdapter
import io.ktor.client.*

enum class AdapterTypes {
    Yandex,
    OpenAi,
}

data class AiAdapterResponse(
    val isSuccessful: Boolean,
    val result: String = "",
)

abstract class AiAdapter {
    companion object {
        fun getInstance(
            client: HttpClient,
            adapterTypeValue: String?,
        ): CallableAi {
            val aiAdapterType =
                AdapterTypes.valueOf(
                    adapterTypeValue ?: AdapterTypes.Yandex.toString(),
                )
            return when (aiAdapterType) {
                AdapterTypes.Yandex -> YandexAdapter(client)
                AdapterTypes.OpenAi -> OpenAiAdapter(client)
            }
        }
    }
}
