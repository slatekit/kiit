---
layout: start_page_mods_utils
title: module Auth
permalink: /mod-auth
---

# Auth

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A simple authentication component to check current user role and permissions | 
| **date**| 2017-04-12T22:59:15.633 |
| **version** | 1.4.0  |
| **jar** | slate.core.jar  |
| **namespace** | slate.core.auth  |
| **source core** | slate.core.auth.Auth.scala  |
| **source folder** | [/src/lib/scala/Slate.Core/src/main/scala/slate/core/auth](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Core/src/main/scala/slate/core/auth)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Auth.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Auth.scala) |
| **depends on** |  slate.common.jar  |

## Import
```scala 
// required 

import slate.common.Result
import slate.core.auth._


// optional 
import slate.common.results.ResultSupportIn
import slate.core.cmds.Cmd


```

## Setup
```scala


    // Setup: Setup the Auth wrapper with the user to inspect info about the user
    // NOTES:
    // * 1. This component does NOT handle any actual login/logout/authorization features.
    // * 2. This set of classes are only used to inspect information about a user.
    // * 3. Since authorization is a fairly complex feature with implementations such as
    // *    OAuth, Social Auth, Slate Kit has purposely left out the Authentication to more reliable
    // *    libraries and frameworks.
    // * 4. The SlateKit.Api component, while supporting basic api "Keys" based authentication,
    // *    and a roles based authentication, it leaves the login/logout and actual generating
    // *    of tokens to libraries such as OAuth.
    val user2 = new User( "2", "john doe", "john", "doe", "jdoe@gmail.com", "123-456-7890", false, false, true)
    val auth = new AuthConsole(isAuthenticated = true, user = user2, roles = "admin")
    

```

## Usage
```scala


    // CASE 1: Use the auth to check user info
    println ("Checking auth info in desktop/local mode" )
    println ( "user info         : " + auth.user                   )
    println ( "user id           : " + auth.userId                 )
    println ( "is authenticated  : " + auth.isAuthenticated        )
    println ( "is email verified : " + auth.isEmailVerified        )
    println ( "is phone verified : " + auth.isPhoneVerified        )
    println ( "is a moderator    : " + auth.isInRole( "moderator") )
    println ( "is an admin       : " + auth.isInRole( "admin" )    )
    

```

