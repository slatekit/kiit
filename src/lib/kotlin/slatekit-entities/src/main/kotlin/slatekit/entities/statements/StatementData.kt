package slatekit.entities.statements

import slatekit.common.data.Values

/**
 * Stores the result of a built statement.
 * @param sql   : Sql statement:     e.g. "select * from `movies` where `id` = ?;"
 * @param pairs : Key/Value pairs    e.g. List<Value>(Value("id", 2))
 * @param values: Just the values    e.g. List<Any?>(2)
 */
data class StatementData(val sql:String, val pairs:Values, val values:List<Any?>)
