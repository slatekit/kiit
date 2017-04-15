---
layout: start_page_mods_infra
title: sample apps
permalink: /samples
---

# 1. Sample App - Batch
The Sample App Batch shows the Slate Kit Base App.
Refer to APP docs for more info.
http://www.slatekit.com/app.html

## About 

The Base App is a powerful base application that includes support for:

1. command line args 
2. environment selection ( dev, qa, stg, prod ) 
3. configs per environment  
4. encryption / decryption  
5. host info 
6. lang runtime info  
7. application info and version  
8. logging  

## Steps

{: .table .table-striped .table-bordered}
|:--|:--|
|1. | run script : run-sample-batch.bat |
|2. | the sample app displays startup info  |
|3. | the sample runs some simulated work for 2 seconds and shuts down |

## Output 

```bat

c:/slatekit/1.0>run-sample-batch.bat

OUTPUT:
Info  : ===============================================================
Info  : SUMMARY :
Info  : ===============================================================
Info  : name             = Sample App - Console
Info  : desc             = Sample console application to show the Slate Kit base app
Info  : version          = 0.9.1
Info  : tags             = slate,shell,cli
Info  : group            = Samples
Info  : region           = ny
Info  : contact          = kishore@codehelix.co
Info  : url              = http://sampleapp.slatekit.com
Info  : args             = Some([Ljava.lang.String;@2424686b)
Info  : env              = loc
Info  : config           = env.loc.conf
Info  : log              = console
Info  : started          = 2016-10-01T01:18:29.753
Info  : ended            = 2016-10-01T01:18:31.182
Info  : duration         = slate.common.TimeSpan@6ea94d6a
Info  : status           = ended
Info  : errors           = 0
Info  : error            = n/a
Info  : host.name        = KRPC1
Info  : host.ip          = Windows 10
Info  : host.origin      = local
Info  : host.version     = 10.0
Info  : lang.name        = scala
Info  : lang.version     = 1.8.0_91
Info  : lang.versionNum  = 2.11.8
Info  : lang.java        = local
Info  : lang.home        = C:/Tools/Java/jdk1.8.0_91/jre
Info  : region = n/a
Info  : ===============================================================
```


# 2. Sample App - Shell 

## About 

The Sample App Shell shows Slate Kit protocol independent APIs running in a command line shell.
Refer to API docs for more info on protocol independent APIs. 
http://www.slatekit.com/apis-detail.html

## Steps 

{: .table .table-striped .table-bordered}
|:--|:--|
| 1. | run script : run-sample-shell.bat |
| 2. | type ? to list all the api areas  |
| 3. | type sys ? to list all the apis in the "sys" area  |
| 4. | type sys.version? to list all the actions in the "sys.version" api  |
| 5. | you can test any of the api commands below.  |
| 6. | type "exit" to shutdown the sample app shell and return the command line  |

## Examples 

```java  
c:/slatekit/1.0>run-sample-shell.bat
:> ?
:> sys.version.java
:> sys.version.scala 
:> app.info.lang
:> app.info.host
:> app.info.app
:> sampleapp.users ?
:> sampleapp.users.total
:> sampleapp.users.create  -email="batman@gotham.com" -first="bruce" -last="wayne" -isMale=true -age=32 -phone="123456789" -country="us" 
:> sampleapp.users.create  -email="superman@metropolis.com" -first="clark" -last="kent" -isMale=true -age=32 -phone="987654321" -country="us" 
:> sampleapp.users.create  -email="wonderwoman@metropolis.com" -first="diana" -last="price" -isMale=false -age=32 -phone="111111111" -country="br" 
:> sampleapp.users.getById -id=1
:> sampleapp.users.first   
:> sampleapp.users.last
:> sampleapp.users.recent -count=2
:> sampleapp.users.oldest -count=2
:> exit
```



# 3. Sample App - API Server 

## About 
The Sample App Server shows Slate Kit protocol independent APIs running in the Slate Kit Server
Refer to API docs for more info on protocol independent APIs. 
http://www.slatekit.com/apis-detail.html


- The Web API Server runs on Akka-Http and hosts the APIs located in the SampleApp.Core project.
- The APIs are protocol independent and can also run the CLI ( above ).
- In order to test the server do the following:

## Steps 

{: .table .table-striped .table-bordered}
|:--|:--|
| 1. | run script : run-sample-server.bat |
| 2. | open postman ( chrome extension to send/test http requests ) |
| 3. | ensure you have setup a header with api-key => 54B1817194C1450B886404C6BEA81673 |
| 4. | ensure for "post" requests you have set the body = raw ( json/application ) |
| 5. | ensure for "post" requests you have at least an empty json object "{ }" for calling endpoints/methods that take 0 params |
| 6. | use any of the urls below for testing |

## Examples 

{: .table .table-striped .table-bordered}
|:--|:--|:--|:--|
| verb  | header      | url                                               | body ( json ) | 
| post  | see 3. | http://localhost:5000/api/sys/version/java        | { }  |    
| post  | see 3. | http://localhost:5000/api/sys/version/scala       | { }  |        
| post  | see 3. | http://localhost:5000/api/app/info/lang           | { }  |               
| post  | see 3. | http://localhost:5000/api/app/info/host           | { }  |        
| post  | see 3. | http://localhost:5000/api/app/info/app            | { }  |      
| post  | see 3. | http://localhost:5000/api/sampleapp/users/total   | { }  |
| post  | see 3. | http://localhost:5000/api/sampleapp/users/create  | { "email" : "batman@gotham.com", "first" : "bruce", "last" : "wayne", "isMale" : true, "age" : 32, "phone" : "123456789", "country" : "us" } |
| post  | see 3. | http://localhost:5000/api/sampleapp/users/create  | { "email" : "superman@metropolis.com", "first" : "clark", "last" : "kent", "isMale" : true, "age" : 32, "phone" : "987654321", "country" : "us" } |
| post  | see 3. | http://localhost:5000/api/sampleapp/users/create  | { "email" : "wonderwoman@metropolis.com", "first" : "diana", "last" : "price", "isMale" : false, "age" : 32, "phone" : "111111111", "country" : "us" } |
| post  | see 3. | http://localhost:5000/api/sampleapp/users/first   | { } |
| post  | see 3. | http://localhost:5000/api/sampleapp/users/last    | { } |
| post  | see 3. | http://localhost:5000/api/sampleapp/users/recent  | { "count" : 2 } |
| post  | see 3. | http://localhost:5000/api/sampleapp/users/oldest  | { "count" : 2 } |
