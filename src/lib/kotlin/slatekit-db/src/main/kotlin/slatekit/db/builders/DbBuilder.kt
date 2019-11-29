/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package slatekit.db.builders

import slatekit.common.db.DbType

/**
 * Created by kishorereddy on 6/14/17.
 */

interface DbBuilder {

    /**
     * Builds the drop table DDL for the name supplied.
     */
    fun dropTable(name: String): String

    /**
     * Builds an add column DDL sql statement
     */
    fun addCol(name: String, dataType: DbType, required: Boolean = false, maxLen: Int = 0): String

    /**
     * Builds a valid column name
     */
    fun colName(name: String): String

    /**
     * Builds a valid column type
     */
    fun colType(colType: DbType, maxLen: Int): String

    /**
     * Builds a delete statement to delete all rows
     */
    fun truncate(name: String): String
}
