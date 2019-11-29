package slatekit.common.db

import java.sql.ResultSet

interface IDb : ScalarSupport, ProcSupport {
    val onError: (Exception) -> Unit

    /**
     * registers the jdbc driver
     *
     * @return
     */
    fun open(): IDb

    /**
     * Executes the sql provided
     */
    fun execute(sql: String)

    /**
     * executes an insert using the sql or stored proc and gets the id
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The id ( primary key )
     */
    fun insert(sql: String, inputs: List<Any>? = null): Long

    /**
     * executes an insert using the sql or stored proc and gets the id
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The id ( primary key )
     */
    fun insertGetId(sql: String, inputs: List<Any>? = null): String

    /**
     * executes the update sql or stored proc
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The number of affected records
     */
    fun update(sql: String, inputs: List<Any>? = null): Int

    /**
     * Executes a sql query
     * @param sql : The sql to query
     * @param callback : The callback to handle the resultset
     * @param moveNext : Whether or not to automatically move the resultset to the next/first row
     * @param inputs : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    fun <T> query(
            sql: String,
            callback: (ResultSet) -> T?,
            moveNext: Boolean = true,
            inputs: List<Any>? = null
    ): T?

    /**
     * maps a single item using the sql supplied
     *
     * @param sql : The sql
     * @param mapper : THe mapper to map the item of type T
     * @tparam T     : The type of the item
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> mapOne(sql: String, mapper: Mapper<T>, inputs: List<Any>? = null): T?

    /**
     * maps multiple items using the sql supplied
     *
     * @param sql : The sql
     * @param mapper : THe mapper to map the item of type T
     * @tparam T     : The type of the item
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> mapAll(sql: String, mapper: Mapper<T>, inputs: List<Any>? = null): List<T>?


    fun errorHandler(ex: Exception)
}