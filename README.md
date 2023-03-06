
[![WebSite](https://img.shields.io/badge/site-slatekit-blue)](https://www.slatekit.com)
![Kotlin](https://img.shields.io/badge/kotlin-1.3-orange.svg)
![Apache 2](https://img.shields.io/badge/license-Apache2-green.svg?style=flat)
![Follow us on twitter](https://img.shields.io/badge/twitter-slatekit-blue.svg)
<!-- ![Gradle Release](https://github.com/code-helix/slatekit/workflows/Gradle%20Package/badge.svg) -->

# ❓ Kiit
Kiit ( Formerly Slate Kit ) is a modular **Kotlin** framework to build Server and Android apps.

# 🗒️ Details
Kiit is a Kotlin framework, designed to be a simple, light-weight, modular set of libraries and tools to build **Apps, APIs, CLIs, Jobs, Mobile Apps**. It is targetd for **Start-ups, Personal projects, Mobile Apps, and SMBs** ( small-medium sized businesses ). These libraries can be used for both **Server and Android** and there are modules for the server that abstract infrastructure ( Queues, Files, SMS, Emails, Alerts ) with integrations for **AWS** ( SQS, S3 ), Twilio ( SMS ), SendGrid ( Email ), Slack ( Alerts ) and more. Multi-platform support is planned as a future state.

![image](media/27-dev.png)

# ✨ Goals
num | type | link 
----| ------------ | ------------ 
1 | **Simple** | Easy to use, light, modular, 100% Kotlin (alternative to Spring Framework) 
2 | **Projects** | More than just for APIs, use it for Console apps, Jobs, CLIs.
3 | **Usage**  | Start-Ups, Personal, Mobile Apps, SMB (small-mid sized business)
4 | **Full-Stack** | Usable on both Server and Android ( due to being simple/light-weight)
5 | **Multiplatform** |  Kotlin Multi-Platform planned for the future for Javascript, Native, iOS, etc
6 | **Modular**  |  Designed as a set of libraries rather than a typical `"framework"`
7 | **Tools**  |  CLI tools to quickly create new Apps, CLIs, API, Jobs projects
8 | **Cloud**  |  Partial Cloud Provider abstractions for ( Queues, Files, Databases, etc ) 
9 | **Defaults** |  Sensible default implementations, currently support AWS

# 🏁 Start
You can quickly get started on **Mac OS** using the **Homebrew** installer and create projects using the Kiit CLI. Refer to https://www.slatekit.com/start/generators/ for more info.
```bash
brew tap slatekit/kiit

# NOTE: Install may take a long time on Mac (Catalina) due to issues noted below
brew install kiit

kiit new app -name="MyApp1" -package="company1.apps"
kiit new api -name="MyAPI1" -package="company1.apis"
kiit new job -name="MyJob1" -package="company1.jobs"
kiit new env -name="MyApp2" -package="company1.apps"
kiit new cli -name="MyCLI1" -package="company1.apps"
```

### Notes
1. [Slow HomeBrew post install](https://discussions.apple.com/thread/251258165)
2. [The install may be very slow with MacOS Catalina](https://discourse.brew.sh/t/brew-install-very-slow-pauses-for-long-period-while-executing-usr-bin-sandbox-exec-in-post-install/7423)
3. Ensure security -> privacy -> full disk access -> iterm2 ( of what ever terminal you use )


# 🏁 Install
You can set up gradle using the example below. You can use as few or as many slatekit modules as you need. 
See https://github.com/orgs/slatekit/packages?repo_name=slatekit for latest versions

```groovy
repositories {
    jcenter()
    mavenCentral()
    maven {
        url "https://maven.pkg.github.com/slatekit/kiit"
	// Your GitHub user name + personal access token
        credentials {
            username = System.getenv('GITHUB_PACKAGES_INSTALL_ACTOR')
            password = System.getenv('GITHUB_PACKAGES_INSTALL_TOKEN')
        }
    }
}

dependencies {
	// Use the results module: Result<T,E> to model successes/failures with optional status codes
    	compile 'dev.kiit:results:2.11.1'
	// ... Other packages here 
}
```

# 📌 Links
Some important links / pages for more info.

num | type | link 
----| ------------ | ------------ 
1   | website     | www.kiit.dev                            
2   | start       | www.kiit.dev/start           
3   | modules     | https://www.kiit.dev/arch/overview/             
4   | releases    | https://github.com/slatekit/kiit/releases              
5   | packages    | https://github.com/orgs/slatekit/packages?repo_name=kit
6   | issues      | https://github.com/slatekit/kiit/issues   
7   | discuss     | https://github.com/slatekit/kiit/discussions   
8   | license     | http://www.kiit.dev/more/license           
9   | utilities   | http://www.kiit.dev/utils/overview          
10   | standards   | http://www.kiit.dev/more/standards  

# 🧰 Tools
These are the vendors and tools we currently use to build and maintain Kiit

Name | Type | Notes
----|------|-------
![intellij](media/intellij.png) | IDE | [Kotlin/Java IDE](https://www.jetbrains.com/idea/). Every aspect of IntelliJ IDEA has been designed to maximize developer productivity. Together, intelligent coding assistance and ergonomic design make development not only productive but also enjoyable.
![your kit](https://www.yourkit.com/images/yklogo.png) | Profiler | [YourKit](https://www.yourkit.com) supports open source projects with innovative and intelligent tools for monitoring and profiling Java and .NET applications. YourKit is the creator of [YourKit Java Profiler](https://www.yourkit.com/java/profiler)
![ktor](media/ktor.png) | HTTP Server | [Ktor](https://www.ktor.io) is an asynchronous framework for creating microservices, web applications, and more. It’s fun, free, and open source.



# ⚙️ Modules
Kiit contains many useful architecture components, utilities and applications features. Many of the modules are organized into logical groups and the entire design can be visualized in this diagram

![image](doc/diagrams/slatekit-overview.png)

Here are some of the main ones:

docs | source | desc 
------------ | ------------ | -------------
Foundations                                                         | --                                        | Used by most modules                
[results](https://www.kiit.dev/arch/results)             | [src](src/common/result)            | Modeling of Successes/Failures 
[common](https://www.kiit.dev/utils/overview)            | [src](src/common/common)            | Utility Components   
[context](https://www.kiit.dev/arch/context)             | [src](src/common/context)           | Stores Common Dependencies 
[actors](https://www.kiit.dev/arch/actors)               | [src](src/internal/actors)            | Micro Actor Library 
Apps                                                                | --                                        | Runnable apps/Services                
[app](http://www.kiit.dev/arch/app)                      | [src](src/services/app)               | Runnable App Template 
[cli](http://www.kiit.dev/arch/cli)                      | [src](src/services/cli)               | Command Line Interface 
[apis](http://www.kiit.dev/arch/apis)                    | [src](src/services/apis)              | RPC-like Web APIs
[jobs](http://www.kiit.dev/arch/jobs)                    | [src](src/services/jobs)              | Pausable jobs on persistent queues 
Infrastructure                                                      | --                                        | Infrastructure components               
[core](http://www.kiit.dev/arch/core)                    | [src](src/infra/core)              | Infrastructure Abstractions(Files, Queues)
[cache](http://www.kiit.dev/arch/cache)                  | [src](src/infra/cache)             | Caching library 
[notifications](http://www.kiit.dev/arch/notifications)  | [src](src/infra/comms)     | Email, SMS, Slack, Push 
Data                                                                | --                                        | Database modules                 
[db](http://www.kiit.dev/arch/data)                      | [src](src/data/db)                | Easy database operations over JDBC 
[data](http://www.kiit.dev/arch/data)                    | [src](src/data/data)              | Repository pattern for data persistence 
[entities](http://www.kiit.dev/arch/data)                | [src](src/data/entities)          | Light-weight Data-Mapper for models 
Providers                                                              | --                                     | 3rd Party Integrations                
[aws](http://www.kiit.dev/arch/files)                    | [src](src/providers/providers-aws)     | AWS S3, SQS provider files/queues 
[logback](http://www.kiit.dev/utils/logs)                | [src](src/providers/providers-logback) | Logback logging provider 
[datadog](http://www.kiit.dev/arch/tracking)             | [src](src/providers/providers-datadog) | DataDog metrics provider 


# ⁉️ Upcoming
1. Kotlin Flow integration where applicable
2. Kotlin Multi-Platform
3. Code Generators and OpenAPI integrations


# ✉️ Contact
- **author**: Kishore Reddy
- **website**: www.kiit.dev
- **company**: www.codehelix.co
- **email**: kishore  codehelix.co

# ❤️ Like Us ? 
- Support Kiit by clicking the :star: button on the upper right of this page. :v:
- Contribute to continued development via Sponsorship ( being set up soon )
