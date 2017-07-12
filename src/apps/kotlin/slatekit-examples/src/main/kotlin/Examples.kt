import slatekit.examples.Example_App
import slatekit.examples.Example_DateTime
import slatekit.examples.Example_Serialization
import slatekit.tutorial.Example_Kotlin_Basics
import slatekit.tutorial.Example_Kotlin_Functions
import slatekit.tutorial.Example_Kotlin_Misc

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
    val cmd = Example_App()
    cmd.execute()
    println("hello kotlin examples")
}