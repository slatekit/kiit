package slatekit.common.db

import slatekit.common.records.Record
import kotlin.reflect.KClass

interface Mapper {
    /**
     * Creates the entity/model expecting a 0 parameter constructor
     * @return
     */
    fun createEntity(): Any?

    /**
     * Creates the entity/model with all the supplied constructor parameters (ideal for case classes)
     * @param args
     * @return
     */
    fun createEntityWithArgs(cls: KClass<*>, args: List<Any?>?): Any

    /**
     * Maps all the parameters to a class that takes in all parameters in the constructor
     * This is ideally for Case Classes, allowing the representation of models as immutable
     * case classes
     * @param record
     * @return
     */
    fun mapFrom(record: Record): Any?

    /**
     * Maps all the parameters to a class that takes in all parameters in the constructor
     * This is ideally for Case Classes, allowing the representation of models as immutable
     * case classes
     * @param record
     * @return
     */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun mapFromToValType(record: Record): Any?

    /**
     * Maps all the parameters to a class that supports vars as fields.
     * While this is NOT recommended, it is still supported.
     * case classes
     * @param record
     * @return
     */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun mapFromToVarType(record: Record): Any?
}