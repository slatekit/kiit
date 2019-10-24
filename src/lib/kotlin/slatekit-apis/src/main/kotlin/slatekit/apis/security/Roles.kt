package slatekit.apis.security

data class Roles(val all:List<String>) {
    companion object {
        val empty = Roles(listOf())
    }
}