package nl.spijkerman.wangaponzi.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalTime

@Service
class TimeService(@Value("\${time.start.hour}") tsh: Int,
                  @Value("\${time.start.minute}") tsm: Int,
                  @Value("\${time.end.hour}") teh: Int,
                  @Value("\${time.end.minute}") tem: Int) {

    val startTime: LocalTime = LocalTime.of(tsh, tsm)
    val endTime: LocalTime = LocalTime.of(teh, tem)
}