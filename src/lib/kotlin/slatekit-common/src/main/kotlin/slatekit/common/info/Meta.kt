package slatekit.common.info

interface Meta {

    fun each(callback: (String, String) -> Unit) = props().forEach { callback(it.first, it.second) }

    fun props():List<Pair<String,String>>
}