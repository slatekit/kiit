package slatekit.query

import slatekit.query.IQuery
import slatekit.query.Query

fun IQuery.where(field: kotlin.reflect.KProperty<*>, compare: kotlin.String, fieldValue: Any?): IQuery {
    val finalValue = fieldValue ?: Query.Null
    return this.where(field.name, compare, finalValue)
}

fun IQuery.and(field: kotlin.reflect.KProperty<*>, compare: String, fieldValue: Any?): IQuery {
    val finalValue = fieldValue ?: Query.Null
    return this.and(field.name, compare, finalValue)
}

fun IQuery.or(field: kotlin.reflect.KProperty<*>, compare: String, fieldValue: Any?): IQuery {
    val finalValue = fieldValue ?: Query.Null
    return this.or(field.name, compare, finalValue)
}