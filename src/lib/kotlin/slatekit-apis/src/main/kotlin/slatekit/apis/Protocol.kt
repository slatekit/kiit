package slatekit.apis

import slatekit.apis.setup.Parentable


object Protocols {
    /**
     * Reference to a parent value
     * e.g. If set on Action, this refers to its parent API
     */
    const val Parent = ApiConstants.parent


    /**
     * Enables all protocols
     */
    const val All = "*"


    /**
     * CLI : Command Line Interactive
     */
    const val CLI = "cli"


    /**
     * Web : HTTP for standard web/rest requests
     */
    const val Web = "web"


    /**
     * File based : Requests processed from a file, e.g. for automation
     */
    const val File = "file"


    /**
     * Queue based : requests saved and processed from a queue
     */
    const val Queue = "queue"
}


sealed class Protocol(override val name:String) : Parentable<Protocol>  {
    object Parent : Protocol(Protocols.Parent)
    object All    : Protocol(Protocols.All)
    object CLI    : Protocol(Protocols.CLI)
    object Web    : Protocol(Protocols.Web)
    object File   : Protocol(Protocols.Web)
    object Queue  : Protocol(Protocols.Queue)


    companion object {

        fun parse(name: String): Protocol {
            return when (name) {
                Protocols.Parent -> Protocol.Parent
                Protocols.All -> Protocol.All
                Protocols.CLI -> Protocol.CLI
                Protocols.Web -> Protocol.Web
                Protocols.File -> Protocol.File
                else -> Protocol.Web
            }
        }
    }
}