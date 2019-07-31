package slatekit.common.ids

import java.util.*

interface Identity {
    val area:String
    val name:String
    val uuid:String
    val fullName:String
}


/**
 * Simple Identity with just name / uuid.
 * This is used to identity sources / components such as :
 * 1. Sender of notifications
 * 2. Worker for background tasks
 */
data class SimpleIdentity(
        override val area:String,
        override val name:String,
        override val uuid:String = UUID.randomUUID().toString()) : Identity {

    override val fullName:String = "$area-$name-$uuid"
}