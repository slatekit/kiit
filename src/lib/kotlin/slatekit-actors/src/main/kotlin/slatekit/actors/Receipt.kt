package slatekit.actors

sealed class Receipt {
    object Accepted : Receipt()
    object Rejected : Receipt()
}
