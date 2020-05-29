package nl.spijkerman.wangaponzi.service

import nl.spijkerman.wangaponzi.model.*
import nl.spijkerman.wangaponzi.model.Rate.Type.GREEN
import nl.spijkerman.wangaponzi.model.Rate.Type.RED
import org.springframework.stereotype.Service
import nl.spijkerman.wangaponzi.repository.TransactionRepository
import java.lang.System.currentTimeMillis
import java.time.LocalTime
import kotlin.streams.toList



@Service
class TransactionService(private val transactionRepository: TransactionRepository,
                         private val rateService: RateService) {

    fun getAll(): List<Transaction> {
        return transactionRepository.getAll().parallelStream().map {
            val moment = LocalTime.parse(it[0].toString().split(" ")[1])
            val user = it[1].toString()
            val amount = it[3].toString().toInt()

            var money = 0.0
            var green = 0L
            var red = 0L

            fun profit(type: Rate.Type): Double = amount * rateService.get(type, moment).current

            when (it[2].toString()) {
                PAYED ->
                    money += amount
                BUY_RED -> {
                    red += amount
                    money -= profit(RED)
                }
                BUY_GREEN -> {
                    green += amount
                    money -= profit(GREEN)
                }
                SELL_RED -> {
                    red -= amount
                    money += profit(RED)
                }
                SELL_GREEN -> {
                    green -= amount
                    money += profit(GREEN)
                }
                else -> throw RuntimeException("Unexpected action: ${it[2]}")
            }
            Transaction(moment, user, money, red, green)
        }.toList()
    }
}