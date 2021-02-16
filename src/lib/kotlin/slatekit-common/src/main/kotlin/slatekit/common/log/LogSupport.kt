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

package slatekit.common.log

import slatekit.common.Ignore
import slatekit.common.newline
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Log methods with messages that are both eager and lazyily called via functions
 * NOTE: This @Ignore attribute is used to avoid these methods in any meta/reflection processing
 */
interface LogSupport {

    val logger: Logger?

    /** =====================================================================
     * Logging using string with optional args for formatting
     *
     * log.error("updating user {0}", user.id )
     * ======================================================================
     */
    @Ignore fun debug(msg: String?, vararg args:Any?) = log(LogLevel.Debug, null, msg, *args)
    @Ignore fun info (msg: String?, vararg args:Any?) = log(LogLevel.Info , null, msg, *args)
    @Ignore fun warn (msg: String?, vararg args:Any?) = log(LogLevel.Warn , null, msg, *args)
    @Ignore fun error(msg: String?, vararg args:Any?) = log(LogLevel.Error, null, msg, *args)
    @Ignore fun fatal(msg: String?, vararg args:Any?) = log(LogLevel.Fatal, null, msg, *args)

    /** =====================================================================
     * Logging using exceptions + messages
     *
     * log.error( ex, "upating user {0}", user.id )
     * ======================================================================
     */
    @Ignore fun debug(ex:Exception?, msg: String?, vararg args:Any?) = log(LogLevel.Debug, ex, msg, *args)
    @Ignore fun info (ex:Exception?, msg: String?, vararg args:Any?) = log(LogLevel.Info , ex, msg, *args)
    @Ignore fun warn (ex:Exception?, msg: String?, vararg args:Any?) = log(LogLevel.Warn , ex, msg, *args)
    @Ignore fun error(ex:Exception?, msg: String?, vararg args:Any?) = log(LogLevel.Error, ex, msg, *args)
    @Ignore fun fatal(ex:Exception?, msg: String?, vararg args:Any?) = log(LogLevel.Fatal, ex, msg, *args)

    /** =====================================================================
     * Logging using exceptions only
     *
     * log.error( ex )
     * ======================================================================
     */
    @Ignore fun debug(ex:Exception?) = log(LogLevel.Debug, ex, null)
    @Ignore fun info (ex:Exception?) = log(LogLevel.Info , ex, null)
    @Ignore fun warn (ex:Exception?) = log(LogLevel.Warn , ex, null)
    @Ignore fun error(ex:Exception?) = log(LogLevel.Error, ex, null)
    @Ignore fun fatal(ex:Exception?) = log(LogLevel.Fatal, ex, null)

    /** =====================================================================
     * Lazy logging
     *
     * log.error( "updating user" ) { " some expensive message to build" }
     * ======================================================================
     */
    @Ignore fun debug(msg: String? = null, callback: () -> String) = log(LogLevel.Debug, msg, callback)
    @Ignore fun info (msg: String? = null, callback: () -> String) = log(LogLevel.Info , msg, callback)
    @Ignore fun warn (msg: String? = null, callback: () -> String) = log(LogLevel.Warn , msg, callback)
    @Ignore fun error(msg: String? = null, callback: () -> String) = log(LogLevel.Error, msg, callback)
    @Ignore fun fatal(msg: String? = null, callback: () -> String) = log(LogLevel.Fatal, msg, callback)

    /** =====================================================================
     * Structured logging ( key-value pairs )
     *
     * log.error( "updating user", listOf( "user_id" to "abc123", "promo-code" to "xyz-111" ) )
     * ======================================================================
     */
    @Ignore fun debug(msg: String, pairs:List<Pair<String, Any?>>) = log(LogLevel.Debug, "$msg : ${format(pairs)}")
    @Ignore fun info (msg: String, pairs:List<Pair<String, Any?>>) = log(LogLevel.Info , "$msg : ${format(pairs)}")
    @Ignore fun warn (msg: String, pairs:List<Pair<String, Any?>>) = log(LogLevel.Warn , "$msg : ${format(pairs)}")
    @Ignore fun error(msg: String, pairs:List<Pair<String, Any?>>) = log(LogLevel.Error, "$msg : ${format(pairs)}")
    @Ignore fun fatal(msg: String, pairs:List<Pair<String, Any?>>) = log(LogLevel.Fatal, "$msg : ${format(pairs)}")

    /**
     * Logs an entry
     * @param level
     * @param msg
     * @param ex
     */
    @Ignore
    fun log(level: LogLevel, ex: Exception?, msg: String?, vararg args:Any?) {
        var fmsg = msg
        val hasMsg = !msg.isNullOrEmpty()
        val hasArgs = args.isNotEmpty()
        if(hasMsg && hasArgs) {
            fmsg = format(msg ?: "", args)
        }
        log(level, fmsg, ex)
    }

    /**
     * Logs key/value pairs
     */
    @Ignore
    fun log(level: LogLevel, ex:Exception?, msg: String?, pairs:List<Pair<String,String>>) {
        val info = pairs.joinToString { it -> it.first + "=" + it.second }
        log(level, "$msg $info", ex)
    }

    /**
     * Logs an entry
     * @param level
     * @param msg
     * @param ex
     */
    @Ignore
    fun log(level: LogLevel, msg: String?, ex: Exception? = null) {
        val hasMsg = !msg.isNullOrEmpty()
        val hasEx = ex != null
        var fmsg = msg
        if(!hasMsg && hasEx) fmsg = ex?.message
        if(hasMsg && hasEx) fmsg += newline + ex?.message
        logger?.let { l -> l.performLog(level, fmsg, ex) }
    }


    @Ignore
    fun log(level: LogLevel, msg:String?, callback: () -> String) {
        logger?.let { l -> l.performLog(level, msg, callback) }
    }


    fun trace(t:Throwable?):String {
        val sw = StringWriter()
        t?.printStackTrace(PrintWriter(sw))
        val trace = sw.toString()
        return trace
    }


    fun format(msg:String, args:Array<out Any?>):String = msg.format(*args)

    /**
     * Format key/value pairs into "structured value"
     * e.g. a=1, b=2, c=3 etc for easier searches in logs
     * NOTE: Logs can be configured to output JSON and/or provide structured arguments.
     * This varies from logging provider so this is an easier text/classic only way to do ( for now )
     */
    fun format(pairs:List<Pair<String, Any?>>):String  {
        return LogUtils.format(pairs)
    }
}
