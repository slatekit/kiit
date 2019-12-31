package slatekit.apis.tools.code

import java.io.File
import java.io.FileNotFoundException
import slatekit.common.log.Logger

/**
 * @param outputFolder     : e.g. ~/dev/app1/src/main/kotlin/app/
 * @param targetFolder     : location where code is created  ( same as output folder unless otherwise specified )
 * @param apiFolder        : location where apis are created e.g. ~/dev/app1/src/main/kotlin/app/apis
 * @param modelFolder      : location where dtos are created e.g. ~/dev/app1/src/main/kotlin/app/dtos
 */
data class CodeGenDirs(
    val outputFolder: File,
    val targetFolder: File = outputFolder,
    val apiFolder: File = File(targetFolder, "api"),
    val modelFolder: File = File(targetFolder, "dto")
) {

    fun create(log: Logger) {
        if (!outputFolder.exists()) {
            outputFolder.mkdir()
            if(!outputFolder.exists()) {
                log.error("Output folder: ${outputFolder.absolutePath} does NOT exist!!")
                throw FileNotFoundException(outputFolder.absolutePath)
            }
        }
        targetFolder.mkdir()
        apiFolder.mkdir()
        modelFolder.mkdir()
    }

    fun log(log: Logger) {
        log.info("Output folder: " + outputFolder.absolutePath)
        log.info("Target folder: " + targetFolder.absolutePath)
        log.info("API    folder: " + apiFolder.absolutePath)
        log.info("Model  folder: " + modelFolder.absolutePath)
    }
}
