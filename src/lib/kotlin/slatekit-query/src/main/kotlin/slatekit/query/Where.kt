package slatekit.query

import slatekit.common.data.Command


/**
 * Interface for building a delete statement with criteria
 */
open class Where(converter: ((String) -> String)? = null,
                  encoder:((String) -> String)? = null)
    : CriteriaBase<Where>(converter, encoder), Stmt {

    /**
     * Builds the select command
     */
    override fun build(): Command {
        if(conditions.isEmpty()){
            return Command("", listOf(), listOf())
        }
        return Command("", listOf(), listOf())
    }
}
