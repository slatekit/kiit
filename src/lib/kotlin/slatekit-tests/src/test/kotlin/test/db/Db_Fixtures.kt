package test.db

import kiit.common.DateTimes
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId

object Db_Fixtures {
    val zoneId = ZoneId.systemDefault()
    val localDate = LocalDate.of(2021, 2, 1)
    val localTime = LocalTime.of(9, 30, 45)
    val localDateTime = LocalDateTime.of(2021, 2, 1, 9, 30, 45)
    val zonedDateTime = DateTimes.of(2021, 2, 1, 9, 30, 45, zoneId = zoneId)

    val table = "sample_entity"
}