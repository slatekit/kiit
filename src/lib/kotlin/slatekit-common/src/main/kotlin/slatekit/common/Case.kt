package slatekit.common

/**
 * LowerCamel       lowerCamel
 * UpperCamel       UpperCamel
 * LowerHyphen      lower-hyphen
 * UpperHyphen      UPPER-HYPHEN
 * LowerUnderscore	lower_underscore
 * UpperUnderscore	UPPER_UNDERSCORE
 */
interface Case {
    val text: String
}


object Cases {
    val hyphenReplacements = setOf(' ', '_')
    val camelReplacements  = setOf(' ', '-', '_')
    val underScoreReplacements = setOf(' ', '-')
}

data class LowerCamel     (override val text: String) : Case
data class UpperCamel     (override val text: String) : Case
data class LowerHyphen    (override val text: String) : Case
data class UpperHyphen    (override val text: String) : Case
data class LowerUnderscore(override val text: String) : Case
data class UpperUnderscore(override val text: String) : Case



interface Namer {
    fun rename(text:String):String
    fun convert(text:String): Case
}


class LowerCamelNamer : Namer {
    override fun rename(text:String):String = convertToCamel(text, false, Cases.camelReplacements)
    override fun convert(text:String): Case = LowerCamel(rename(text))
}


class UpperCamelNamer : Namer {
    override fun rename(text:String):String = convertToCamel(text, true, Cases.camelReplacements)
    override fun convert(text:String): Case = UpperCamel(rename(text))
}


class LowerHyphenNamer : Namer {
    override fun rename(text: String):String = convertToCase(text, false, Cases.hyphenReplacements, '-')
    override fun convert(text:String): Case = LowerHyphen(rename(text))
}


class UpperHyphenNamer : Namer {
    override fun rename(text: String): String = convertToCase(text, true, Cases.hyphenReplacements, '-')
    override fun convert(text:String): Case = UpperHyphen(rename(text))
}


class LowerUnderscoreNamer : Namer {
    override fun rename(text: String): String = convertToCase(text, false, Cases.underScoreReplacements, '_')
    override fun convert(text:String): Case = LowerUnderscore(rename(text))
}


class UpperUnderscoreNamer : Namer {
    override fun rename(text: String): String = convertToCase(text, true, Cases.underScoreReplacements, '_')
    override fun convert(text:String): Case = UpperUnderscore(rename(text))
}


fun convertToCase(text: String,  upper:Boolean, replacements:Set<Char>, replacement:Char): String {
    return text.foldIndexed("", { ndx, acc, c ->
        val currentCharIsUpper = c.isLetter() && c.toUpperCase() == c
        val ch:String = if(replacements.contains(c)) {
            replacement.toString()
        }
        else if( ndx > 0 && text[ndx-1].toLowerCase() == text[ndx-1] && currentCharIsUpper) {
            replacement.toString() + ( if(upper) c.toUpperCase() else c.toLowerCase())
        }
        else {
            if(upper) c.toUpperCase().toString() else c.toLowerCase().toString()
        }
        acc + ch
    })
}


fun convertToCamel(text: String, upper:Boolean, replacements:Set<Char>): String {
    return text.foldIndexed("", { ndx, acc, c ->
        val isUpper = c.toUpperCase() == c
        val ch = if(replacements.contains(c)) {
            Char.MIN_SURROGATE
        }
        else if(ndx == 0 && upper) {
            c.toUpperCase()
        }
        else if (ndx == 0 && !upper) {
            c.toLowerCase()
        }
        else if( ndx > 0 && replacements.contains(text[ndx-1])) {
            c.toUpperCase()
        }
        else if( ndx > 0 && text[ndx-1].toLowerCase() == text[ndx-1] && isUpper) {
            c.toUpperCase()
        }
        else {
            c
        }

        if (ch == Char.MIN_SURROGATE) acc else acc + ch
    })
}