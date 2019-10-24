package slatekit.apis.setup



object Protocols {
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


sealed class Protocol(val name:String) {
    object Parent : Protocol(Protocols.Parent)
    object All    : Protocol(Protocols.All)
    object CLI    : Protocol(Protocols.CLI)
    object Web    : Protocol(Protocols.Web)
    object File   : Protocol(Protocols.Web)
    object Queue  : Protocol(Protocols.Queue)
}