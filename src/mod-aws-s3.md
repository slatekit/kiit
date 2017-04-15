---
layout: start_page_mods_infra
title: module AWS-S3
permalink: /mod-aws-s3
---

# AWS-S3

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Abstraction layer on cloud file storage to Amazon S3 | 
| **date**| 2017-04-12T22:59:15.749 |
| **version** | 1.4.0  |
| **jar** | slate.cloud.jar  |
| **namespace** | slate.cloud.aws  |
| **source core** | slate.cloud.aws.AwsCloudFiles.scala  |
| **source folder** | [/src/lib/scala/Slate.Cloud/src/main/scala/slate/cloud/aws](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Cloud/src/main/scala/slate/cloud/aws)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Aws_S3.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Aws_S3.scala) |
| **depends on** |  slate.common.jar slate.core.jar  |

## Import
```scala 
// required 
import slate.cloud.aws.{AwsCloudQueue, AwsCloudFiles}
import slate.common.results.ResultSupportIn



// optional 
import slate.common.{Result}
import slate.core.cmds.Cmd


```

## Setup
```scala


    // Not storing any key/secret in source code for security purposes
    // Setup 1: Use the default aws config file in "{user_dir}/.aws/credentials"
    val files1 = new AwsCloudFiles("app1-files-1", false)

    // Setup 2: Use the type safe config in "{user_id}/myapp/conf/files.conf"
    // Specify the api key section as "sqs"
    val files2 = new AwsCloudFiles("app1-files-1", false, Some("user://myapp/conf/files.conf"), Some("s3"))

    // Setup 2: Use the type safe config in "{user_id}/myapp/conf/files.conf"
    // Specify the api key section as "sqs"
    val files3 = new AwsCloudFiles("app1-queue-1", false, Some("user://myapp/conf/files.conf"), Some("s3-1"))

    

```

## Usage
```scala


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

