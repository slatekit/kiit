# Auth
| prop | desc  |
|:--|:--|
| **desc** | A simple auth component for desktop/local and web/concurrent apps | 
| **date**| 2016-3-28 1:12:23 |
| **version** | 0.9.1  |
| **namespace** | slate.core.auth  |
| **core source** | slate.core.auth.Auth  |
| **example** | [Example_Auth](https://github.com/kishorereddy/blend-server/blob/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Auth.scala) |

# Import
```scala 
// required 
import slate.core.auth._


// optional 
import slate.common.OperationResult
import slate.core.commands.Command


```

# Setup
```scala


    // Setup 1: For web / con-current apps ( use separate instance of authWeb per user )
    val user1 = new User( "1", "user 001", "user", "001", "user001@gmail.com", "123-456-7001", false, false, true)
    val authWeb = new AuthWeb().init(isAuthenticated = true, user = user1, roles = "moderator")

    // Setup 2: For local / desktop ( non-concurrent apps - use 1 instance of AuthDesktop )
    // and initialize the singleton Auth with the desktop implementation for convenient access
    val user2 = new User( "2", "john doe", "john", "doe", "jdoe@gmail.com", "123-456-7890", false, false, true)
    Auth.init(isAuthenticated = true, user = user2, roles = "admin")
    

```

# Examples
```scala


    // Use case 1: Use the authWeb ( per user / concurrent service ) to check auth info
    println ("Checking auth info in web/concurrent mode" )
    println ( "user info         : " + authWeb.user                   )
    println ( "user id           : " + authWeb.userId                 )
    println ( "is authenticated  : " + authWeb.isAuthenticated        )
    println ( "is email verified : " + authWeb.isEmailVerified        )
    println ( "is phone verified : " + authWeb.isPhoneVerified        )
    println ( "is a moderator    : " + authWeb.isInRole( "moderator") )
    println ( "is an admin       : " + authWeb.isInRole( "admin" )    )
    println ()

    // Use case 2: Use singleton Auth class for desktop apps to conveniently access auth information
    println ("Checking auth info in desktop/local mode" )
    println ( "user info         : " + Auth.user                   )
    println ( "user id           : " + Auth.userId                 )
    println ( "is authenticated  : " + Auth.isAuthenticated        )
    println ( "is email verified : " + Auth.isEmailVerified        )
    println ( "is phone verified : " + Auth.isPhoneVerified        )
    println ( "is a moderator    : " + Auth.isInRole( "moderator") )
    println ( "is an admin       : " + Auth.isInRole( "admin" )    )
    

```
