package slatekit.query

/**
 * Interface for building a delete statement with criteria
 */
abstract class Delete(converter: ((String) -> String)? = null,
                      encoder:((String) -> String)? = null)
    : CriteriaBase<Delete>(converter, encoder), Stmt {
}
