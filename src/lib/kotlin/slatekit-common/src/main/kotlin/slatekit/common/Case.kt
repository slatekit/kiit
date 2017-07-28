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

data class Namer(val name:String, val f:(String) -> Case) {
    fun name(text:String):Case = f(text)
}

fun lowerHyphen(text: String): LowerHyphen {
    return LowerHyphen(convertToCase(text, false, Cases.hyphenReplacements, '-'))
}


fun upperHyphen(text: String): UpperHyphen {
    return UpperHyphen(convertToCase(text, true, Cases.hyphenReplacements, '-'))
}


fun lowerUnderscore(text: String): LowerUnderscore {
    return LowerUnderscore(convertToCase(text, false, Cases.underScoreReplacements, '_'))
}


fun upperUnderscore(text: String): UpperUnderscore {
    return UpperUnderscore(convertToCase(text, true, Cases.underScoreReplacements, '_'))
}


fun lowerCamel(text: String): LowerCamel = LowerCamel(convertToCamel(text, false, Cases.camelReplacements))


fun upperCamel(text: String): UpperCamel = UpperCamel(convertToCamel(text, true, Cases.camelReplacements))


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