package test.setup

import slatekit.common.Field
import slatekit.common.Id
import slatekit.common.ids.UPID
import slatekit.common.ids.UPIDs
import slatekit.entities.EntityWithId
import java.util.*

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


data class UserWithAddress(
        @property:Id(generated = true)
        override val id: Long             = 0,

        @property:Field(required = true)
        val email:String = "",

        @property:Field(required = true)
        val isActive:Boolean = false,

        @property:Field(required = true)
        val age:Int = 35,

        @property:Field(required = true)
        val salary:Double = 20.5,

        @property:Field(required = false)
        val addr:Address? = null,

        @property:Field(required = true)
        val uid: UUID = UUID.fromString(UUIDSamples.sampleUUID1),

        @property:Field(required = true)
        val shardId: UPID = UPIDs.parse(UUIDSamples.sampleUUID2)
) : EntityWithId<Long> {
        override fun isPersisted(): Boolean = id > 0
}



