package slatekit.data.sql

import slatekit.common.data.BuildMode
import slatekit.common.data.Command

/**
 * Query Builders
 */
class Builders {
    class Delete(table:String,
                 converter: ((String) -> String)? = null,
                 encoder:((String) -> String)? = null) : slatekit.query.Delete() {
        init { fromTable = table }

        override fun build(mode: BuildMode): Command {
            if(conditions.isEmpty()){
                return Command("", listOf(), listOf())
            }
            return Command("", listOf(), listOf())
        }
    }


    class Select(table:String,
                 converter: ((String) -> String)? = null,
                 encoder:((String) -> String)? = null) : slatekit.query.Select() {
        init { fromTable = table }

        override fun build(mode: BuildMode): Command {
            return Command("", listOf(), listOf())
        }
    }


    class Patch(table:String,
                converter: ((String) -> String)? = null,
                encoder:((String) -> String)? = null) : slatekit.query.Update() {
        init { fromTable = table }

        override fun build(mode: BuildMode): Command {
            return Command("", listOf(), listOf())
        }
    }
}
