package slatekit.common


object Ioc {
    val lookup = mutableMapOf<String, (String) -> Any>()

    fun register(name:String, creator:(String) -> Any) : Unit {
        lookup.put(name, creator)
    }


    fun <T> get(name:String): T {
        val creator = lookup.get(name)!!
        val instance = creator.invoke(name) as T
        return instance
    }


    fun  contains(name:String): Boolean {
       return lookup.containsKey(name)
    }
}