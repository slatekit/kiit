package slatekit.setup

import slatekit.common.Context
import slatekit.results.Success
import slatekit.results.Try

class SetupService(val context: Context) {

    fun app(root:String, output:String): Try<String> {
        return create(root, "app", output)
    }


    fun cli(root:String, output:String): Try<String> {
        return create(root, "cli", output)
    }


    fun server(root:String, output:String): Try<String> {
        return create(root, "server", output)
    }


    private fun create(root:String, template:String, output:String):Try<String>{
       return Success("")
    }
}
