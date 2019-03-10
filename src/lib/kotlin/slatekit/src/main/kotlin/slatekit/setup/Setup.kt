package slatekit.setup

import slatekit.common.info.About
import java.nio.file.Path

data class Setup(val mode:String,
                 val source:Path,
                 val destination:Path,
                 val about:About)