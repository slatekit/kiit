package slatekit.core.scheduler

import slatekit.common.toId

data class Task(val name:String, val call:() -> Unit) {

    val id = name.toId()
}