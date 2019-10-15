package slatekit.functions.syncs

import slatekit.results.Notice

typealias SyncCompletion = (Notice<Int>) -> Unit
typealias SyncCallback   = ((SyncCompletion) -> Unit)?