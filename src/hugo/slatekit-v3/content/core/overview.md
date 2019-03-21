---
title: "Features"
date: 2019-03-17T14:30:52-04:00
draft: true
---

# Overview
Slate Kit offers <strong>several features</strong> designed as modular <strong>Architecture components</strong>. These are designed are a collection of Kotlin libraries that help you to quickly build fully featured, testable, 
and scalable applications on the JVM. Some components are sophisticated enough to have their own module/project. Other smaller ones are
located in the <a class="url-ch" href="#project">slateKit-core</a> project.
 Refer to each component for more info. Click any of the modules below for more info.
 {{% break %}}
{{< highlight kotlin >}}

     
    repositories {
        maven { url  "https://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // For results ( modeling success/failures with optional status codes )
        compile 'com.slatekit:slatekit-results:0.9.9'

        // For app ( application template with args, cli, envs, logs, help )
        compile 'com.slatekit:slatekit-app:0.9.9'

        // Click on components for more info or see setup page
    }

{{< /highlight >}}

<section id="services" class="services">
  <div class="container">
      <div class="row text-center">
            <!-- Item -->
          <div class="item col-lg-4 col-md-6 wow fadeInUp">
              <a href="core/kotlin-result.html"><img src="assets/media/img/white/target.png" alt="" class="img-fluid"></a>
              <div><a href="core/kotlin-apis.html"><h3>Results</h3></a></div>
              <p>Models <strong>successes and failures</strong> with <strong>optional</strong> status codes. 
                 See <a class="url-ch" href="core/kotlin-apis.html">concepts</a>, 
                 <a class="url-ch" href="core/kotlin-api.html">example</a> and 
                 <a class="url-ch" href="releases.html#release-downloads">sample apps</a>.
              </p>
          </div><!-- End Item -->
          <!-- Item -->
          <div class="item col-lg-4 col-md-6 wow fadeInUp">
              <a href="core/kotlin-apis.html"><img src="assets/media/img/white/webapi.png" alt="" class="img-fluid"></a>
              <div><a href="core/kotlin-apis.html"><h3>APIs</h3></a></div>
              <p>A <strong>protocol independent</strong> approach to building APIs in Slate so they run as Web APIs or on the CLI. 
                 See <a class="url-ch" href="core/kotlin-apis.html">concepts</a>, 
                 <a class="url-ch" href="core/kotlin-api.html">example</a> and 
                 <a class="url-ch" href="releases.html#release-downloads">sample apps</a>.
              </p>
          </div><!-- End Item -->
          <!-- Item -->
          <div class="item col-lg-4 col-md-6 wow fadeInUp">
              <a href="core/kotlin-app.html"><img src="assets/media/img/white/desktop.png" alt="" class="img-fluid"></a>
              <div><a href="core/kotlin-app.html"><h3>App</h3></a></div>
              <p>A <strong>base App</strong> for Console, CLI, Batch, or Server apps with support for environments, logging, configs and more.
                 See <a class="url-ch" href="core/kotlin-app.html">concepts</a>, 
                 <a class="url-ch" href="core/kotlin-app.html">example</a> and 
                 <a class="url-ch" href="releases.html#release-downloads">sample apps</a>.
              </p>
          </div><!-- End Item -->
          <!-- Item -->
          <div class="item col-lg-4 col-md-6 wow fadeInUp">
              <a href="core/kotlin-cli.html"><img src="assets/media/img/white/terminal.png" alt="" class="img-fluid"></a>
              <div><a href="core/kotlin-cli.html"><h3>CLI</h3></a></div>
              <p>Command line access to your services, apis. Extend the Shell app, 
                 register apis, and have the shell marshal commands via reflection.
                 See <a class="url-ch" href="core/kotlin-cli.html">example</a>.
              </p>
          </div><!-- End Item -->

          <!-- Item -->
          <div class="item col-lg-4 col-md-6 wow fadeInUp">
              <a href="core/kotlin-cache.html"><img src="assets/media/img/white/lightning.png" alt="" class="img-fluid"></a>
              <div><a href="core/kotlin-cache.html"><h3>Cache</h3></a></div>
              <p>Light-weight cache to load, store, and refresh data, with support 
                for metrics and time-stamps. Default in-memory implementation available. 
                See <a class="url-ch" href="core/kotlin-cache.html">example</a> 
              </p>
          </div><!-- End Item -->
          <!-- Item -->
          <div class="item col-lg-4 col-md-6 wow fadeInUp">
              <a href="core/kotlin-cmd.html"><img src="assets/media/img/white/command2.png" alt="" class="img-fluid"></a>
              <div><a href="core/kotlin-cmd.html"><h3>Commands</h3></a></div>
              <p>A variation to the command pattern to support ad-hoc execution of code, 
                with support for metrics and time-stamps. 
                See <a class="url-ch" href="core/kotlin-cmd.html">example</a> 
              </p>
          </div><!-- End Item -->
          <!-- Item -->
          <div class="item col-lg-4 col-md-6 wow fadeInUp">
              <a href="core/kotlin-ctx.html"><img src="assets/media/img/white/connected.png" alt="" class="img-fluid"></a>
              <div><a href="core/kotlin-ctx.html"><h3>Context</h3></a></div>
              <p>A container to hold common dependencies like configs, logger, encryptor, etc, 
                to make them easily available to other components.
                 See <a class="url-ch" href="core/kotlin-ctx.html">example</a> for setup and usage
              </p>
          </div><!-- End Item -->


          <!-- Item -->
          <div class="item col-lg-4 col-md-6 wow fadeInUp">
              <a href="core/kotlin-email.html"><img src="assets/media/img/white/email.png" alt="" class="img-fluid"></a>
              <div><a href="core/kotlin-email.html"><h3>Email</h3></a></div>
              <p>An email service to send emails with support for templates, with a 
                 default implementation for SendGrid.
                 See <a class="url-ch" href="core/kotlin-email.html">example</a> 
                 for details on setup and usage</a>.
              </p>
          </div><!-- End Item -->
          <!-- Item -->
          <div class="item col-lg-4 col-md-6 wow fadeInUp">
              <a href="core/kotlin-aws-s3.html"><img src="assets/media/img/white/folders.png" alt="" class="img-fluid"></a>
              <div><a href="core/kotlin-aws-s3.html"><h3>Files</h3></a></div>
              <p>A simplified interface and abstraction for persistent file storage. 
                We offer built in support for <strong>AWS S3</strong> to store files. 
                See <a class="url-ch" href="core/kotlin-aws-s3.html">example</a>
              </p>
          </div><!-- End Item -->
          <!-- Item -->
          <div class="item col-lg-4 col-md-6 wow fadeInUp">
              <a href="core/kotlin-notifications.html"><img src="assets/media/img/white/speaker.png" alt="" class="img-fluid"></a>
              <div><a href="core/kotlin-notifications.html"><h3>Notifications</h3></a></div>
              <p>A push notification abstraction primarily for mobile based 
                push notifications. This component is a work-in-progress and not 
                currently available.
              </p>
          </div><!-- End Item -->


          <!-- Item -->
          <div class="item col-lg-4 col-md-6 wow fadeInUp">
              <a href="core/kotlin-orm.html"><img src="assets/media/img/white/layers.png" alt="" class="img-fluid"></a>
              <div><a href="core/kotlin-orm.html"><h3>ORM</h3></a></div>
              <p>Light-weight domain-driven ORM to map entities to tables. And a database utility for querying, updating data.
                 See <a class="url-ch" href="core/kotlin-orm.html">ORM</a>, 
                 <a class="url-ch" href="core/kotlin-orm-service.html">example</a> and 
                 <a class="url-ch" href="releases.html#release-downloads">sample apps</a>.
              </p>
          </div><!-- End Item -->
          <!-- Item -->
          <div class="item col-lg-4 col-md-6 wow fadeInUp">
              <a href="core/kotlin-aws-sqs.html"><img src="assets/media/img/white/queue.png" alt="" class="img-fluid"></a>
              <div><a href="core/kotlin-aws-sqs.html"><h3>Queues</h3></a></div>
              <p>A simplified interface and abstraction for persistent queues. 
                We offer built in support for <strong>AWS SQS</strong> and in-memory queue. 
                See <a class="url-ch" href="core/kotlin-aws-sqs.html">example</a>
              </p>
          </div><!-- End Item -->
          <!-- Item -->
          <div class="item col-lg-4 col-md-6 wow fadeInUp">
              <a href="core/kotlin-server.html"><img src="assets/media/img/white/server.png" alt="" class="img-fluid"></a>
              <div><a href="core/kotlin-server.html"><h3>Server</h3></a></div>
              <p>A base Web Server to host your <a class="url-ch" href="core/kotlin-server.html">protocol independent APIs</a> 
                with built in support for many features. 
                See <a class="url-ch" href="core/kotlin-server.html">concepts</a>, 
                <a class="url-ch" href="core/kotlin-server.html">example</a> and 
                <a class="url-ch" href="releases.html#release-downloads">sample apps</a>
              </p>
          </div><!-- End Item -->


          <!-- Item -->
          <div class="item col-lg-4 col-md-6 wow fadeInUp">
              <a href="core/kotlin-sms.html"><img src="assets/media/img/white/mobile.png" alt="" class="img-fluid"></a>
              <div><a href="core/kotlin-sms.html"><h3>SMS</h3></a></div>
              <p>An SMS Service to send text messages to mobile phones, with support for templates, with a default implementation using Twilio.
                 See <a class="url-ch" href="core/kotlin-sms.html">example</a> for setup and usage. 
              </p>
          </div><!-- End Item -->
          <!-- Item -->
          <div class="item col-lg-4 col-md-6 wow fadeInUp">
              <a href="core/kotlin-workers.html"><img src="assets/media/img/white/gears.png" alt="" class="img-fluid"></a>
              <div><a href="core/kotlin-workers.html"><h3>Workers</h3></a></div>
              <p>Background workers for long-running and continuous processing. 
                Support for pausing, stopping, resuming workers, metrics and queues. 
                See <a class="url-ch" href="core/kotlin-workers.html">example</a>
              </p>
          </div><!-- End Item -->
      </div>
  </div>
</section>


# Sources
Some smaller Slate Kit components listed here are available in the core module. Click on any of the modules above for more info.
{{% sk-module 
    name="Core"
    package="slatekit.core"
    jar="slatekit.core.jar"
    git="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-core"
    gitAlias="slatekit-core"
    url="core/overview"
    uses="slatekit-results, slatekit-common, slatekit-meta"
    exampleUrl="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples"
    exampleFileName="Misc examples"
%}}
