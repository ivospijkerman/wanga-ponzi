package nl.spijkerman.wangaponzi.controller

import nl.spijkerman.wangaponzi.model.Stats
import nl.spijkerman.wangaponzi.service.TransactionService
import nl.spijkerman.wangaponzi.service.StatsService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StatsController(private val statsService: StatsService,
                      private val transactionService: TransactionService) {

    @GetMapping
    @CrossOrigin
    fun getStats(): Stats {
        transactionService.getAll()
        return statsService.get()
    }
}