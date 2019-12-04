package slatekit.meta.models


sealed class FieldType {

    // Bool
    object typeBool    : FieldType()

    // Chars/Strings
    object typeChar    : FieldType()
    object typeString  : FieldType()
    object typeText    : FieldType()

    // Numbers
    object typeShort   : FieldType()
    object typeInt     : FieldType()
    object typeLong    : FieldType()
    object typeFloat   : FieldType()
    object typeDouble  : FieldType()
    object typeDecimal : FieldType()

    // Dates
    object typeLocalDate     : FieldType()
    object typeLocalTime     : FieldType()
    object typeLocalDateTime : FieldType()
    object typeZonedDateTime : FieldType()
    object typeInstant       : FieldType()
    object typeDateTime      : FieldType()

    // Enum
    object typeEnum : FieldType()
    object typeUUID : FieldType()
    object typeUnique : FieldType()

    // Complex/Object
    object typeObject : FieldType()
}
