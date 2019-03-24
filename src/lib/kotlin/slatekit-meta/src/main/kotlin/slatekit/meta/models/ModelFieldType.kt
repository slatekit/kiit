package slatekit.meta.models


sealed class ModelFieldType {

    // Bool
    object TypeBool    : ModelFieldType()

    // Chars/Strings
    object TypeChar    : ModelFieldType()
    object TypeString  : ModelFieldType()
    object TypeText    : ModelFieldType()

    // Numbers
    object TypeShort   : ModelFieldType()
    object TypeInt     : ModelFieldType()
    object TypeLong    : ModelFieldType()
    object TypeFloat   : ModelFieldType()
    object TypeDouble  : ModelFieldType()
    object TypeDecimal : ModelFieldType()

    // Dates
    object TypeLocalDate     : ModelFieldType()
    object TypeLocalTime     : ModelFieldType()
    object TypeLocalDateTime : ModelFieldType()
    object TypeZonedDateTime : ModelFieldType()
    object TypeInstant       : ModelFieldType()
    object TypeDateTime      : ModelFieldType()

    // Enum
    object TypeEnum : ModelFieldType()

    // Complex/Object
    object TypeObject : ModelFieldType()
}
