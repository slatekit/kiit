package kiit.cli

import slatekit.utils.writer.TextType

data class CliOutput(
        val type: TextType,
        val text: String?,
        val newline: Boolean
)
