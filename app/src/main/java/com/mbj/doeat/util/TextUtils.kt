package com.mbj.doeat.util

object TextUtils {

    fun extractMiddleCharacters(postId: String): String {
        if (postId.length < 2) {
            return ""
        }
        return postId.substring(1, postId.length - 1)
    }
}
