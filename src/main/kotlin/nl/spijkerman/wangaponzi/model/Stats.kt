package nl.spijkerman.wangaponzi.model

import java.time.LocalTime

data class Stats(val startTime: LocalTime,
                 val endTime: LocalTime,
                 val users: List<User>,
                 val actualBest: List<User>,
                 val theoryBest: List<User>,
                 val red: Rate,
                 val green: Rate)