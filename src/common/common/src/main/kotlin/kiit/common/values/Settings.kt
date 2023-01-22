package kiit.common.values

/**
 * Provides both gets ( Reads ) / puts ( Writes ) on configurable settings
 */
interface Settings : Inputs, Puts {

    /**
     * Convenience method to initiate and complete editing of settings
     */
    fun edit(op: () -> Unit) {
        init()
        op()
        done()
    }

    /**
     * Initiates edits to the settings.
     * This is to comply ( this ends up working nicely w/ Android SharedPrefs
     */
    fun init()

    /**
     * Designates completion of editing settings
     */
    fun done()

    fun put(key: String, value: String, overwrite: Boolean = false) {
        if (!containsKey(key) || overwrite) {
            putString(key, value)
        }
    }
}
