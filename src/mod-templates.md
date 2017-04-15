---
layout: start_page_mods_utils
title: module Templates
permalink: /mod-templates
---

# Templates

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A micro template system for processing text with variables, useful for generating dynamic emails/messages. | 
| **date**| 2017-04-12T22:59:15.351 |
| **version** | 1.4.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common.templates  |
| **source core** | slate.common.templates.Templates.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/templates](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/templates)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Templates.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Templates.scala) |
| **depends on** |   |

## Import
```scala 
// required 
import slate.common.templates.{TemplateConstants, Template, TemplatePart, Templates}


// optional 
import slate.common.{Result, Random}
import slate.core.cmds.Cmd


```

## Setup
```scala


    // Setup the templates with list of predefined(named) templates and
    // also a list of handlers to process named variables in the templates.
    // NOTE: The Templates.apply method ( used here ) processes/parses the templates
    // first so they don't have to be processed on demand.
    // You can also use the Templates constructor directly.
    val templates = Templates(
      templates = Seq(
        new Template("welcome", "Hi @{user.name}, Welcome to @{company.name}."),
        new Template("confirm", "Your confirmation code for @{app.name} is @{code}.")
      ),
      subs = Some(List[(String,(TemplatePart)=>String)](
        ("user.home"    , (s) => "c:/users/johndoe"                ),
        ("company.name" , (s) => "CodeHelix"                       ),
        ("company.dir"  , (s) => "@{user.home}/@{company.id}"      ),
        ("company.confs", (s) => "@{user.home}/@{company.id}/confs"),
        ("app.name"     , (s) => "slatekit.sampleapp"              ),
        ("app.dir"      , (s) => "@{company.dir}/@{app.id}"        ),
        ("app.confs"    , (s) => "@{app.dir}/confs"                ),
        ("user.name"    , (s) => "john.doe"                        ),
        ("code"         , (s) => Random.alpha6()                   )
    )))
    

```

## Usage
```scala


    // Case 1: Parse text and get the result as individual parts
    val result = templates.parse("Hi @{user.name}, Welcome to @{company.name}.")
    println("Case 1: Parse into individual parts")
    print(result.get)

    // Case 2: Parse text into a template
    val template = templates.parseTemplate("welcome", "Hi @{user.name}, Welcome to @{company.name}.")
    println("Case 2: Parse into a Template containing the parts")
    print(template)

    // Case 3: Resolve ( process ) the template on demand using the initial variables
    val text3 = templates.resolve("Hi @{user.name}, Welcome to @{company.name}.")
    println("Case 3: Resolve template with variables into a Option[String]")
    println(text3)
    println()

    // Case 4: Resolve ( process ) the template on demand using custom variables
    val text4 = templates.resolve("Hi @{user.name}, Welcome to @{company.name}.",
      Some( Templates.subs (
        List[(String,(TemplatePart)=>String)](
          ("company.name" , (s) => "Gotham" ),
          ("user.name"    , (s) => "batman"  )
        )
      ))
    )
    println("Case 4: Resolve template with custom variables")
    println(text4)
    println()

    // Case 5: Resolve ( process ) saved template using the initial variables
    val text5 = templates.resolveTemplate("welcome", None)
    println("Case 5: Resolve named template")
    println(text5)
    println()

    // Case 6: Resolve ( process ) saved template using custom variables
    val text6 = templates.resolveTemplate("welcome", Some( Templates.subs (
      List[(String,(TemplatePart)=>String)](
        ("company.name" , (s) => "Gotham" ),
        ("user.name"    , (s) => "batman"  )
      )
    )))
    println("Case 6: Resolve named template with custom variables")
    println(text6)
    println()

    

```


## Output

```bat
  Case 1: Parse into individual parts
  type: txt, pos : 0, len : 3, text: Hi
  type: sub, pos : 5, len : 14, var: user.name
  type: txt, pos : 15, len : 13, text: , Welcome to
  type: sub, pos : 30, len : 42, var: company.name
  type: txt, pos : 43, len : 1, text: .

  Case 2: Parse into a Template containing the parts
  name : welcome, content : Hi @{user.name}, Welcome to @{company.name}., group : None, path : None, parsed : true, valid : true, status : None
  type: txt, pos : 0, len : 3, text: Hi
  type: sub, pos : 5, len : 14, var: user.name
  type: txt, pos : 15, len : 13, text: , Welcome to
  type: sub, pos : 30, len : 42, var: company.name
  type: txt, pos : 43, len : 1, text: .

  Case 3: Resolve template with variables into a Option[String]
  Some(Hi john.doe, Welcome to CodeHelix.)

  Case 4: Resolve template with custom variables
  Some(Hi batman, Welcome to Gotham.)

  Case 5: Resolve named template
  Some(Hi john.doe, Welcome to CodeHelix.)

  Case 6: Resolve named template with custom variables
  Some(Hi batman, Welcome to Gotham.)
```
