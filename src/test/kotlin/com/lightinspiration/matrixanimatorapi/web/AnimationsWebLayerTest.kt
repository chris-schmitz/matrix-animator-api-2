package com.lightinspiration.matrixanimatorapi.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.lightinspiration.matrixanimatorapi.controllers.AnimationController
import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.domain.Frame
import com.lightinspiration.matrixanimatorapi.services.AnimationService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
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

    @Autowired
    private lateinit var objectMapper: ObjectMapper

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
    fun `getAnimations - get all animations`() {
        val expected =
            listOf(
                buildAnimation("a cool animation", 1),
                buildAnimation("another cool animation", 2)
            )
                .map { it.getMeta() }
        whenever(animationService.getAnimationList())
            .thenReturn(expected)
        val request = MockMvcRequestBuilders.get("/rest/animations")

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
    }


    @Test
    fun `saveAnimation - can save an animation`() {
        val animation = buildAnimation("anAnimation")
        val request = MockMvcRequestBuilders
            .post("/rest/animations")
            .content(animation.toJson())
            .contentType(APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isOk)

        verify(animationService).saveAnimation(animation)
    }

    @Test
    fun `updateAnimation - can update an animation`() {
        val animation = buildAnimation("anAnimation", 10)
        val request = MockMvcRequestBuilders
            .put("/rest/animations")
            .content(animation.toJson())
            .contentType(APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isOk)

        verify(animationService).updateAnimation(animation.id!!, animation)
    }

    @Test
    fun `updateAnimation - if an animation does not have an id - expect a 422`() {
        val animation = buildAnimation("anAnimation")
        val request = MockMvcRequestBuilders
            .put("/rest/animations")
            .content(animation.toJson())
            .contentType(APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isUnprocessableEntity)

        verifyNoMoreInteractions(animationService)
    }


    @Test
    fun `deleteAnimation - can delete an existing animation`() {
        val id = 3
        val request = MockMvcRequestBuilders
            .delete("/rest/animations/$id")
            .contentType(APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isOk)

        verify(animationService).deleteAnimation(3)
    }

    private fun buildAnimation(title: String, id: Int? = null): Animation {
        return Animation(
            title,
            1,
            8,
            8,
            2,
            listOf(
                Frame(0, listOf(0xFFFFFF)),
            ),
            id
        )
    }
}
