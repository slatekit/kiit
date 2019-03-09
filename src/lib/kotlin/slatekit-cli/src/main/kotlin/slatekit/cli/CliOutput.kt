package slatekit.cli

import slatekit.common.console.SemanticType


data class CliOutput(val type: SemanticType,
                     val text: String?,
                     val newline: Boolean)