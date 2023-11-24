package com.lightinspiration.matrixanimatorapi.domain

import com.fasterxml.jackson.databind.ObjectMapper

data class Animation (
    val title: String,
    val userId: String,
    val height: Int,
    val width: Int,
    val speed: Int,
    val frames: List<Frame>,
    val id: String? = null
) {
    fun toJson() :String{
        return ObjectMapper().writeValueAsString(this)
    }
}
