package slatekit.common.db

import slatekit.common.Record

interface Mapper {

    /**
     * Maps all the parameters to a class that takes in all parameters in the constructor
     * This is ideally for Case Classes, allowing the representation of models as immutable
     * case classes
     * @param record
     * @return
     */
    fun <T> mapFrom(record: Record): T?
}