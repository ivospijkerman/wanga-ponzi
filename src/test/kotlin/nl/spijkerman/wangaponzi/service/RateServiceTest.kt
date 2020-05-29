package nl.spijkerman.wangaponzi.service

import nl.spijkerman.wangaponzi.afterEnd
import nl.spijkerman.wangaponzi.beforeStart
import nl.spijkerman.wangaponzi.model.Rate.Type.GREEN
import nl.spijkerman.wangaponzi.model.Rate.Type.RED
import nl.spijkerman.wangaponzi.timeService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class RateServiceTest {

    private val sut = RateService(timeService, 1.0, 10.0)

    @Test
    fun testGet_forRedBeforeStart_expectBeginValue() {
        // Act
        val actual = sut.get(RED, beforeStart).current
        val expected = sut.get(RED, timeService.startTime).current

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun testGet_forGreenAfterEnd_expectEndValue() {
        val actual = sut.get(GREEN, afterEnd).current
        val expected = sut.get(GREEN, timeService.endTime).current

        assertEquals(expected, actual)
    }

    @Test
    fun testGet_forRedAtEnd_expect24100000() {
        val expected = 2.84150646829147
        val actual = sut.get(RED, timeService.endTime).current

        assertEquals(expected, actual)
    }
}