---
layout: start_page_mods_infra
title: module AWS-S3
permalink: /kotlin-mod-aws-s3
---

# AWS-S3

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Abstraction layer on cloud file storage to Amazon S3 | 
| **date**| 2018-03-19 |
| **version** | 0.9.9  |
| **jar** | slatekit.cloud.jar  |
| **namespace** | slatekit.cloud.aws  |
| **source core** | slatekit.cloud.aws.AwsCloudFiles.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Aws_S3.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Aws_S3.kt){:.url-ch} |
| **depends on** |  slatekit.common.jar slatekit.core.jar  |

## Import
```kotlin 
// required 
import slatekit.cloud.aws.AwsCloudFiles



// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok


```

## Setup
```kotlin


    // Not storing any key/secret in source code for security purposes
    // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
    val files1 = AwsCloudFiles("app1-files-1", false)

    // Setup 2: Use the type safe config in "{user_id}/myapp/conf/files.conf"
    // Specify the api key section as "sqs"
    val files2 = AwsCloudFiles("app1-files-1", false, "user://myapp/conf/files.conf", "s3")

    // Setup 2: Use the type safe config in "{user_id}/myapp/conf/files.conf"
    // Specify the api key section as "sqs"
    val files3 = AwsCloudFiles("app1-queue-1", false, "user://myapp/conf/files.conf", "s3-1")

    

```

## Usage
```kotlin


    // Use case 1: Connect using parameters
    files1.init()

    // Use case 2: create using just name and content
    files1.create("2016_nba_v3", "version 1")

    // Use case 3: update using just name and content
    files1.update("2016_nba_v3", "version 2")

    // Use case 4: create using folder and file name
    files1.create("2016_nba_v3", "chi", "version 1")

    // Use case 5: update using folder and file name
    files1.update("2016_nba_v3", "chi", "version 2")

    // Use case 6: get file as a text using just name
    files1.getAsText("2016_nba_v3")

    // Use case 7: get file using folder and file name
    files1.getAsText("2016_nba_v3", "chi")

    // Use case 8: download file to local folder
    files1.download("2016_nba_v3", "c:/dev/temp/")

    // Use case 9: download using folder and file name to local folder
    files1.download("2016_nba_v3", "chi", "c:/dev/temp")

    // Use case 10: delete file by just the name
    files1.delete("2016_nba_v3")

    // Use case 11: delete using folder and name
    files1.delete("2016_nba_v3", "chi")
    

```

