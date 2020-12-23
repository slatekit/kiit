package slatekit.serialization

import slatekit.common.EnumLike
import slatekit.common.types.Content
import slatekit.common.types.ContentType
import slatekit.common.types.ContentTypeCsv
import slatekit.common.types.ContentTypeJson
import slatekit.meta.Reflector
import slatekit.meta.kClass

object Serialization {
    fun csv(isoDates: Boolean = false): SerializerCsv = SerializerCsv(this::serializeObject, isoDates)
    fun json(isoDates: Boolean = false): SerializerJson = SerializerJson(this::serializeObject, isoDates)
    fun props(prettyPrint: Boolean = false, isoDates: Boolean = false): SerializerProps = SerializerProps(prettyPrint, this::serializeObject, isoDates)
    fun sampler(isoDates: Boolean = false): Serializer = SerializerSample(this::serializeObject, isoDates)


    fun serialize(item:Any?, type: ContentType): Content {
        return when(type){
            ContentTypeCsv  -> Content.csv (csv().serialize(item))
            ContentTypeJson -> Content.json(json().serialize(item))
            else            -> Content.prop(props().serialize(item))
        }
    }

    /**
     * recursive serialization for a object.
     *
     * @param item: The object to serialize
     * @param serializer: The serializer to serialize a value to a string
     * @param delimiter: The delimiter to use between key/value pairs
     */
    fun serializeObject(serializer: Serializer, item: Any, depth: Int) {

        // Handle enum
        if (Reflector.isSlateKitEnum(item.kClass)) {
            val enumVal = (item as EnumLike).value
            serializer.serializeValue(enumVal, depth)
            return
        }

        // Begin
        serializer.onContainerStart(item, Serializer.ParentType.OBJECT_TYPE, depth)

        // Get fields
        val fields = Reflector.getProperties(item.kClass)

        // Standardize the display of the props
        val maxLen = if (serializer.standardizeWidth) {
            fields.maxBy { it.name.length }?.name?.length ?: 0
        } else {
            0
        }

        fields.forEachIndexed { index, field ->
            // Get name/value
            val propName = field.name.trim()

            // Standardized width
            val finalPropName = if (serializer.standardizeWidth) {
                propName.padEnd(maxLen)
            } else {
                propName
            }
            val value = Reflector.getFieldValue(item, propName)

            // Entry
            serializer.onMapItem(item, depth, index, finalPropName, value)
        }

        // End
        serializer.onContainerEnd(item, Serializer.ParentType.OBJECT_TYPE, depth)
    }
}
