package meta

import kotlin.reflect.KClass


object ModelHelpers {

    @JvmStatic
    fun model(cls:Class<*>):KClass<*> {
        return cls.kotlin
    }
}