package slatekit.common.info

interface Info {

    fun each(callback: (Pair<String, String>) -> Unit) = props().forEach(callback)

    fun props():List<Pair<String,String>>
}