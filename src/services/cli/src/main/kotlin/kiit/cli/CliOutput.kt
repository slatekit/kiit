package kiit.cli

import kiit.utils.writer.TextType

data class CliOutput(
        val type: TextType,
        val text: String?,
        val newline: Boolean
)
