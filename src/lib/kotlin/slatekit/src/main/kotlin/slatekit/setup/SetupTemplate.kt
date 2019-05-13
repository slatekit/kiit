package slatekit.setup

interface SetupAction

data class SetupTemplate(val context:SetupContext,
                         val actions:List<SetupAction>) {
}