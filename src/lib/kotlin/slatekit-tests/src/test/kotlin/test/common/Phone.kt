package test.common

import slatekit.common.DateTime
import slatekit.entities.core.EntityWithId


data class Phone (

        override val id: Long = 0,

        val number :String = "",

        val os :String = "",

        val uniqueId :String = "",

        val createdAt: DateTime = DateTime.now(),

        val createdBy: Int  = 0,

        val updatedAt : DateTime =  DateTime.now(),

        val updatedBy : Int  = 0

) : EntityWithId
