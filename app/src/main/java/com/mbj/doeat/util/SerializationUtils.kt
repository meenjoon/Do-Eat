package com.mbj.doeat.util

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object SerializationUtils {
    val json = Json

    inline fun <reified T> toJson(value: T): String {
        return json.encodeToString(value)
    }

    inline fun <reified T> fromJson(jsonString: String?): T? {
        return runCatching {
            json.decodeFromString<T>(jsonString ?: "")
        }.getOrNull()
    }
}
