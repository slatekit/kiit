package slatekit.common.info

interface Info {

    fun each(callback: (String, String) -> Unit) = props().forEach { callback(it.first, it.second) }

    fun props():List<Pair<String,String>>
}