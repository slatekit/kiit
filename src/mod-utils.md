---
layout: start_page_mods_utils
title: module Utils
permalink: /mod-utils
---

# Utils

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Various utilities available in the Slate library | 
| **date**| 2017-04-12T22:59:15.401 |
| **version** | 1.4.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common.console  |
| **source core** | slate.common.console.ConsoleWriter.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/console](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/console)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Utils.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Utils.scala) |
| **depends on** |   |

## Import
```scala 
// required 
import slate.common.databases.DbConString
import slate.common._
import slate.common.Require._
import slate.common.results.{ResultSupportIn}




// optional 
import slate.core.cmds.Cmd


```

## Setup
```scala

n/a

```

## Usage
```scala


    // Miscellaneous utilities.

    // CASE 1: Api Credentials
    // The ApiCredentials class provides a convenient container for most fields required to
    // represent the access credentials some API such as AWS ( Amazon Web Services ) or Azure.
    val awsS3    = ApiCredentials("aws-s3", "ABCDEFG", "123456", "dev", "user-profile")
    val twilio   = ApiCredentials("1-234-567-8901", "ABCEDEFG", "123456", "dev", "sms")
    val sendgrid = ApiCredentials("support@mystartup.com", "mystartup", "123456789", "dev", "emails")

    // CASE 2: Api Keys
    val devKey = ApiKey("dev1", "B8779D64-6104-4244-88B6-F81B4D2AAF5B", "dev")
    val qaKey  = ApiKey("qa1" , "F01718FF-0AF5-43C2-84D7-D1E2B4234644", "qa")

    // CASE 3: Guards ( Exceptions are discouraged in favor
    // of functional error handling, however, there are times where
    // a guard like approach to inputs is preferable
    requireText("slate-kit", "Name must be supplied" )
    requireOneOf( "scala", Seq("scala", "go"), "Name not valid")
    requireValidIndex( 3, 4, "Index is must be 0 <= index <= 4")

    // CASE 4: Db connection
    val db = DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/app1", "db1", "1245689")

    // CASE 5: Interpret URI represetning file locations
    // - user dir: user://{folder} ( user home directory for os  )
    // - temp dir: temp://{folder} ( temp files directory for os )
    // - file dir: file://{path}   ( absolution file location    )
    // This yeilds c:/users/{user1}/myapp1
    val path1 = Uris.interpret("user://myapp1")

    // CASE 6: BoolMessage
    // combined bool / string instead of a tuple
    val result = BoolMessage(true, "successfuly created")
    

```

