package test.entities

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import slatekit.common.DateTimes
import slatekit.common.ids.UPIDs
import slatekit.data.core.LongId
import slatekit.data.core.Meta
import slatekit.data.core.Table
import test.setup.AppEncryptor
import test.setup.StatusEnum
import java.util.*

object EntityFixtures {
    val enc = AppEncryptor
    val encrypted = enc.encrypt("abc123")
    val uuid = "497dea41-8658-4bb7-902c-361014799214"
    val upid = "usa:314fef51-43a7-496c-be24-520e73758836"
    val meta = Meta<Long, SampleEntityImmutable>(LongId { m -> m.id }, Table("sample1"))


    fun sampleImmutable(): SampleEntityImmutable = SampleEntityImmutable(
            id = 0L,
            test_string = "abc",
            test_string_enc = "abc123",
            test_bool = false,
            test_short = 1,
            test_int = 2,
            test_long = 3,
            test_float = 4.5f,
            test_double = 5.5,
            test_enum = StatusEnum.Active,
            test_localdate = LocalDate.of(2021, 1, 20),
            test_localtime = LocalTime.of(13, 30, 45),
            test_localdatetime = LocalDateTime.of(2021, 1, 20, 13, 30, 45),
            test_zoneddatetime = DateTimes.of(2021, 1, 20, 13, 30, 45),
            test_uuid = UUID.fromString(EntityFixtures.uuid),
            test_uniqueId = UPIDs.parse(EntityFixtures.upid)
    )

    fun sampleMutable(): SampleEntityMutable = SampleEntityMutable().apply {
        id = 0L
        test_string = "abc"
        test_string_enc = "abc123"
        test_bool = false
        test_short = 1
        test_int = 2
        test_long = 3
        test_float = 4.5f
        test_double = 5.5
        test_enum = StatusEnum.Active
        test_localdate = LocalDate.of(2021, 1, 20)
        test_localtime = LocalTime.of(13, 30, 45)
        test_localdatetime = LocalDateTime.of(2021, 1, 20, 13, 30, 45)
        test_zoneddatetime = DateTimes.of(2021, 1, 20, 13, 30, 45)
        test_uuid = UUID.fromString(EntityFixtures.uuid)
        test_uniqueId = UPIDs.parse(EntityFixtures.upid)
    }
}