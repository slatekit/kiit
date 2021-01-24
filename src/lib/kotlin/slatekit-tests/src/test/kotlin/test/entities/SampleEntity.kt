package test.entities

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.common.Field
import slatekit.common.Id
import slatekit.common.ids.UPID
import slatekit.common.ids.UPIDs
import slatekit.entities.EntityUpdatable
import slatekit.entities.EntityWithId
import test.setup.Address
import test.setup.StatusEnum
import java.util.*

data class SampleEntityImmutable(
        @property:Id()
        override val id: Long = 0L,

        @property:Field(length = 30, required = true)
        val test_string: String = "",

        @property:Field(length = 100, encrypt = true)
        val test_string_enc: String = "",

        @property:Field()
        val test_bool: Boolean = false,

        @property:Field()
        val test_short: Short = 35,

        @property:Field()
        val test_int: Int = 35,

        @property:Field()
        val test_long: Long = 35,

        @property:Field()
        val test_float: Float = 20.5f,

        @property:Field()
        val test_double: Double = 20.5,

        @property:Field()
        val test_enum: StatusEnum = StatusEnum.Pending,

        @property:Field()
        val test_localdate: LocalDate = LocalDate.now(),

        @property:Field()
        val test_localtime: LocalTime = LocalTime.now(),

        @property:Field()
        val test_localdatetime: LocalDateTime = LocalDateTime.now(),

        @property:Field()
        val test_zoneddatetime: DateTime = DateTimes.now(),

        @property:Field(length = 50, unique = true)
        val test_uuid: UUID = UUID.randomUUID(),

        @property:Field(length = 50, indexed = true)
        val test_uniqueId: UPID = UPIDs.create("usa"),

        @property:Field()
        val test_object: Address = Address("street 1", "city 1", "state 1", 1,"12345", true)

) : EntityWithId<Long>, EntityUpdatable<Long, SampleEntityImmutable> {
    override fun isPersisted(): Boolean = id > 0

    /**
     * sets the id on the entity and returns the entity with updated id.
     * @param id
     * @return
     */
    override fun withId(id: Long): SampleEntityImmutable {
        return this.copy(id = id)
    }
}


class SampleEntityMutable {
    @property:Id()
    var id: Long = 0L

    @property:Field(length = 30, required = true)
    var test_string: String = ""

    @property:Field(length = 100, encrypt = true)
    var test_string_enc: String = ""

    @property:Field()
    var test_bool: Boolean = false

    @property:Field()
    var test_short: Short = 35

    @property:Field()
    var test_int: Int = 35

    @property:Field()
    var test_long: Long = 35

    @property:Field()
    var test_float: Float = 20.5f

    @property:Field()
    var test_double: Double = 20.5

    @property:Field()
    var test_enum: StatusEnum = StatusEnum.Pending

    @property:Field()
    var test_localdate: LocalDate = LocalDate.now()

    @property:Field()
    var test_localtime: LocalTime = LocalTime.now()

    @property:Field()
    var test_localdatetime: LocalDateTime = LocalDateTime.now()

    @property:Field()
    var test_zoneddatetime: DateTime = DateTimes.now()

    @property:Field(length = 50, unique = true)
    var test_uuid: UUID = UUID.randomUUID()

    @property:Field(length = 50, indexed = true)
    var test_uniqueId: UPID = UPIDs.create("usa")

    @property:Field()
    var test_object: Address = Address("street 1", "city 1", "state 1", 1,"12345", true)
}