# Api
| prop | desc  |
|:--|:--|
| **desc** | A protocol agnostic api approach to make apis available on command line or web | 
| **date**| 2016-3-28 1:12:23 |
| **version** | 0.9.1  |
| **namespace** | slate.core.apis  |
| **core source** | slate.core.apis.ApiRunner  |
| **example** | [Example_Api](https://github.com/kishorereddy/blend-server/blob/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Api.scala) |

# Import
```scala 
// required 
import slate.core.apis._
import scala.reflect.runtime.universe.typeOf


// optional 
import slate.common.OperationResult
import slate.core.commands.Command
import slate.examples.common.{UserApi, User}


```

# Setup
```scala


    // Setup 1: Register an api that can be called dynamically
    val apiRunner = new ApiRunner()
    apiRunner.register[UserApi](new UserApi())
    

```

# Examples
```scala


    // The point of the api runner is that it allows your api ( decorated )
    // with Api, ApiAction, ApiArg annotations to be called on:
    //
    // 1. command line shell
    // 2. web api rest calls
    //
    // Also, the api runner and apiBase class and api annotations provide support for:
    //
    // 1. authorization
    // 2. auditing
    // 3. encryption / decryption
    // 4. status updates
    // 5. error handling
    //
    // This approach essentially makes your api protocal agnostic!!

    // Use case 1: check if api action exists ( false )
    printResult( apiRunner.contains( "users.fakeMethod" ) )

    // Use case 2: check if api action exists ( true )
    printResult( apiRunner.contains( "users.total" ) )

    // Use case 3a: validate the parameters ( fails - total takes 0 parameters )
    printResult( apiRunner.check( "users.total -test:5" ) )

    // Use case 3b: validate the parameters ( fails - invite takes 3 parameters )
    printResult( apiRunner.check( "users.activate" ) )

    // Use case 3c: validate the parameters ( fails - invite requires 'phone' and 'date' param )
    printResult( apiRunner.check( "users.activate -code:1234 -isPremiumUser:true" ) )

    // Use case 3d: validate the parameters ( succeeds )
    printResult( apiRunner.check( "users.activate -phone:1234567890 -code:1234 -isPremiumUser:true -date:20160315" ) )

    // Use case 4: call an api action without parameters
    printResult( apiRunner.call( "users.total" ) )

    // Use case 6: call an api action with multiple parameters
    printResult( apiRunner.call( "users.activate -phone:1234567890 -code:1234 -isPremiumUser:true -date:20160315" ) )
    

```
