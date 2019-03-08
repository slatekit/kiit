package slatekit.cli


data class CliOutput(val type: slatekit.common.console.TextType,
                     val text: String?,
                     val newline: Boolean)