package nl.spijkerman.wangaponzi.service

import nl.spijkerman.wangaponzi.model.Rate
import nl.spijkerman.wangaponzi.model.Tick
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalTime
import kotlin.math.max
import kotlin.math.min

@Service
class RateService(timeService: TimeService,
                  @Value("\${exchange.min}") private val exchangeMin: Double,
                  @Value("\${exchange.max}") private val exchangeMax: Double
) {
    private val startTime = timeService.startTime
    private val endTime = timeService.endTime

    fun get(type: Rate.Type,
            moment: LocalTime): Rate {
        val timePassed = timePassed(moment)

        val lines = javaClass.getResource("/static/${type.name}.rate").readText().split("\n")

        val limit = lines.size * timePassed
        val tickSeconds: Double = Duration.between(startTime, endTime).seconds.toDouble() / lines.size

        fun scale(input: String) = type.scale(exchangeMin, exchangeMax, input.trim().toDouble())

        val history = lines.filterIndexed { index, _ -> index <= limit }
                .map(::scale)
                .mapIndexed { index, value ->
                    val time = startTime.plusSeconds((index * tickSeconds).toLong())
                    Tick(time, value)
                }

        return Rate(history)
    }


    private fun timePassed(moment: LocalTime): Double {
        val currDuration = Duration.between(startTime, moment).seconds
        val totalDuration = Duration.between(startTime, endTime).seconds

        val tempResult = currDuration.toDouble() / totalDuration
        return max(min(tempResult, 1.0), 0.0)
    }
}