package com.zeyadmamoun.network.model

import android.os.Message
import kotlinx.serialization.Serializable

@Serializable
data class PlanResponse(
    val tier: String,
    val message: String
)
