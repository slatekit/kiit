package test.setup

import kiit.common.DateTime
import kiit.common.ids.UPID
import kiit.common.ids.UPIDs
import kiit.common.utils.Random
import kiit.entities.EntityWithId
import kiit.entities.Id
import kiit.entities.Column
import java.util.*

data class AuthorEnc(
        @property:Id(generated = true)
        override val id: Long = 0,

        @property:Column(required = true)
        val uuid: String            = Random.uuid(),

        @property:Column(required = true)
        val createdAt: DateTime = DateTime.now(),

        @property:Column(required = true)
        val createdBy: Long = 0,

        @property:Column(required = true)
        val updatedAt: DateTime = DateTime.now(),

        @property:Column(required = true)
        val updatedBy: Long = 0,

        @property:Column(required = true, encrypt = true)
        val email:String = "",

        @property:Column(required = true)
        val isActive:Boolean = false,

        @property:Column(required = true)
        val age:Int = 35,

        @property:Column(required = true)
        val status: StatusEnum = StatusEnum.Pending,

        @property:Column(required = true)
        val salary:Double = 20.5,

        @property:Column(required = true)
        val uid: UUID = UUID.fromString(UUIDSamples.sampleUUID1),

        @property:Column(required = true)
        val shardId: UPID = UPIDs.parse(UUIDSamples.sampleUUID2),

        @property:Column(required = true)
        val encmode: String = "a"
) : EntityWithId<Long> {
        override fun isPersisted(): Boolean = id > 0
}