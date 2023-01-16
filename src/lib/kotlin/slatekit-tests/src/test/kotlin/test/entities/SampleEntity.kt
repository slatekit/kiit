package test.entities

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.common.ids.UPID
import slatekit.common.ids.UPIDs
import kiit.entities.EntityUpdatable
import kiit.entities.EntityWithId
import kiit.entities.Id
import kiit.entities.Column
import test.setup.Address
import test.setup.StatusEnum
import java.util.*

data class SampleEdgeCases(
        @property:Id()
        val id: Long = 0L,

        @property:Column(length = 30, required = true)
        val name: String = "",

        @property:Column()
        val active: Boolean = true,

        @property:Column()
        val item: SampleSubobject = SampleSubobject("sub1", "123")
)

data class SampleSubobject(
        @property:Column(length = 30, required = true)
        val name: String = "",

        @property:Column()
        val uuid: String = ""
)


data class SampleEntityImmutable(
        @property:Id()
        override val id: Long = 0L,

        @property:Column(length = 30, required = true)
        val test_string: String = "",

        @property:Column(length = 100, encrypt = true)
        val test_string_enc: String = "",

        @property:Column()
        val test_bool: Boolean = false,

        @property:Column()
        val test_short: Short = 35,

        @property:Column()
        val test_int: Int = 35,

        @property:Column()
        val test_long: Long = 35,

        @property:Column()
        val test_float: Float = 20.5f,

        @property:Column()
        val test_double: Double = 20.5,

        @property:Column()
        val test_enum: StatusEnum = StatusEnum.Pending,

        @property:Column()
        val test_localdate: LocalDate = LocalDate.now(),

        @property:Column()
        val test_localtime: LocalTime = LocalTime.now(),

        @property:Column()
        val test_localdatetime: LocalDateTime = LocalDateTime.now(),

        @property:Column()
        val test_zoneddatetime: DateTime = DateTimes.now(),

        @property:Column(length = 50, unique = true)
        val test_uuid: UUID = UUID.randomUUID(),

        @property:Column(length = 50, indexed = true)
        val test_uniqueId: UPID = UPIDs.create("usa"),

        @property:Column()
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

    @property:Column(length = 30, required = true)
    var test_string: String = ""

    @property:Column(length = 100, encrypt = true)
    var test_string_enc: String = ""

    @property:Column()
    var test_bool: Boolean = false

    @property:Column()
    var test_short: Short = 35

    @property:Column()
    var test_int: Int = 35

    @property:Column()
    var test_long: Long = 35

    @property:Column()
    var test_float: Float = 20.5f

    @property:Column()
    var test_double: Double = 20.5

    @property:Column()
    var test_enum: StatusEnum = StatusEnum.Pending

    @property:Column()
    var test_localdate: LocalDate = LocalDate.now()

    @property:Column()
    var test_localtime: LocalTime = LocalTime.now()

    @property:Column()
    var test_localdatetime: LocalDateTime = LocalDateTime.now()

    @property:Column()
    var test_zoneddatetime: DateTime = DateTimes.now()

    @property:Column(length = 50, unique = true)
    var test_uuid: UUID = UUID.randomUUID()

    @property:Column(length = 50, indexed = true)
    var test_uniqueId: UPID = UPIDs.create("usa")

    @property:Column()
    var test_object: Address = Address("street 1", "city 1", "state 1", 1,"12345", true)
}