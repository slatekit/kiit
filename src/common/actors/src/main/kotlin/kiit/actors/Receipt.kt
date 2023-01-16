package kiit.actors

sealed class Receipt {
    object Accepted : Receipt()
    object Rejected : Receipt()
}
