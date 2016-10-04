# AWS.S3
| prop | desc  |
|:--|:--|
| **desc** | Abstraction layer on cloud file storage to Amazon S3 | 
| **date**| 2016-3-28 1:12:23 |
| **version** | 0.9.1  |
| **namespace** | slate.cloud.aws  |
| **core source** | slate.cloud.aws.AwsCloudFiles  |
| **example** | [Example_Aws_S3](https://github.com/kishorereddy/blend-server/blob/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Aws_S3.scala) |

# Import
```scala 
// required 
import slate.cloud.aws.AwsCloudFiles


// optional 
import slate.common.OperationResult
import slate.core.commands.Command


```

# Setup
```scala


    // Not storing any key/secret in source code for security purposes
    // Using the recommended approach of aws to store a config file
    // in the users directory
    val files = new AwsCloudFiles("blendlife-dev1", false)
    

```

# Examples
```scala


    // Use case 1: Connect using parameters
    files.connect(null)

    // Use case 2: create using just name and content
    files.create("2016_nba_v3", "version 1")

    // Use case 3: update using just name and content
    files.update("2016_nba_v3", "version 2")

    // Use case 4: create using folder and file name
    files.create("2016_nba_v3", "chi", "version 1")

    // Use case 5: update using folder and file name
    files.update("2016_nba_v3", "chi", "version 2")

    // Use case 6: get file as a text using just name
    files.getAsText("2016_nba_v3")

    // Use case 7: get file using folder and file name
    files.getAsText("2016_nba_v3", "chi")

    // Use case 8: download file to local folder
    files.download("2016_nba_v3", "c:/dev/temp/")

    // Use case 9: download using folder and file name to local folder
    files.download("2016_nba_v3", "chi", "c:/dev/temp")

    // Use case 10: delete file by just the name
    files.delete("2016_nba_v3")

    // Use case 11: delete using folder and name
    files.delete("2016_nba_v3", "chi")
    

```
