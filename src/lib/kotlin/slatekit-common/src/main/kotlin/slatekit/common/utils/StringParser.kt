package slatekit.common.utils

class StringParser(private val _content: String) {

    private val _extracts = mutableMapOf<String, String>()
    private var _pos = 0
    private var _lastMatch = false

    fun extracts(): MutableMap<String, String> = _extracts

    fun saveUntil(token: String, name: String, ensure: Boolean = true): StringParser
    {
        val start = _pos
        moveInternal(token, ensure)
        if (_lastMatch) {
            val end = _pos - token.length
            val content = extract(start, end)
            _extracts.put(name, content)
        }
        return this
    }

    fun moveTo(token: String, ensure: Boolean = true): StringParser
    {
        moveInternal(token, ensure)
        return this
    }

    fun moveInternal(token: String, ensure: Boolean = true): StringParser
    {
        val ndxMatch = _content.indexOf(token, _pos)
        if (ensure && ndxMatch < 0) {
        } else {
            _lastMatch = ndxMatch >= 0

            if (ndxMatch >= 0) {
                // Update pos to next position
                _pos = ndxMatch + token.length
            }
        }
        return this
    }

    fun extract(start: Int, end: Int): String = _content.substring(start, end)
}
