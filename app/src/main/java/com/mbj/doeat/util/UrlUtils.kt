package com.mbj.doeat.util

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object UrlUtils {

    fun encodeUrl(url: String?): String {
        if (url == null) {
            return ""
        }

        return try {
            URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException("Error encoding URL parameter", e)
        }
    }

    fun decodeUrl(encodedUrl: String): String {
        return try {
            URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException("Error decoding URL parameter", e)
        }
    }
}
