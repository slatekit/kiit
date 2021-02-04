package slatekit.query


fun <T> Criteria<T>.where(field: kotlin.reflect.KProperty<*>, op: Op, fieldValue: Any?): T {
    val finalValue = fieldValue ?: Const.Null
    return this.where(field.name, op, finalValue)
}

fun <T> Criteria<T>.where(field: kotlin.reflect.KProperty<*>, fieldValue: Any?): T {
    val finalValue = fieldValue ?: Const.Null
    return this.where(field.name, Op.Eq, finalValue)
}

fun <T> Criteria<T>.and(field: kotlin.reflect.KProperty<*>, op: Op,  fieldValue: Any?): T {
    val finalValue = fieldValue ?: Const.Null
    return this.and(field.name, op, finalValue)
}

fun <T> Criteria<T>.or(field: kotlin.reflect.KProperty<*>, op: Op, fieldValue: Any?): T {
    val finalValue = fieldValue ?: Const.Null
    return this.or(field.name, op, finalValue)
}

fun Update.set(prop: kotlin.reflect.KProperty<*>, fieldValue: Any?): Update {
    return this.set(prop.name, fieldValue)
}
