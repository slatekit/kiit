package slatekit.setup

import java.io.File

interface SetupAction

data class SetupTemplate(val context:SetupContext,
                         val rootDir: File,
                         val actions:List<SetupAction>) {
}