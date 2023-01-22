package kiit.utils.naming

interface Namers {
    fun rename(text: String): String
    fun convert(text: String): Case
}

class LowerCamelNamer : Namer {
    override fun rename(text: String): String =
        convertToCamel(text, false, Cases.camelReplacements)

    override fun convert(text: String): Case = LowerCamel(rename(text))
}

class UpperCamelNamer : Namer {
    override fun rename(text: String): String =
        convertToCamel(text, true, Cases.camelReplacements)

    override fun convert(text: String): Case = UpperCamel(rename(text))
}

class LowerHyphenNamer : Namer {
    override fun rename(text: String): String =
        convertToCase(text, false, Cases.hyphenReplacements, '-')

    override fun convert(text: String): Case = LowerHyphen(rename(text))
}

class UpperHyphenNamer : Namer {
    override fun rename(text: String): String =
        convertToCase(text, true, Cases.hyphenReplacements, '-')

    override fun convert(text: String): Case = UpperHyphen(rename(text))
}

class LowerUnderscoreNamer : Namer {
    override fun rename(text: String): String =
        convertToCase(text, false, Cases.underScoreReplacements, '_')

    override fun convert(text: String): Case =
        LowerUnderscore(rename(text))
}

class UpperUnderscoreNamer : Namer {
    override fun rename(text: String): String =
        convertToCase(text, true, Cases.underScoreReplacements, '_')

    override fun convert(text: String): Case =
        UpperUnderscore(rename(text))
}
