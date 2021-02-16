package slatekit.common.log

object LogUtils {
    val sensitiveFields = listOf("username", "email", "phone", "password", "pswd", "firstname", "lastname")

    fun keys(name:String, fields:List<Pair<String, Any?>>) {
        val values = fields.joinToString(",") { item -> "${toKey(item.first)}=${item.second}" }
        println("$name : $values")
    }

    /**
     * Format key/value pairs into "structured value"
     * e.g. a=1, b=2, c=3 etc for easier searches in logs
     * NOTE: Logs can be configured to output JSON and/or provide structured arguments.
     * This varies from logging provider so this is an easier text/classic only way to do ( for now )
     */
    fun format(pairs:List<Pair<String, Any?>>):String  {
        // OPTIMIZATION:
        // avoiding map/filter here to avoid unnecessary copies / loops
        return pairs.foldIndexed("") { ndx, acc, info ->
            val key = toKey(info.first)

            // Check sensitive fields like password, email, phone,
            when(!isSensitive(key)){
                true -> {
                    val prefix = if(acc.isBlank()) "" else ", "
                    acc + "$prefix${key}=${info.second}"
                }
                false -> acc
            }
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun toKey(key:String):String = key.trim().toLowerCase().replace(" ", "-")

    @Suppress("NOTHING_TO_INLINE")
    inline fun isSensitive(key:String) : Boolean = sensitiveFields.any { key.contains(it) }
}