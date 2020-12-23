package slatekit.cli

import slatekit.common.writer.TextType

data class CliOutput(
    val type: TextType,
    val text: String?,
    val newline: Boolean
)
