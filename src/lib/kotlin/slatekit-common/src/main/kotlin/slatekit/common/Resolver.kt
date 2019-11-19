package slatekit.common

interface Resolver {
    fun <T> contains(cls:String):Boolean
    fun <T> obtain(cls:String): T
    fun <T> obtain(cls:String, name: String): T
}