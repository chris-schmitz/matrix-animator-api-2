package com.lightinspiration.matrixanimatorapi.web

import com.lightinspiration.matrixanimatorapi.controllers.AnimationController
import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.domain.Frame
import com.lightinspiration.matrixanimatorapi.services.AnimationService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [AnimationController::class])
class AnimationsWebLayerTest {

    @MockBean
    private lateinit var animationService: AnimationService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `getAnimation - can get an animation`() {
        val id = 1
        val expected = Animation("an animation", 1, 8, 8, 2, listOf(Frame(0, listOf(0xFFFFFF))), id)
        whenever(animationService.getAnimation(id))
            .thenReturn(expected)
        val request = MockMvcRequestBuilders
            .get("/rest/animations/$id")

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(content().json(expected.toJson()))
    }

    @Test
    fun `getAnimation - if record does not exist - expect 404`() {
        val id = 1
        whenever(animationService.getAnimation(id))
            .thenReturn(null)
        val request = MockMvcRequestBuilders
            .get("/rest/animations/$id")

        mockMvc.perform(request)
            .andExpect(status().isNotFound)
    }


    @Test
    fun `can save an animation`() {
        val animation = Animation("an animation", 1, 8, 8, 2, listOf(Frame(0, listOf(0xFFFFFF))))
        val request = MockMvcRequestBuilders
            .post("/rest/animations")
            .content(animation.toJson())
            .contentType(APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isOk)

        verify(animationService).saveAnimation(animation)
    }


}