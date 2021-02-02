
[![WebSite](https://img.shields.io/badge/site-slatekit-blue)](https://www.slatekit.com)
![Kotlin](https://img.shields.io/badge/kotlin-1.3-orange.svg)
![Apache 2](https://img.shields.io/badge/license-Apache2-green.svg?style=flat)
![Follow us on twitter](https://img.shields.io/badge/twitter-slatekit-blue.svg)
<!-- ![Gradle Release](https://github.com/code-helix/slatekit/workflows/Gradle%20Package/badge.svg) -->

![image](media/slatekit-banner.png)


# About
Slate Kit is a **Kotlin** framework, with simple, light-weight, modular libraries and tools to build **Apps, APIs, CLIs, Jobs**, and more for **Start-ups, Personal projects, Mobile Apps, and SMBs** ( small-medium sized businesses ). These libraries can be used for both **Server and Android** and there are modules for the server that abstract infrastructure ( Queues, Files, SMS, Emails, Alerts ) with integrations for **AWS** ( SQS, S3 ), Twilio ( SMS ), SendGrid ( Email ), Slack ( Alerts ) and more. Multi-platform support is planned as a future state.


# Goals
1. **Simple** : Easy to use, light-weight, modular ( 100% Kotlin based alternative to Spring Framework ) 
2. **Projects**: More than just for APIs, use it for Console apps, Jobs, CLIs.
3. **Usage**  : Designed for Start-Ups, Personal Projects, Mobile Apps, SMB ( small-medium sized businesses )
4. **Full-Stack**: Usable on both Server and Android ( due to being simple/light-weight)
5. **Multiplatform** Kotlin Multi-Platform planned for the future for Javascript, Native, iOS, etc
6. **Modular** : Designed as a set of libraries rather than a typical `"framework"`
7. **Tools** : CLI tools to quickly create new Apps, CLIs, API, Jobs projects
8. **Cloud** : Partial Cloud Provider abstractions for ( Queues, Files, Databases, etc ) 
9. **Defaults** Sensible default implementations, currently support AWS

# Start
You can quickly get started on **Mac OS** using the **Homebrew** installer and create projects using the Slate Kit CLI. Refer to https://www.slatekit.com/start/generators/ for more info.
```bash
brew tap slatekit/slatekit

# NOTE: Install may take a long time on Mac (Catalina) due to issues noted below
brew install slatekit

slatekit new app -name="MyApp1" -package="company1.apps"
slatekit new api -name="MyAPI1" -package="company1.apis"
slatekit new job -name="MyJob1" -package="company1.jobs"
slatekit new env -name="MyApp2" -package="company1.apps"
slatekit new cli -name="MyCLI1" -package="company1.apps"
```

### Notes
1. [Slow HomeBrew post install](https://discussions.apple.com/thread/251258165)
2. [The install may be very slow with MacOS Catalina](https://discourse.brew.sh/t/brew-install-very-slow-pauses-for-long-period-while-executing-usr-bin-sandbox-exec-in-post-install/7423)
3. Ensure security -> privacy -> full disk access -> iterm2 ( of what ever terminal you use )


# Install
You can set up gradle using the example below. You can use as few or as many slatekit modules as you need. 

[ ![Latest Version](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-results/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-results/_latestVersion)

```groovy
repositories {
    jcenter()
    mavenCentral()
    maven { url  "https://dl.bintray.com/codehelixinc/slatekit" }
}
dependencies {
	// slatekit-results: Result<T,E> to model successes/failures with optional status codes
    	compile 'com.slatekit:slatekit-results:2.0.0'
}
```

# Links
Some important links / pages for more info.

num | type | link 
----| ------------ | ------------ 
1   | website     | www.slatekit.com                            
2   | start       | www.slatekit.com/start/hello_world           
3   | modules     | https://www.slatekit.com/arch/overview/             
4   | releases    | https://github.com/slatekit/slatekit/releases 
5   | issues      | https://github.com/slatekit/slatekit/issues   
6   | discuss     | https://github.com/slatekit/slatekit/discussions   
7   | license     | http://www.slatekit.com/arch/license           
8   | utilities   | http://www.slatekit.com/utils/overview          
9   | standards   | http://www.slatekit.com/more/standards  
10  | packages    | http://dl.bintray.com/codehelixinc/slatekit
11  | bintray     | https://bintray.com/codehelixinc/slatekit


# Modules
Slate Kit contains many useful architecture components, utilities and applications features. Many of the modules are organized into logical groups and the entire design can be visualized in this diagram

![image](doc/diagrams/slatekit-overview.png)

Here are some of the main ones:

docs | source | desc | download
------------ | ------------ | ------------- | ------------- 
[slatekit-results](http://www.slatekit.com/utils.html)                  | [src](src/lib/kotlin/slatekit-result)  | Accurately model successes/failures | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-results/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-results/_latestVersion)
[slatekit-common](http://www.slatekit.com/utils.html)                  | [src](src/lib/kotlin/slatekit-common)  | utilities for android + server | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-common/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-common/_latestVersion)
[slatekit-app](http://www.slatekit.com/app.html)                    | [src](src/lib/kotlin/slatekit-app)     | application template | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-app/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-core/_latestVersion)
[slatekit-cli](http://www.slatekit.com/cli.html)                    | [src](src/lib/kotlin/slatekit-cli)    | command line interface template | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-cli/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-core/_latestVersion)
[slatekit-apis](http://www.slatekit.com/kotlin-core-apis.html)         | [src](src/lib/kotlin/slatekit-apis)   | api container | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-apis/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-apis/_latestVersion)
[slatekit-jobs](http://www.slatekit.com/kotlin-core-workers.html)    | [src](src/lib/kotlin/slatekit-jobs)  | background jobs for persistant job queues | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-jobs/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-jobs/_latestVersion)
[slatekit-core](http://www.slatekit.com/infra.html)                    | [src](src/lib/kotlin/slatekit-core)    | architecture components | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-core/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-core/_latestVersion)
[slatekit-notifications](http://www.slatekit.com/infra.html)                    | [src](src/lib/kotlin/slatekit-notifications)    | Apache2.0    | alerts, emails, sms, push notifications | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-notifications/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-notifications/_latestVersion)
[slatekit-db](http://www.slatekit.com/utils.html)                  | [src](src/lib/kotlin/slatekit-db)  |  database utilities | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-common/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-common/_latestVersion)
[slatekit-data](http://www.slatekit.com/kotlin-core-data.html)      | [src](src/lib/kotlin/slatekit-data)  | repository pattern | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-data/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-data/_latestVersion)
[slatekit-entities](http://www.slatekit.com/kotlin-core-orm.html)      | [src](src/lib/kotlin/slatekit-entities)  | Standardized entity repository pattern | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-entities/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-entities/_latestVersion)
[slatekit-meta](http://www.slatekit.com/utils.html)                    | [src](src/lib/kotlin/slatekit-meta)   | meta/reflection utils | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-meta/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-meta/_latestVersion)
[slatekit-integration](https://www.slatekit.com)                       | [src](src/lib/kotlin/slatekit-integration) | integration components | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-integration/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-integration/_latestVersion)
[slatekit-providers-aws](http://www.slatekit.com/infra.html)                   | [src](src/ext/kotlin/slatekit-providers-aws)    | clouder services ( AWS ) | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-providers-aws/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-providers-aws/_latestVersion)


**Links**:
1. https://mariadb.com/bsl-faq-adopting/
2. https://blog.adamretter.org.uk/business-source-license-adoption/


# Upcoming
1. Additional AWS abstractions/integrations ( Documents, Streams )
2. CI/CD improvements ( lint tools, etc ) 
3. Kotlin Flow integration where applicable
4. Kotlin Multi-Platform
5. Code Generators and OpenAPI integrations
6. Docker, Kubernetes build scripts
7. Slate Kit MBaaS ( Mobile Backend as a Service )

# MBaaS
We are working on an Slate Kit based **MBaaS** ( Mobile Backend as a Service ) that cane be **self-hosted or cloud deployed.** This is meant to be an alternative to **Google Firebase**, with default integration with AWS for things like Files(S3), Queues(SQS), Docs(DynamoDB), and support for sending emails, sms, notifications and many more features. 


# Contact
- **author**: Kishore Reddy
- **website**: www.slatekit.com
- **company**: www.codehelix.co
- **email**: kishore  codehelix.co

# Like Us ? :heart:
- Support Slate Kit by clicking the :star: button on the upper right of this page. :v:
- Contribute to continued development via Sponsorship ( being set up soon )
