package slatekit.setup

import slatekit.common.Context
import slatekit.results.Success
import slatekit.results.Try

class SetupService(val context: Context) {

    fun app(context:SetupContext): Try<String> {
        val actions = SetupTemplates.app()
        val template = SetupTemplate(context, actions)
        return Success("")
    }
}
