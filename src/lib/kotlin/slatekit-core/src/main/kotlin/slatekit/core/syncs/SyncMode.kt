package slatekit.core.syncs

enum class SyncMode constructor(val value: Int) {
    Periodic(0),
    OnDemand(1),
    Both(2)
}