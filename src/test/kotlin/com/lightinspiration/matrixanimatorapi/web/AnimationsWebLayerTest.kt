package com.lightinspiration.matrixanimatorapi.web

import com.fasterxml.jackson.databind.ObjectMapper
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
                .map { it.metadata() }
        whenever(animationService.getAnimationList())
            .thenReturn(expected)
        val request = MockMvcRequestBuilders.get("/rest/animations")

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
    }


    @Test
    fun `saveAnimation - can save an animation`() {
        val storedAnimationId = 3
        val animation = buildAnimation("anAnimation")
        whenever(animationService.saveAnimation(animation))
            .thenReturn(storedAnimationId)
        val request = MockMvcRequestBuilders
            .post("/rest/animations")
            .content(animation.toJson())
            .contentType(APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(content().string(storedAnimationId.toString()))

    }

    @Test
    fun `updateAnimation - can update an animation`() {
        val animationId = 10
        val animation = buildAnimation("anAnimation", animationId)
        whenever(animationService.updateAnimation(animation.id!!, animation))
            .thenReturn(animationId)
        val request = MockMvcRequestBuilders
            .put("/rest/animations/${animation.id}")
            .content(animation.toJson())
            .contentType(APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(content().string(animation.id.toString()))
    }

    //@Test
    //fun `updateAnimation - if there isn't an animation record to update - expect a 422`() {
    //    val badId = 9999
    //    val animation = buildAnimation("anAnimation")
    // TODO ask andrew about this
    // * this is the exception that I want to throw, but it's really being thrown at the repository level, which I can't get to from here
    // * because I'm mocking at the service level. I figured that I could still throw from the mock here to at least assert the conversion from
    // * the internal exception to the http status exception, but when I do I get an error that the service isn't supposed to raise that checked
    // * exception.
    // * at the moment I'm going to put the check in the integration test, which maybe it belongs in anyway, but I figured this would be a good
    // * place to have all of the http-y stuff. What's his opinion on approach??
    //    whenever(animationService.updateAnimation(badId, animation)).thenThrow(NoRecordToUpdateException("can't do it"))
    //    val request = MockMvcRequestBuilders
    //        .put("/rest/animations/$badId")
    //        .content(animation.toJson())
    //        .contentType(APPLICATION_JSON)
    //
    //    mockMvc.perform(request)
    //        .andExpect(status().isUnprocessableEntity)
    //
    //    verifyNoMoreInteractions(animationService)
    //}


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
