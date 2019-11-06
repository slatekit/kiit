//import org.apache.tools.ant.filters.*
import java.io.File

tasks {
    val TARGET_HOME = "./gen/app1"
    val TARGET_NAME = project.property("target_name")
    val TARGET_PKG = project.property("target_pkg")

    println("Generating")
    println("Name: $TARGET_NAME")
    println("Package: $TARGET_PKG")

    File("$TARGET_HOME"     ).mkdir() 
    File("$TARGET_HOME/src" ).mkdir()
    File("$TARGET_HOME/test").mkdir()
    File("$TARGET_HOME/src/main").mkdir()
    File("$TARGET_HOME/src/main/resources").mkdir()
    File("$TARGET_HOME/src/main/java").mkdir()
    File("$TARGET_HOME/src/main/kotlin").mkdir()

    // Package parts
    val parts = TARGET_PKG.toString().split(".")
    val finalPath = parts.reduce { acc, curr -> 
        println("creating: " + acc)
        val path = "$TARGET_HOME/src/main/kotlin/$acc"
        File(path).mkdir()
        acc + "/" + curr 
    }
    println("creating: " + finalPath)
    File("$TARGET_HOME/src/main/kotlin/$finalPath").mkdir()
}