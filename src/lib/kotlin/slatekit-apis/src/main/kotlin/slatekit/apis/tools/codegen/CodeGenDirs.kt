package slatekit.apis.tools.codegen

import slatekit.common.log.Logger
import java.io.File
import java.io.FileNotFoundException

data class CodeGenDirs(
        val outputFolderPath:String,
        val dateFolderName:String,
        val outputFolder:File = File(outputFolderPath),
        val targetFolder:File = File(outputFolder, dateFolderName),
        val apiFolder   :File = File(targetFolder, "api"),
        val modelFolder :File = File(targetFolder, "dto")
) {

    fun create(log:Logger){
        if(!outputFolder.exists()){
            log.error("Output folder: ${outputFolder.absolutePath} does NOT exist!!")
            throw FileNotFoundException(outputFolder.absolutePath)
        }
        outputFolder.mkdir()
        targetFolder.mkdir()
        apiFolder.mkdir()
        modelFolder.mkdir()
    }


    fun log(log:Logger){
        log.info("Output folder: " + outputFolder.absolutePath)
        log.info("Target folder: " + targetFolder.absolutePath)
        log.info("API    folder: " + apiFolder.absolutePath)
        log.info("Model  folder: " + modelFolder.absolutePath)
    }
}