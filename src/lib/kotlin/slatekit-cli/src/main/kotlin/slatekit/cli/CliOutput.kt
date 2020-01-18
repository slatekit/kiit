package slatekit.cli

import slatekit.common.console.TextType

data class CliOutput(
    val type: TextType,
    val text: String?,
    val newline: Boolean
)
