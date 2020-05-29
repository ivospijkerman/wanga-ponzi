package nl.spijkerman.wangaponzi.service

import nl.spijkerman.wangaponzi.line
import nl.spijkerman.wangaponzi.lines
import nl.spijkerman.wangaponzi.model.PAYED
import nl.spijkerman.wangaponzi.model.Transaction
import nl.spijkerman.wangaponzi.repository.TransactionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.time.LocalDateTime
import java.time.LocalTime
import org.mockito.Mockito.`when` as case

private val mockTransactionRepository = mock(TransactionRepository::class.java)
private val mockRateService = mock(RateService::class.java)

internal class TransactionServiceTest {

    private val sut = TransactionService(mockTransactionRepository, mockRateService)


    @Test
    fun testGetAll_forNoTransactions_expectEmpty() {
        val actual = sut.getAll()

        assertTrue(actual.isEmpty())
    }

    @Test
    fun testGetAll_forOneTransaction_expectOne() {
        // Arrange
        val moment = LocalDateTime.of(1992, 3, 14, 3, 14, 15)
        val name = "Ivo"
        val action = PAYED
        val amount = 1
        case(mockTransactionRepository.getAll()).thenAnswer { lines(line(moment, name, action, amount)) }

        val expected = Transaction(
                moment = LocalTime.of(3, 14, 15),
                user = "Ivo",
                deltaGreen = 0L,
                deltaRed = 0L,
                deltaMoney = 1.0
        )

        // Act
        val actual = sut.getAll()


        // Assert
        assertEquals(1, actual.size)
        assertEquals(expected, actual.first())
    }





}