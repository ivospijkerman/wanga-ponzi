package nl.spijkerman.wangaponzi.model

import java.time.LocalTime

data class Transaction(val moment: LocalTime,
                       val user: String,
                       val deltaMoney: Double,
                       val deltaRed: Long,
                       val deltaGreen: Long)