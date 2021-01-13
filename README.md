
[![WebSite](https://img.shields.io/badge/site-slatekit-blue)](https://www.slatekit.com)
![Kotlin](https://img.shields.io/badge/kotlin-1.3-orange.svg)
![Apache 2](https://img.shields.io/badge/license-Apache2-brightgreen.svg?style=flat)
![BSL ](https://img.shields.io/badge/license-bsl__1.0-yellow.svg?style=flat)
[![Follow us on twitter](https://img.shields.io/badge/twitter-slatekit-blue.svg)]
![Gradle Release](https://github.com/code-helix/slatekit/workflows/Gradle%20Package/badge.svg)

![image](media/slatekit-banner.png)


# About
Slate Kit is a **Kotlin** Tool-Kit, a simple, light-weight, modular framework to build **Apps, APIs, CLIs, Jobs**, and more for **Start-ups, Personal projects, and SMBs** ( small-medium sized businesses ). These libraries can be used for both Server and Android and there are modules for the server that abstract infrastructure ( Queues, Files, SMS, Emails, Alerts ) with integrations for **AWS** ( SQS, S3 ), Twilio ( SMS ), SendGrid ( Email ), Slack ( Alerts ) and more. Multi-platform support is planned as a future state.


# Goals
1. Simple, light-weight, module, 100% Kotlin version of Spring Framework
2. Targeted for Start-Ups, Personal Projects, SMB 
3. Ability to use many of these libraries for both Server and Android ( due to being simple/light-weight)
4. Will support Multi-Platform in the future
5. Designed as a set of libraries rather than a typical `"framework"`
6. CLI tools to quickly create new Apps, CLIs, API, Jobs projects

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
1. Ensure security -> privacy -> full disk access -> iterm2 ( of what ever terminal you use )
2. [Slow HomeBrew post install](https://discussions.apple.com/thread/251258165)
3. [The install may be very slow with MacOS Catalina](https://discourse.brew.sh/t/brew-install-very-slow-pauses-for-long-period-while-executing-usr-bin-sandbox-exec-in-post-install/7423)


# Install
You can set up gradle using the example below. You can use as few or as many slatekit modules as you need. 

```groovy
repositories {
    jcenter()
    mavenCentral()
    maven { url  "https://dl.bintray.com/codehelixinc/slatekit" }
}
dependencies {
	// slatekit-results: Result<T,E> to model successes/failures with optional status codes
    	compile 'com.slatekit:slatekit-results:1.34.0'
}
```

# Links
Some important links / pages for more info.

num | type | link 
----| ------------ | ------------ 
1   | website     | www.slatekit.com                            
2   | quick-start | www.slatekit.com/start/hello_world           
3   | modules     | https://www.slatekit.com/arch/overview/             
4   | releases    | https://github.com/code-helix/slatekit/releases 
5   | issues      | https://github.com/code-helix/slatekit/issues   
6   | license     | http://www.slatekit.com/arch/license           
7   | utilities   | http://www.slatekit.com/utils/overview          
8   | standards   | http://www.slatekit.com/more/standards  
9   | packages    | http://dl.bintray.com/codehelixinc/slatekit


# Modules
Slate Kit contains many useful architecture components, utilities and applications features. Many of the modules are organized into logical groups and the entire design can be visualized in this diagram

![image](doc/diagrams/slatekit-overview.png)

Here are some of the main ones:

docs | source | license | desc | download
------------ | ------------ | ------------- | ------------- | -------------
[slatekit-results](http://www.slatekit.com/utils.html)                  | [src](src/lib/kotlin/slatekit-result)  | Apache2.0     | Accurately model successes/failures | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-results/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-results/_latestVersion)
[slatekit-common](http://www.slatekit.com/utils.html)                  | [src](src/lib/kotlin/slatekit-common)  | Apache2.0    | utilities for android + server | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-common/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-common/_latestVersion)
[slatekit-app](http://www.slatekit.com/app.html)                    | [src](src/lib/kotlin/slatekit-app)     | Apache2.0   | application template | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-app/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-core/_latestVersion)
[slatekit-cli](http://www.slatekit.com/cli.html)                    | [src](src/lib/kotlin/slatekit-cli)    | Apache2.0    | command line interface template | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-cli/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-core/_latestVersion)
[slatekit-apis](http://www.slatekit.com/kotlin-core-apis.html)         | [src](src/lib/kotlin/slatekit-apis)   | AGPLv3     | api container | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-apis/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-apis/_latestVersion)
[slatekit-jobs](http://www.slatekit.com/kotlin-core-workers.html)    | [src](src/lib/kotlin/slatekit-jobs)  | AGPLv3   | background jobs for persistant job queues | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-jobs/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-jobs/_latestVersion)
[slatekit-db](http://www.slatekit.com/utils.html)                  | [src](src/lib/kotlin/slatekit-db)  | Apache2.0    | database utilities | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-common/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-common/_latestVersion)
[slatekit-meta](http://www.slatekit.com/utils.html)                    | [src](src/lib/kotlin/slatekit-meta)   | Apache2.0     | meta/reflection utils | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-meta/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-meta/_latestVersion)
[slatekit-core](http://www.slatekit.com/infra.html)                    | [src](src/lib/kotlin/slatekit-core)    | Apache2.0    | architecture components | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-core/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-core/_latestVersion)
[slatekit-notifications](http://www.slatekit.com/infra.html)                    | [src](src/lib/kotlin/slatekit-notifications)    | Apache2.0    | alerts, emails, sms, push notifications | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-notifications/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-notifications/_latestVersion)
[slatekit-entities](http://www.slatekit.com/kotlin-core-orm.html)      | [src](src/lib/kotlin/slatekit-entities)  | Apache2.0  | Standardized entity repository pattern | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-entities/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-entities/_latestVersion)
[slatekit-orm](http://www.slatekit.com/kotlin-core-orm.html)      | [src](src/lib/kotlin/slatekit-orm)  | Apache2.0  | database entities/orm | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-entities/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-entities/_latestVersion)
[slatekit-integration](https://www.slatekit.com)                       | [src](src/lib/kotlin/slatekit-integration) | Apache2.0| integration components | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-integration/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-integration/_latestVersion)
[slatekit-providers-aws](http://www.slatekit.com/infra.html)                   | [src](src/ext/kotlin/slatekit-providers-aws)    | Apache2.0   | clouder services ( AWS ) | [ ![Download](https://api.bintray.com/packages/codehelixinc/slatekit/slatekit-providers-aws/images/download.svg) ](https://bintray.com/codehelixinc/slatekit/slatekit-providers-aws/_latestVersion)



# License
Slate Kit has a unique **Dual License** approach using **BSL** (Business Source License) and **Apache 2.0** using the `Additional Use Grants` parameter of the BSL. This is a reletively new approach and license and something we are experimenting with. At the moment, we are making this framework as permissive as possible, and more details will come. However, we 100% plan to allow this to be used as Apache 2.0 immediately and indefinitely for the groups listed in the Apache 2.0 section below.

**Apache 2.0**: This is effectively Apache 2.0 for the following groups and usage
1. Non-Profits ( .org ) 
2. Educational Institutions ( .edu )
3. Start Ups ( Pre Series A and not in the cloud-provider space )
4. Personal projects
5. Non-Cloud Provider companies ( basically any group not building cloud provider infrastructure )

**BSL 1.1** 
For Cloud-Providers such as AWS, Google Cloud, Azure, Digital Ocean, etc, this is a BSL license. It can not be used for production usage without obtaining a license.

**Notes**:
1. BSL is a relatively new license type.
2. BSL is technicall NOT an open-source" license but rather `source available`
3. BSL has parameters `Change Date | Change License | Additional Use` that convert it to open source
4. BSL `Additional Use Grant` is being used in to grant production use and further rights to various groups

**Links**:
1. https://mariadb.com/bsl-faq-adopting/
2. https://blog.adamretter.org.uk/business-source-license-adoption/


**Parameters**
1. **Change Date** : Jan 1, 2024  - Converts to Apache 2.0 on Jan 1, 2024
2. **Change License** :  Apache 2.0 - Type of license this BSL will convert to
3. **Additional Use Grants** : Grants production use immediately and indefinitely as Apache 2.0 for non-profits (.org), edus (.edu), startups (pre-Series A), personal projects, and any company not in the cloud-provider space like AWS, Google Cloud, Azure, Digital Ocean, etc.


# Upcoming
1. Additional AWS abstractions/integrations ( Documents, Streams )
2. Kotlin Flow integration where applicable
3. Kotlin Multi-Platform
4. Code Generators and OpenAPI integrations
5. Docker, Kubernetes integrations


# Contact
- **author**: Kishore Reddy
- **website**: www.slatekit.com
- **company**: www.codehelix.co
- **email**: kishore  codehelix.co

# Like Us ? :heart:
- Support Slate Kit by clicking the :star: button on the upper right of this page. :v:
- Contribute to continued development via Sponsorship ( being set up soon )
