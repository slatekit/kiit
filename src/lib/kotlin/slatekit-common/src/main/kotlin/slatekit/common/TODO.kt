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

object TODO {

    private var logger: ((String) -> Unit)? = null

    /**
     * sets the logger for messages
     */
    @JvmStatic fun CONFIGURE(logger: ((String) -> Unit)?) { this.logger = logger }

    /**
     * Indicates that code is not implemented
     * @param tag
     * @param msg
     * @param callback
     */
    @JvmStatic fun REMOVE(tag: String = "", msg: String = "", callback: (() -> Unit)? = null) {
        exec("TODO(remove): " + msg, tag, callback)
    }

    /**
     * Indicates that code is not implemented
     * @param tag
     * @param msg
     * @param callback
     */
    @JvmStatic fun NOT_IMPLEMENTED(tag: String = "", msg: String = "", callback: (() -> Unit)? = null) {
        exec("TODO(not_implement): " + msg, tag, callback)
    }

    /**
     * Indicates that an implementation is required
     * @param tag
     * @param msg
     * @param callback
     */
    @JvmStatic fun IMPLEMENT(tag: String = "", msg: String = "", callback: (() -> Unit)? = null) {
        exec("TODO(implement): " + msg, tag, callback)
    }

    /**
     * Indicates that an improvement is required
     * @param tag
     * @param msg
     * @param callback
     */
    @JvmStatic fun IMPROVE(tag: String = "", msg: String = "", callback: (() -> Unit)? = null) {
        exec("TODO(improve): " + msg, tag, callback)
    }

    /**
     * Indicates that a refactoring is required
     * @param tag
     * @param msg
     * @param callback
     */
    @JvmStatic fun REFACTOR(tag: String = "", msg: String = "", callback: (() -> Unit)? = null) {
        exec("TODO(refactor): " + msg, tag, callback)
    }

    /**
     * Indicates a bug
     * @param tag
     * @param msg
     * @param bugId
     * @param callback
     */
    @JvmStatic fun BUG(tag: String = "", msg: String = "", bugId: String = "", callback: (() -> Unit)? = null) {
        exec("TODO(bug) $bugId: $msg", tag, callback)
    }

    private fun exec(msg: String, tag: String, callback: (() -> Unit)? = null) {
        logger?.let { log ->
            log("$tag:$msg")
        }
        callback?.invoke()
    }
}
