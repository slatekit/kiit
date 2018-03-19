---
layout: start_page_mods_utils
title: module Reflect
permalink: /kotlin-mod-reflect
---

# Reflect

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Reflection helper to create instances, get methods, fields, annotations and more | 
| **date**| 2018-03-18 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common  |
| **source core** | slatekit.common.Reflector.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Reflect.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Reflect.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.common.Field
import slatekit.meta.Reflector



// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok
import slatekit.examples.common.User
import slatekit.examples.common.UserApi
import slatekit.integration.common.AppEntContext



```

## Setup
```kotlin



    

```

## Usage
```kotlin


    val ctx = AppEntContext.sample("sample", "sample", "", "")
    val api = UserApi(ctx)

    // CASE 1: Create instance of a class ( will pick a 0 parameter constructor )
    val user = Reflector.create<User>(User::class)
    println("user: " + user)


    // CASE 2: Get a field value
    val user2 = user.copy(email = "johndoe@home.com")
    val name = Reflector.getFieldValue(user2, "email")
    println("email : " + name)


    // CASE 3: Set a field value
    Reflector.setFieldValue(user, "email", "johndoe@work.com")
    println("email : " + user.email)


    // CASE 4: Call a method with parameters
    val result = Reflector.callMethod(UserApi::class, api, "create", arrayOf("superman@metro.com", "super", "man", true, 35))
    println((result as User ).toString())


    // CASE 5: Get a class level annotation
    // NOTE: The annotation must be created with all parameters ( not named parameters )
    val annoCls = Reflector.getAnnotationForClass<Api>(UserApi::class, Api::class)
    println(annoCls)


    // CASE 6: Get a method level annotations
    // NOTE: The annotation must be created with all parameters
    val annoMems = Reflector.getAnnotatedMembers<ApiAction>(UserApi::class, ApiAction::class)
    println(annoMems)


    // CASE 7: Get a field level annotations
    // NOTE: The annotation must be created with all parameters
    val annoFlds = Reflector.getAnnotatedProps<Field>(User::class, Field::class)
    println(annoFlds)


    // CASE 8: print parameters
    val method = Reflector.getMethod(UserApi::class, "activate")
    //Reflector.printParams(method)


    // CASE 10: Get method
    val sym = Reflector.getMethod(UserApi::class, "info")
    println(sym?.name)


    // CASE 11: Get method parameters
    val symArgs = Reflector.getMethodArgs(UserApi::class, "activate")
    println(symArgs)


    // CASE 12: Is argument a basic type
    val argType = symArgs!!.toList()[0]
    println(argType.type)


    // CASE 13: Create instance from parameter
    val argInstance = Reflector.create<Any>(symArgs!!.toList()[0].javaClass.kotlin)
    println(argInstance)
    

```

