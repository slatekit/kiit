//import org.apache.tools.ant.filters.*
import java.io.File

data class Source(val name:String) {
}


data class Target(val home:String, val name:String, val pkg:String) {
    
    fun mkdirs(){
        File("$home"     ).mkdir() 
        File("$home/src" ).mkdir()
        File("$home/test").mkdir()
        File("$home/src/main").mkdir()
        File("$home/src/main/resources").mkdir()
        File("$home/src/main/java").mkdir()
        File("$home/src/main/kotlin").mkdir()
    }


    fun mkpkg(){
        // Package parts
        val parts = pkg.toString().split(".")
        val finalPath = parts.reduce { acc, curr -> 
            println("creating: " + acc)
            val path = "$home/src/main/kotlin/$acc"
            File(path).mkdir()
            acc + "/" + curr 
        }
        println("creating: " + finalPath)
        File("$home/src/main/kotlin/$finalPath").mkdir()
    }
}


data class GenContext(val source:Source, val target:Target) {
    
    fun info() {
       println()
        println("generating")
        println("source.Name: $name")
        println("target.Name: ${target.name}")
        println("target.Package: ${target.pkg}")
        println() 
    }

    fun mkdirs() {
        target.mkdirs()
        target.mkpkg()
    }
}


class Builder {
    fun context():GenContext {
        val trg = target()
        val src = source()
        val context = GenContext(src, trg)
        return context
    }

    fun target():Target {
        val name = project.property("target_name")
        val pkg = project.property("target_pkg")
        val home = "./gen/$name"
        return Target(home, name.toString(), pkg.toString())
    }

    fun source():Source {
        return Source(project.property("source_name").toString())
    }
}


tasks {
    
    val ctx = Builder().context()
    ctx.info()
    ctx.mkdirs()
}


