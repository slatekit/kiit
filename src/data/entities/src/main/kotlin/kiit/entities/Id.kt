package kiit.entities

/**
 * Used to annotate a field as an Id ( primary key with optional name
 * and whether or not it should be auto-generated
 */
@Target(AnnotationTarget.CLASS)
annotation class Table(
    val name:String = "",
    val schema:String = ""
)


/**
 * Used to annotate a field as an Id ( primary key with optional name
 * and whether or not it should be auto-generated
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Id(
    val generated:Boolean = true,
    val name:String = ""
)




/**
 * Used to annotate a field as Persisted/Model field.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Column(
    val name: String = "",
    val desc: String = "",
    val required: Boolean = true,
    val unique: Boolean = false,
    val updatable: Boolean = true,
    val indexed: Boolean = false,
    val length: Int = 0,
    val defaultVal: String = "",
    val encrypt: Boolean = false,
    val example: String = ""
)
