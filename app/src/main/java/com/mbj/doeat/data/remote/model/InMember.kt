package com.mbj.doeat.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class InMember(
    var inMemberId: String? = null,
    val userId: String? = null,
    val guest: Boolean? = null
)
