---
layout: start_page_mods_utils
title: module Auth
permalink: /kotlin-mod-auth
---

# Auth

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A simple authentication component to check current user role and permissions | 
| **date**| 2018-03-18 |
| **version** | 0.9.9  |
| **jar** | slatekit.core.jar  |
| **namespace** | slatekit.core.auth  |
| **source core** | slatekit.core.auth.Auth.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Auth.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Auth.kt){:.url-ch} |
| **depends on** |  slatekit.common.jar  |

## Import
```kotlin 
// required 
import slatekit.common.auth.AuthConsole
import slatekit.common.auth.User



// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok


```

## Setup
```kotlin


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
    val user2 = User( "2", "john doe", "john", "doe", "jdoe@gmail.com", "123-456-7890", false, false, true)
    val auth = AuthConsole(isAuthenticated = true, user = user2, roles = "admin")
    

```

## Usage
```kotlin


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

