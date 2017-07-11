---
layout: start_page
title: module Utils
permalink: /scratch-server
---

```kotlin
 
    // 1: Sample API ( Pure Kotlin annotated classes / methods 
    @Api(area = "app", name = "tests", verb = "*", protocol = "*")
    class SampleApi(context: AppContext): ApiBase(context) {
    
        @ApiAction(desc = "accepts supplied basic data types from request", roles = "@parent", verb = "@parent", protocol = "@parent")
        fun hello(greeting: String): String {
            return "$greeting back"
        }
        
    }
	
	
    // 2: Setup the server with API(s)
    val server = Server(
            port = 5000,
            prefix = "/api/",
            info = true,
            ctx = AppContext.simple("myapp1"),
            apis = listOf(
                    ApiReg(SampleApi(ctx), false)
            )
    ) 

    // 3: Run the Server
    server.run()

```