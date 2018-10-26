package slatekit.common.naming

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
