{{< highlight kotlin >}}

    // other setup ...
    repositories {
        maven { url  "https://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // For results ( modeling success/failures with optional status codes )
        compile 'com.slatekit:slatekit-results:0.9.9'

        // For the smaller components ( context, email, sms )
        compile 'com.slatekit:slatekit-core:0.9.9'

        // For app ( application template with args, cli, envs, logs, help )
        compile 'com.slatekit:slatekit-app:0.9.9'

        // For CLI ( command line interaction template )
        compile 'com.slatekit:slatekit-cli:0.9.9'

        // For Entities ( General Domain-Driven Entity/Service/Repo pattern )
        compile 'com.slatekit:slatekit-entities:0.9.9'

        // For Workers ( Background workers for persistant job queues - like Ruby Resque )
        compile 'com.slatekit:slatekit-workers:0.9.9'
    }

{{< /highlight >}}