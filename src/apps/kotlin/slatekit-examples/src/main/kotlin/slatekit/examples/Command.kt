package slatekit.examples

import slatekit.common.args.Args
import slatekit.results.Try

data class CommandRequest(val name: String, val args:Args = Args.empty())

abstract class Command(val name:String) {
    abstract fun execute(request: CommandRequest): Try<Any>
}