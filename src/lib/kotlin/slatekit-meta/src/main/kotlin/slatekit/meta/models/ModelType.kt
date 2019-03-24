package slatekit.meta.models

import kotlin.reflect.KClass

sealed class ModelType {
    data class ModelJavaType(val cls:Class<*>)
    data class ModelKotlinType(val cls: KClass<*>)
}