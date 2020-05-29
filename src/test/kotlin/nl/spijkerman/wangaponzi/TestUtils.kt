package nl.spijkerman.wangaponzi

import nl.spijkerman.wangaponzi.model.PAYED
import nl.spijkerman.wangaponzi.service.TimeService
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 *
 */
val timeService = TimeService(
        tsh = 16,
        tsm = 0,
        teh = 17,
        tem = 0
)

val beforeStart: LocalTime = timeService.startTime.minusMinutes(30L)
val afterEnd: LocalTime = timeService.endTime.plusMinutes(30L)

fun lines(vararg lines: List<Any>): List<List<Any>> {
    return lines.asList()
}

fun line(time: LocalDateTime = timeService.startTime.atDate(LocalDate.now()),
         who: String = "TEST",
         action: String = PAYED,
         amount: Int = 1): List<Any> {
    return listOf(time.toString().replace('T', ' '), who, action, amount)
}
