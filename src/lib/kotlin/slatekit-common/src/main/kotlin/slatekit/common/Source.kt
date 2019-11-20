package slatekit.common

/**
 * Used to represent the source / origin of a request/item being processed
 */
object Sources {
    /**
     * Reference to a parent value
     * e.g. If set on Action, this refers to its parent API
     */
    const val PARENT = "@parent"

    /**
     * Enables all protocols
     */
    const val ALL = "*"

    /**
     * API : HTTP for standard web/rest requests
     */
    const val API = "api"

    /**
     * Auto : Automation requests
     */
    const val AUTO = "auto"

    /**
     * Bot : Bots/Chat
     */
    const val BOT = "bot"

    /**
     * CLI : Command Line Interactive
     */
    const val CLI = "cli"

    /**
     * Commands
     */
    const val CMD = "cmd"

    /**
     * File based : Requests processed from a file, e.g. for automation
     */
    const val FILE = "file"

    /**
     * Queue based : requests saved and processed from a queue
     */
    const val QUEUE = "queue"

    /**
     * Stream based : requests saved and processed from a queue
     */
    const val STREAM = "stream"

    /**
     * Web : HTTP for standard web/rest requests
     */
    const val WEB = "web"
}

/**
 * Used to represent the source / origin of a request/item being processed
 */
sealed class Source(val id: String) {

    object All   : Source(Sources.ALL)   // reference to all/any
    object Parent: Source(Sources.PARENT)// reference to parent source
    object API   : Source(Sources.API)   // generic api usage
    object Auto  : Source(Sources.AUTO)  // automation
    object Bot   : Source(Sources.BOT)   // chat / bots
    object CLI   : Source(Sources.CLI)   // command line interaction
    object Cmd   : Source(Sources.CMD)   // commands / functions
    object File  : Source(Sources.FILE)  // files / scripts
    object Queue : Source(Sources.QUEUE) // queues
    object Stream: Source(Sources.STREAM)// queues
    object Web   : Source(Sources.WEB)   // Web
    data class Other(val name: String) : Source("other")

    fun isParentReference():Boolean {
        return this.id == Sources.PARENT
    }


    fun orElse(other: Source): Source {
        return if (isParentReference()) other else this
    }

    companion object {

        fun parse(name:String): Source {
            return when(name) {
                Parent.id -> Parent
                All.id    -> All
                API.id    -> API
                Auto.id   -> Auto
                Bot.id    -> Bot
                CLI.id    -> CLI
                Cmd.id    -> Cmd
                File.id   -> File
                Queue.id  -> Queue
                Stream.id -> Stream
                Web.id    -> Web
                else      -> Other(name)
            }
        }
    }
}