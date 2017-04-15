---
layout: start_page_mods_utils
title: module Reflect
permalink: /mod-reflect
---

# Reflect

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Reflection helper for Scala to create instances, get methods, fields, annotations and more | 
| **date**| 2017-04-12T22:59:15.444 |
| **version** | 1.4.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common  |
| **source core** | slate.common.Reflector.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/common](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/common)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Reflect.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Reflect.scala) |
| **depends on** |   |

## Import
```scala 
// required 
import slate.common.{Result, Field, Reflector}
import slate.core.apis.{Api, ApiAction}
import slate.core.common.AppContext
import scala.reflect.runtime.universe._


// optional 
import slate.core.cmds.Cmd
import slate.common.results.ResultSupportIn
import slate.examples.common.{User, UserApi}


```

## Setup
```scala



    

```

## Usage
```scala


    val ctx = AppContext.sample("sample", "sample", "", "")
    val api = new UserApi(ctx)

    // CASE 1: Create instance of a class ( will pick a 0 parameter constructor )
    val user = Reflector.createInstance(typeOf[User]).asInstanceOf[User]
    println("user: " + user)


    // CASE 2: Get a field value
    user.email = "johndoe@home.com"
    val name = Reflector.getFieldValue(user, "email")
    println("email : " + name)


    // CASE 3: Set a field value
    Reflector.setFieldValue(user, "email", "johndoe@work.com")
    println("email : " + user.email)


    // CASE 4: Call a method with parameters
    val result = Reflector.callMethod(api, "create", Array[Any]("superman@metro.com", "super", "man", true, 35))
    println(result.asInstanceOf[User].toString())


    // CASE 5: Get a class level annotation
    // NOTE: The annotation must be created with all parameters ( not named parameters )
    val annoCls = Reflector.getClassAnnotation(typeOf[UserApi], typeOf[Api]).asInstanceOf[Api]
    println(annoCls)


    // CASE 6: Get a method level annotation
    // NOTE: The annotation must be created with all parameters
    val annoMem = Reflector.getMemberAnnotation(typeOf[UserApi], typeOf[ApiAction], "activate").asInstanceOf[ApiAction]
    println(annoMem)


    // CASE 7: Get a field level annotation
    // NOTE: The annotation must be created with all parameters
    val annoFld = Reflector.getFieldAnnotation(typeOf[User], typeOf[Field], "email").asInstanceOf[Field]
    println(annoFld)


    // CASE 8: print parameters
    val method = Reflector.getMethod(api, "activate")
    //Reflector.printParams(method)

    // CASE 9: get all fields with annotations
    val fields = Reflector.getFieldsWithAnnotations(Option(user), typeOf[User], typeOf[Field])


    // CASE 10: Get method
    val sym = Reflector.getMethod(api, "info")
    println(sym.name)


    // CASE 11: Get method parameters
    val symArgs = Reflector.getMethodParameters(sym)
    println(symArgs(0))


    // CASE 12: Is argument a basic type
    val argType = symArgs(0)
    println(argType.isBasicType())


    // CASE 13: Create instance from parameter
    val argInstance = Reflector.createInstance(symArgs(0).asType())
    println(argInstance)


    // CASE 14: Get fields of argument type
    val argInstanceFields = Reflector.getFieldsDeclared(argInstance.asInstanceOf[AnyRef])
    println(argInstanceFields)
    

```

