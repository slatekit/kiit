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
package slatekit.common.db.types


import slatekit.common.db.DbFieldType

/**
 * Created by kishorereddy on 6/14/17.
 */

interface DbSource {

    /**
     * Builds the drop table DDL for the name supplied.
     */
    fun buildDropTable(name: String): String

    /**
     * Builds an add column DDL sql statement
     */
    fun buildAddCol(name: String, dataType: DbFieldType, required: Boolean = false, maxLen: Int = 0): String

    /**
     * Builds a valid column name
     */
    fun buildColName(name: String): String

    /**
     * Builds a valid column type
     */
    fun buildColType(colType: DbFieldType, maxLen: Int): String

    /**
     * Builds a delete statement to delete all rows
     */
    fun buildDeleteAll(name: String): String
}
