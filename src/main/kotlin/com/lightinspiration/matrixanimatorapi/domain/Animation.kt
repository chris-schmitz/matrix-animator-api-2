package com.lightinspiration.matrixanimatorapi.domain

import com.fasterxml.jackson.databind.ObjectMapper

data class Animation(
    val title: String,
    val userId: Int,
    val height: Int,
    val width: Int,
    val speed: Int,
    val frames: List<Frame>,
    val id: Int? = null
) {
    fun toJson(): String {
        return ObjectMapper().writeValueAsString(this)
    }
}
