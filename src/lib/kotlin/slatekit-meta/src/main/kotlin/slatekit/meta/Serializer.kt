package slatekit.meta

import slatekit.common.Reflector
import slatekit.common.Serial
import slatekit.common.kClass


/**
 * recursive serialization for a object.
 *
 * @param item: The object to serialize
 * @param serializer: The serializer to serialize a value to a string
 * @param delimiter: The delimiter to use between key/value pairs
 */
fun serializeObject(serializer: Serial, item: Any, depth: Int): Unit {

    // Begin
    serializer.onContainerStart(item, Serial.ParentType.OBJECT_TYPE, depth)

    // Get fields
    val fields = Reflector.getProperties(item.kClass)

    // Standardize the display of the props
    val maxLen = if (serializer.standardizeWidth) {
        fields.maxBy { it.name.length }?.name?.length ?: 0
    }
    else {
        0
    }

    fields.forEachIndexed { index, field ->
        // Get name/value
        val propName = field.name.trim()

        // Standardized width
        val finalPropName = if (serializer.standardizeWidth) {
            propName.padEnd(maxLen)
        }
        else {
            propName
        }
        val value = Reflector.getFieldValue(item, propName)

        // Entry
        serializer.onMapItem(item, depth, index, finalPropName, value)
    }

    // End
    serializer.onContainerEnd(item, Serial.ParentType.OBJECT_TYPE, depth)
}