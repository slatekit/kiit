package slatekit.meta.models


sealed class ModelFieldType {

    // Bool
    object typeBool    : ModelFieldType()

    // Chars/Strings
    object typeChar    : ModelFieldType()
    object typeString  : ModelFieldType()
    object typeText    : ModelFieldType()

    // Numbers
    object typeShort   : ModelFieldType()
    object typeInt     : ModelFieldType()
    object typeLong    : ModelFieldType()
    object typeFloat   : ModelFieldType()
    object typeDouble  : ModelFieldType()
    object typeDecimal : ModelFieldType()

    // Dates
    object typeLocalDate     : ModelFieldType()
    object typeLocalTime     : ModelFieldType()
    object typeLocalDateTime : ModelFieldType()
    object typeZonedDateTime : ModelFieldType()
    object typeInstant       : ModelFieldType()
    object typeDateTime      : ModelFieldType()

    // Enum
    object typeEnum : ModelFieldType()
    object typeUUID : ModelFieldType()

    // Complex/Object
    object typeObject : ModelFieldType()
}
