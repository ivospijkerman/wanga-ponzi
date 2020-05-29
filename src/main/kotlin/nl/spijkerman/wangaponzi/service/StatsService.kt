package nl.spijkerman.wangaponzi.service

import nl.spijkerman.wangaponzi.model.Rate
import nl.spijkerman.wangaponzi.model.Stats
import org.springframework.stereotype.Service

@Service
class StatsService(private val timeService: TimeService,
                   private val rateService: RateService,
                   private val userService: UserService) {

    fun get(): Stats {
        val red = rateService.get(Rate.Type.RED)
        val green = rateService.get(Rate.Type.GREEN)
        val users = userService.getAll().sortedBy { it.name }
        val actualBest = userService.getActualTop5()
        val theoryBest = userService.getTheoryTop5()
        val startTime = timeService.startTime
        val endTime = timeService.endTime
        return Stats(startTime, endTime, users, actualBest, theoryBest, red, green)
    }
}