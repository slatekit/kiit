/**
<slate_header>
  author: Kishore Reddy
  url: www.github.com/code-helix/slatekit
  copyright: 2016 Kishore Reddy
  license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  desc: A tool-kit, utility library and server-backend
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slatekit.examples

//<doc:import_required>
import slatekit.common.templates.Template
import slatekit.common.templates.TemplateConstants
import slatekit.common.templates.TemplatePart
import slatekit.common.templates.Templates

//</doc:import_required>

//<doc:import_examples>
import slatekit.cmds.Command
import slatekit.common.utils.Random
import slatekit.cmds.CommandRequest
import slatekit.results.Try
import slatekit.results.getOrElse

//</doc:import_examples>

/**
  * Created by kreddy on 10/21/2016.
  */
class Example_Templates : Command("templates") {

  override fun execute(request: CommandRequest) : Try<Any>
  {
    //<doc:setup>
    // Setup the templates with list of predefined(named) templates and
    // also a list of handlers to process named variables in the templates.
    // NOTE: The Templates.apply method ( used here ) processes/parses the templates
    // first so they don't have to be processed on demand.
    // You can also use the Templates constructor directly.
    val templates = Templates.build(
      templates = listOf(
        Template("showWelcome", "Hi @{user.api}, Welcome to @{company.api}."),
        Template("confirm", "Your confirmation code for @{app.api} is @{code}.")
      ),
      subs = listOf(
        Pair("user.home"    , { s -> "c:/users/johndoe"                } ),
        Pair("company.api" , { s -> "CodeHelix"                       } ),
        Pair("company.dir"  , { s -> "@{user.home}/@{company.id}"      } ),
        Pair("company.confs", { s -> "@{user.home}/@{company.id}/confs"} ),
        Pair("app.api"     , { s -> "slatekit.sampleapp"              } ),
        Pair("app.dir"      , { s -> "@{company.dir}/@{app.id}"        } ),
        Pair("app.confs"    , { s -> "@{app.dir}/confs"                } ),
        Pair("user.api"    , { s -> "john.doe"                        } ),
        Pair("code"         , { s -> Random.alpha6()                   } )
    ))
    //</doc:setup>

    //<doc:examples>
    // Case 1: Parse text and get the result as individual parts
    val result = templates.parse("Hi @{user.api}, Welcome to @{company.api}.")
    println("Case 1: Parse into individual parts")
    print(result.getOrElse { listOf() })

    // Case 2: Parse text into a template
    val template = templates.parseTemplate("showWelcome", "Hi @{user.api}, Welcome to @{company.api}.")
    println("Case 2: Parse into a Template containing the parts")
    print(template)

    // Case 3: Resolve ( process ) the template on demand using the initial variables
    val text3 = templates.resolve("Hi @{user.api}, Welcome to @{company.api}.")
    println("Case 3: Resolve template with variables into a Option[String]")
    println(text3)
    println()

    // Case 4: Resolve ( process ) the template on demand using custom variables
    val text4 = templates.resolve("Hi @{user.api}, Welcome to @{company.api}.",
      Templates.subs (
        listOf(
          Pair("company.api" , { s -> "Gotham"  }),
          Pair("user.api"    , { s -> "batman"  })
        )
      )
    )
    println("Case 4: Resolve template with custom variables")
    println(text4)
    println()

    // Case 5: Resolve ( process ) saved template using the initial variables
    val text5 = templates.resolveTemplate("showWelcome")
    println("Case 5: Resolve named template")
    println(text5)
    println()

    // Case 6: Resolve ( process ) saved template using custom variables
    val text6 = templates.resolveTemplate("showWelcome", Templates.subs (
      listOf(
        Pair("company.api" , { s -> "Gotham"  } ),
        Pair("user.api"    , { s -> "batman"  } )
      )
    ))
    println("Case 6: Resolve named template with custom variables")
    println(text6)
    println()

    //</doc:examples>
    return result
  }


  fun print(template:Template):Unit {
    println("api : "    + template.name    +
            ", content : " + template.content +
            ", group : "   + template.group   +
            ", path : "    + template.path    +
            ", parsed : "  + template.parsed  +
            ", valid : "   + template.valid   +
            ", status : "  + template.status )
    print( template.parts  )
  }


  fun print(parts:List<TemplatePart>):Unit {
    parts.forEach { part ->
      val tpe = if(part.subType == TemplateConstants.TypeText) "txt" else "sub"
      val prop = if(part.subType == TemplateConstants.TypeText) "text" else "var"
      println("type: " + tpe         +
              ", pos : " + part.pos    +
              ", len : " + part.length +
              ", " + prop + ": " + part.text   )

    }
    println()
  }


/*
//<doc:output>
{{< highlight yaml >}}
  Case 1: Parse into individual parts
  type: txt, pos : 0, len : 3, text: Hi
  type: sub, pos : 5, len : 14, var: user.api
  type: txt, pos : 15, len : 13, text: , Welcome to
  type: sub, pos : 30, len : 42, var: company.api
  type: txt, pos : 43, len : 1, text: .

  Case 2: Parse into a Template containing the parts
  api : welcome, content : Hi @{user.api}, Welcome to @{company.api}., group : None, path : None, parsed : true, valid : true, status : None
  type: txt, pos : 0, len : 3, text: Hi
  type: sub, pos : 5, len : 14, var: user.api
  type: txt, pos : 15, len : 13, text: , Welcome to
  type: sub, pos : 30, len : 42, var: company.api
  type: txt, pos : 43, len : 1, text: .

  Case 3: Resolve template with variables into a Option[String]
  Some(Hi john.doe, Welcome to CodeHelix.)

  Case 4: Resolve template with custom variables
  Some(Hi batman, Welcome to Gotham.)

  Case 5: Resolve named template
  Some(Hi john.doe, Welcome to CodeHelix.)

  Case 6: Resolve named template with custom variables
  Some(Hi batman, Welcome to Gotham.)
{{< /highlight >}}
//</doc:output>
*/
}


