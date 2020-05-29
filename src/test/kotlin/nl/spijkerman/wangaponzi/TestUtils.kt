package nl.spijkerman.wangaponzi

import nl.spijkerman.wangaponzi.service.TimeService

/**
 *
 */
val timeService = TimeService(
        tsh = 16,
        tsm = 0,
        teh = 17,
        tem = 0
)

val beforeStart = timeService.startTime.minusMinutes(30L)
val afterEnd = timeService.endTime.plusMinutes(30L)