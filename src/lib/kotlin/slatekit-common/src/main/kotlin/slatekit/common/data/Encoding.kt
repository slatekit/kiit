package slatekit.common.data

object Encoding {
    /**
     * ensures the text value supplied be escaping single quotes for sql.
     *
     * @param text
     * @return
     */
    @JvmStatic
    fun ensureValue(text: String): String =
            if (text.isNullOrEmpty()) {
                ""
            } else {
                text.replace("'", "''")
            }

    @JvmStatic
    fun ensureField(text: String): String =
            if (text.isNullOrEmpty()) {
                ""
            } else {
                text.toLowerCase().trim().filter { c -> c.isDigit() || c.isLetter() || c == '_' || c == '.' }
            }
}