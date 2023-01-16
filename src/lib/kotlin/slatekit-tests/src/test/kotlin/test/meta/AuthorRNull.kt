package test.meta

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import kiit.common.DateTime
import kiit.common.DateTimes
import kiit.common.ids.UPID
import kiit.common.ids.UPIDs
import kiit.entities.EntityUpdatable
import kiit.entities.EntityWithId
import kiit.meta.models.Field
import kiit.meta.models.Id
import test.setup.StatusEnum
import java.util.*

data class AuthorRNull(
        @property:Id()
        val id: Long ? = null,

        @property:Field(required = false)
        val email:String? = null,

        @property:Field(required = false)
        val isActive:Boolean? = null,

        @property:Field(required = false)
        val age:Int?  = null,

        @property:Field(required = false)
        val status: StatusEnum = StatusEnum.Pending,

        @property:Field(required = false)
        val salary:Double? = null,

        @property:Field(required = false)
        val createdAt: DateTime? = null
)


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


data class Address(

        @property:Field(required = true, length = 40)
        val addr   : String,


        @property:Field(required = true, length = 30)
        val city   : String,


        @property:Field(required = true, length = 20)
        val state  : String,


        @property:Field(required = true)
        val country: Int,


        @property:Field(required = true, length = 5)
        val zip    : String,


        @property:Field(required = true)
        val isPOBox: Boolean
)