package slatekit.apis.setup



object Protocols {
    const val all = "*"
    const val cli = "cli"
    const val web = "web"
    const val file = "file"
    const val queue = "queue"
}


sealed class Protocol(val name:String) {
    object All   : Protocol(Protocols.all)
    object CLI   : Protocol(Protocols.cli)
    object Web   : Protocol(Protocols.web)
    object File  : Protocol(Protocols.web)
    object Queue : Protocol(Protocols.queue)
}