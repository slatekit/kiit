package slatekit.common.requests

sealed class Source(val id: String) {
    object API   : Source("api")   // generic api usage
    object Auto  : Source("auto")  // automation
    object Chat  : Source("bot")   // chat / bots
    object CLI   : Source("cli")   // command line interaction
    object File  : Source("file")  // files / scripts
    object Queue : Source("queue") // queues
    object Web   : Source("web")   // http
    data class Other(val name: String) : Source("other")

    companion object {
        fun parse(name:String): Source {
            return when(name) {
                API   .id -> API
                Auto  .id -> Auto
                Chat  .id -> Chat
                CLI   .id -> CLI
                File  .id -> File
                Queue .id -> Queue
                Web   .id -> Web
                else      -> Other(name)
            }
        }
    }
}