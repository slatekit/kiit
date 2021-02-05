package slatekit.query


/**
 * Interface for building a Select statement with criteria
 */
abstract class Select(converter: ((String) -> String)? = null,
                         encoder:((String) -> String)? = null)
    : CriteriaBase<Select>(converter, encoder), Stmt {
    private var agg: Agg? = null
    private val fields = mutableListOf<String>()

    /**
     * Get all fields
     */
    fun all(): Select {
        return this
    }


    fun agg(name:String, field:String): Select {
        agg = Agg(name, field)
        return this
    }
}
