package test.setup

import kiit.common.DateTime
import kiit.common.utils.Random
import kiit.common.ids.UPID
import kiit.common.ids.UPIDs
import kiit.entities.Column
import kiit.entities.EntityWithId
import kiit.entities.Id
import java.util.*

class AuthorW : EntityWithId<Long> {
    override fun isPersisted(): Boolean = id > 0

    @property:Id()
    override var id: Long = 0

    @property:Column(required = true)
    var uuid: String = Random.uuid()

    @property:Column(required = true)
    var createdAt: DateTime = DateTime.now()

    @property:Column(required = true)
    var createdBy: Long = 0

    @property:Column(required = true)
    var updatedAt: DateTime = DateTime.now()

    @property:Column(required = true)
    var updatedBy: Long = 0

    @property:Column(required = true)
    var email: String = ""

    @property:Column(required = true)
    var isActive: Boolean = false

    @property:Column(required = true)
    var age: Int = 35

    @property:Column(required = true)
    var status:StatusEnum = StatusEnum.Pending

    @property:Column(required = true)
    var salary: Double = 20.5

    @property:Column(required = true)
    var uid: UUID = UUID.fromString(UUIDSamples.sampleUUID1)

    @property:Column(required = true)
    var shardId: UPID = UPIDs.parse(UUIDSamples.sampleUUID2)
}