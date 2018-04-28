import slatekit.core.cmds.Cmd
import slatekit.examples.*


/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
/**
 * Created by kishorereddy on 6/4/17.
 */

// https://looksok.wordpress.com/2014/07/12/compile-gradle-project-with-another-project-as-a-dependency/
fun main(args:Array<String>):Unit  {

    val logger = java.util.logging.Logger.getLogger("main")
    logger.info("info")
    logger.fine("fine")
    logger.warning("warn")
    logger.severe("severe")
    println("hello kotlin examples")
}



fun run() {
    val cmd = Example_SmartStrings()
    cmd.execute()
}
