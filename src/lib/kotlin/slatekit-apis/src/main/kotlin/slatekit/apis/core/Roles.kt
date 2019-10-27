package slatekit.apis.core


data class Roles(val all:List<String>) {

    val isEmpty = all.isEmpty() || all.size == 1 && all.first() == slatekit.common.auth.Roles.none

    val allowGuest = all.any { it == slatekit.common.auth.Roles.guest }

    val allowAll   = all.any { it == slatekit.common.auth.Roles.all }

    val isAuthed = !isEmpty && !allowGuest

    fun contains(name:String):Boolean = all.contains(name)

    fun orElse(other:Roles):Roles = if(this.isEmpty) other else this


    companion object {
        val empty = Roles(listOf())
    }
}