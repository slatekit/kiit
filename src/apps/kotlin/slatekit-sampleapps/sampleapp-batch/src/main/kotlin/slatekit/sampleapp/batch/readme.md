#  SAMPLE APP 

The Sample App Batch shows the Slate Kit Base App.
Refer to APP docs for more info.
http://www.slatekit.com/app.html

# NOTES
The Base App is a powerful base application that includes support for:
- command line args 
- environment selection ( dev, qa, stg, prod )
- configs per environment 
- encryption / decryption 
- host info
- lang runtime info 
- application info and version 
- logging 

# RUNNING
1. run script : run-sample-batch.bat
2. the sample app displays startup info 
3. the sample runs some simulated work for 2 seconds and shuts down

# HELP

```bat
 ==============================================
 ABOUT
 name     :  Sample App
 desc     :  Sample console application to show the base application features
 group    :  codehelix.co
 region   :  ny
 url      :  http://sampleapp.slatekit.com
 contact  :  kishore@codehelix.co
 version  :  0.9.1
 tags     :  slate,shell,cli
 examples :  sampleapp -env=dev -log.level=debug -region='ny' -enc=false
 ==============================================

 ARGS
 -env     :  the environment to run in
             ! required  [String]  e.g. dev
 -log     :  the log level for logging
             ? optional  [String]  e.g. info
 -enc     :  whether encryption is on
             ? optional  [String]  e.g. false
 -region  :  the region linked to app
             ? optional  [String]  e.g. us

```


# OUTPUT:

```bat
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