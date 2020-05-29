package nl.spijkerman.wangaponzi.service

import nl.spijkerman.wangaponzi.model.Rate.Type.GREEN
import nl.spijkerman.wangaponzi.model.Rate.Type.RED
import nl.spijkerman.wangaponzi.model.User
import org.springframework.stereotype.Service
import java.time.LocalTime

@Service
class UserService(private val transactionService: TransactionService,
                  private val rateService: RateService) {

    private fun hasSufficientFunds(n: Number, delta: Number): Boolean {
        if (delta.toDouble() >= 0) return true
        else if (n.toDouble() + delta.toDouble() > 0) return true
        return false
    }

    fun getAll(): List<User> = transactionService.getAll()
            .groupBy { it.user }
            .map { (name, transactions) ->
                var money = 0.0
                var red = 0L
                var green = 0L

                transactions.sortedBy { it.moment }.forEach {
                    if (hasSufficientFunds(money, it.deltaMoney) &&
                            hasSufficientFunds(red, it.deltaRed) &&
                            hasSufficientFunds(green, it.deltaGreen)) {
                        money += it.deltaMoney
                        red += it.deltaRed
                        green += it.deltaGreen
                    }
                }
                User(name, money, red, green)
            }.toList()

    fun getActualTop5(): List<User> = getAll()
            .sortedByDescending { it.money }
            .filterIndexed { i, _ -> i < 5 }

    fun getTheoryTop5(): List<User> {
        val now = LocalTime.now()
        val redPrice = rateService.get(RED, now).current
        val greenPrice = rateService.get(GREEN, now).current
        return getAll().sortedByDescending { it.money + it.green * greenPrice + it.red * redPrice }
                .filterIndexed { i, _ -> i < 5 }
    }
}
