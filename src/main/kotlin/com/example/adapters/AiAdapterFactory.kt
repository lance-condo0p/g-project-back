package com.example.adapters

import com.example.adapters.openai.OpenAiAdapter
import com.example.adapters.yandex.YandexAdapter
import io.ktor.client.*

data class AiAdapterResponse(
    val isSuccessful: Boolean,
    val result: String = "",
)

abstract class AiAdapterFactory {
    enum class AdapterTypes {
        Yandex,
        OpenAi,
    }

    companion object {
        fun getInstance(
            client: HttpClient,
            adapterTypeValue: String,
        ): CallableAi =
            when (AdapterTypes.valueOf(adapterTypeValue)) {
                AdapterTypes.Yandex -> YandexAdapter(client)
                AdapterTypes.OpenAi -> OpenAiAdapter(client)
            }
    }
}
