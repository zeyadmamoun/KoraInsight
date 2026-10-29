package com.zeyadmamoun.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryDto(
    @SerialName("code")
    val code: String? = null,
    @SerialName("name")
    val name: String,
    @SerialName("logo")
    val logo: String? = null
)
