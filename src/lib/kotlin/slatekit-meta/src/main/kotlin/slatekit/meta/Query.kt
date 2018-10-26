package slatekit.meta

import slatekit.common.query.IQuery

fun IQuery.where(field: kotlin.reflect.KProperty<*>, compare: kotlin.String, fieldValue: kotlin.Any): IQuery {
    return this.where(field.name, compare, fieldValue)
}

fun IQuery.and(field: kotlin.reflect.KProperty<*>, compare: String, fieldValue: Any): IQuery {
    return this.and(field.name, compare, fieldValue)
}

fun IQuery.or(field: kotlin.reflect.KProperty<*>, compare: String, fieldValue: Any): IQuery {
    return this.or(field.name, compare, fieldValue)
}