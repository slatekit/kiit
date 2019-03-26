package slatekit.common.utils

class StringParser(private val content: String) {

    private val extracts = mutableMapOf<String, String>()
    private var pos = 0
    private var lastMatch = false

    fun extracts(): MutableMap<String, String> = extracts

    fun saveUntil(token: String, name: String, ensure: Boolean = true): StringParser {
        val start = pos
        moveInternal(token, ensure)
        if (lastMatch) {
            val end = pos - token.length
            val content = extract(start, end)
            extracts.put(name, content)
        }
        return this
    }

    fun moveTo(token: String, ensure: Boolean = true): StringParser {
        moveInternal(token, ensure)
        return this
    }

    fun moveInternal(token: String, ensure: Boolean = true): StringParser {
        val ndxMatch = content.indexOf(token, pos)
        if (ensure && ndxMatch < 0) {
        } else {
            lastMatch = ndxMatch >= 0

            if (ndxMatch >= 0) {
                // Update pos to next position
                pos = ndxMatch + token.length
            }
        }
        return this
    }

    fun extract(start: Int, end: Int): String = content.substring(start, end)
}
