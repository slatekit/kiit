/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common

/**
 * Similar to @see[kotlin.TODO] but a type-safe way to track Notes, and this does not throw exceptions
 */
object NOTE {

    private var logger: ((String) -> Unit)? = null

    /**
     * sets the logger for messages
     */
    @JvmStatic fun CONFIGURE(logger: ((String) -> Unit)?) { NOTE.logger = logger }

    /**
     * Indicates that code should be removed at some point
     * @param tag
     * @param msg
     * @param callback
     */
    @JvmStatic fun REMOVE(tag: String = "", msg: String = "", callback: (() -> Unit)? = null) {
        exec("NOTE.REMOVE: $msg", tag, callback)
    }

    /**
     * Indicates that an implementation is required
     * @param tag
     * @param msg
     * @param callback
     */
    @JvmStatic fun IMPLEMENT(tag: String = "", msg: String = "", callback: (() -> Unit)? = null) {
        exec("NOTE.IMPLEMENT: $msg", tag, callback)
    }

    /**
     * Indicates that an improvement is required
     * @param tag
     * @param msg
     * @param callback
     */
    @JvmStatic fun IMPROVE(tag: String = "", msg: String = "", callback: (() -> Unit)? = null) {
        exec("NOTE.IMPROVE: $msg", tag, callback)
    }

    /**
     * Indicates that a refactoring is required
     * @param tag
     * @param msg
     * @param callback
     */
    @JvmStatic fun REFACTOR(tag: String = "", msg: String = "", callback: (() -> Unit)? = null) {
        exec("NOTE.REFACTOR: $msg", tag, callback)
    }

    /**
     * Indicates a bug/potential bug
     * @param tag
     * @param msg
     * @param bugId
     * @param callback
     */
    @JvmStatic fun BUG(tag: String = "", msg: String = "", bugId: String = "", callback: (() -> Unit)? = null) {
        exec("NOTE.BUG: $bugId - $msg", tag, callback)
    }

    /**
     * Indicates a reference to a specific work ticket ( e.g. JIRA )
     * @param id
     * @param msg
     * @param callback
     */
    @JvmStatic fun TICKET(id: String = "", msg: String = "", callback: (() -> Unit)? = null) {
        exec("NOTE.TICKET: $msg", id, callback)
    }

    private fun exec(msg: String, tag: String, callback: (() -> Unit)? = null) {
        logger?.let { log ->
            log("$tag:$msg")
        }
        callback?.invoke()
    }
}
