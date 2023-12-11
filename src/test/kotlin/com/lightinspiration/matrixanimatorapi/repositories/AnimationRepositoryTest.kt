package com.lightinspiration.matrixanimatorapi.repositories

import com.fasterxml.jackson.databind.ObjectMapper
import com.lightinspiration.matrixanimatorapi.domain.Animation
import com.lightinspiration.matrixanimatorapi.domain.Frame
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

// TODO: ask andrew ->
// * should I try narrowing the classes like this? I don't need a full on spring framework load,
// * just enough to do the dependency injection of the stuff I want for the repository test, right?
// * when I try to use the classes I get an error b/c NamedParamterJdbcTemplate all of the sudden can't
// * find a default constructor
@SpringBootTest(
    //classes = [
    //    AnimationRepository::class,
    //    DataSource::class,
    //    NamedParameterJdbcTemplate::class,
    //    ObjectMapper::class
    //]
)
@ActiveProfiles("integration-test")
@TestPropertySource("classpath:test-application.yml")
class AnimationRepositoryTest {

    @Autowired
    lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

    @Autowired
    lateinit var objectMapper: ObjectMapper
    private lateinit var animationRepository: AnimationRepository


    @BeforeEach
    fun setUp() {
        animationRepository = AnimationRepository(namedParameterJdbcTemplate, objectMapper)
    }

    @Test
    fun `saveAnimation - can save animation record successfully`() {
        val animation = Animation(
            "a cool annimation",
            1,
            8,
            8,
            1,
            listOf(Frame(1, listOf(0xFFFFFF, 0xCCCCCC)))
        )

        animationRepository.save(animation)

        val actual = namedParameterJdbcTemplate.query(
            """
            SELECT * FROM matrix_animator.animations
        """,
        ) { resultSet, _ ->
            val animation = Animation(
                resultSet.getString("title"),
                resultSet.getInt("user_id"),
                resultSet.getInt("height"),
                resultSet.getInt("width"),
                resultSet.getInt("speed"),
                objectMapper.readValue(resultSet.getString("frames"), Array<Frame>::class.java).toList()
            )
            animation
        }.first()
        assertEquals(animation, actual)
    }
}