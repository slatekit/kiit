package test.meta

import kiit.common.ids.UPID
import kiit.common.ids.UPIDs
import kiit.entities.EntityWithId
import kiit.meta.models.Id
import kiit.meta.models.Field
import test.setup.UUIDSamples
import java.util.*

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
        val addr: Address? = null,

        @property:Field(required = true)
        val uid: UUID = UUID.fromString(UUIDSamples.sampleUUID1),

        @property:Field(required = true)
        val shardId: UPID = UPIDs.parse(UUIDSamples.sampleUUID2)
) : EntityWithId<Long> {
        override fun isPersisted(): Boolean = id > 0
}