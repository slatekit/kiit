package slatekit.common

/**
 * Used to represent the source / origin of a request/item being processed
 */
object Sources {
    /**
     * Reference to a parent value
     * e.g. If set on Action, this refers to its parent API
     */
    const val Parent = "@parent"

    /**
     * Enables all protocols
     */
    const val All = "*"

    /**
     * API : HTTP for standard web/rest requests
     */
    const val Api = "api"

    /**
     * Auto : Automation requests
     */
    const val Auto = "auto"

    /**
     * Bot : Bots/Chat
     */
    const val Bot = "bot"

    /**
     * CLI : Command Line Interactive
     */
    const val CLI = "cli"

    /**
     * Commands
     */
    const val Cmd = "cmd"

    /**
     * File based : Requests processed from a file, e.g. for automation
     */
    const val File = "file"

    /**
     * Queue based : requests saved and processed from a queue
     */
    const val Queue = "queue"

    /**
     * Web : HTTP for standard web/rest requests
     */
    const val Web = "web"
}

/**
 * Used to represent the source / origin of a request/item being processed
 */
sealed class Source(val id: String) {

    object All   : Source(Sources.All)   // reference to all/any
    object Parent: Source(Sources.Parent)// reference to parent source
    object API   : Source(Sources.Api)   // generic api usage
    object Auto  : Source(Sources.Auto)  // automation
    object Bot   : Source(Sources.Bot)   // chat / bots
    object CLI   : Source(Sources.CLI)   // command line interaction
    object Cmd   : Source(Sources.Cmd)   // commands / functions
    object File  : Source(Sources.File)  // files / scripts
    object Queue : Source(Sources.Queue) // queues
    object Web   : Source(Sources.Web)   // Web
    data class Other(val name: String) : Source("other")

    fun isParentReference():Boolean {
        return this.id == Sources.Parent
    }


    fun orElse(other: Source): Source {
        return if (isParentReference()) other else this
    }

    companion object {

        fun parse(name:String): Source {
            return when(name) {
                Parent.id -> Parent
                All.id -> All
                API.id -> API
                API.id -> API
                Auto.id -> Auto
                Bot.id -> Bot
                CLI.id -> CLI
                Cmd.id -> Cmd
                File.id -> File
                Queue.id -> Queue
                Web.id -> Web
                else      -> Other(name)
            }
        }
    }
}