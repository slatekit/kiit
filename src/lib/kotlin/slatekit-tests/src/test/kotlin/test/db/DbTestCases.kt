package test.db

import kiit.common.data.IDb


interface DbTestCases {
    fun can_build()
    fun can_insert_sql_raw()
    fun can_insert_sql_prep()
    fun can_update()
    fun can_delete()
    fun can_get()

    fun can_query_scalar_string()
    fun can_query_scalar_bool()
    fun can_query_scalar_short()
    fun can_query_scalar_int()
    fun can_query_scalar_long()
    fun can_query_scalar_float()
    fun can_query_scalar_double()
    fun can_query_scalar_localdate()
    fun can_query_scalar_localtime()
    fun can_query_scalar_localdatetime()
    fun can_query_scalar_date()

    fun <T> ensure_scalar(colName: String, callback: (IDb, String) -> T, expected: T): Unit
}
