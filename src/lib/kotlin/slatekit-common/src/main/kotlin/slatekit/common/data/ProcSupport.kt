package slatekit.common.data

import slatekit.common.Record
import java.sql.ResultSet

interface ProcSupport {

    /**
     * Calls a stored procedure
     * @param procName : The name of the stored procedure e.g. get_by_id
     * @param callback : The callback to handle the resultset
     * @param moveNext : Whether or not to automatically move the resultset to the next/first row
     * @param inputs : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    fun <T> callQuery(
            procName: String,
            callback: (ResultSet) -> T?,
            moveNext: Boolean = true,
            inputs: List<Value>? = null
    ): T?

    /**
     * Calls a stored procedure
     * @param procName : The name of the stored procedure e.g. get_by_id
     * @param inputs : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    fun <T> callQueryMapped(
            procName: String,
            mapper: (Record) -> T?,
            inputs: List<Value>? = null
    ): List<T>?

    /**
     * Calls a stored procedure to issue an insert
     * @param procName : The name of the stored procedure e.g. createUser
     * @param inputs : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    fun callCreate(procName: String, inputs: List<Value>? = null): String

    /**
     * Calls a stored procedure
     * @param procName : The name of the stored procedure e.g. get_by_id
     * @param inputs : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    fun callUpdate(procName: String, inputs: List<Value>? = null): Int
}