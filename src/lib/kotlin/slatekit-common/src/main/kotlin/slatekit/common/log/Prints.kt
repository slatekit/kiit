package slatekit.common.log

object Prints {
    fun keys(name:String, fields:List<Pair<String, Any?>>) {
        val values = fields.joinToString(",") { item -> "${item.first}=${item.second}" }
        println("$name : $values")
    }
}