package slatekit.cli

import slatekit.common.console.SemanticText

data class CliOutput(
    val type: SemanticText,
    val text: String?,
    val newline: Boolean
)
