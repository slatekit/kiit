package slatekit.meta.models

/**
 * Used to annotate a field as an Id ( primary key with optional name
 * and whether or not it should be auto-generated
 */
annotation class Id(
        val generated:Boolean = true,
        val name:String = ""
)