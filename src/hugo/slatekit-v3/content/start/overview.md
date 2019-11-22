---
title: Overview
date: 2019-03-17T13:02:30-04:00
section_header: Overview
---

# Overiew
  <p>
      Slate Kit is a <strong><a class="url-ch" href="http://www.kotlinlang.org">Kotlin</a></strong> based open-source set of libraries. It is comprised of several projects and modular components that collectively provide pre-built architecture that can be used to build any type of application on the JVM. Slate Kit can also be used as an open-source alternative to FireBase/Parse Beta* as the server-side backend for your mobile / web applications.
  </p>
{{% break %}}

# Goals
<p>
  The Slate Kit framework was designed for and extracted from a sizable and sophisticated production Android Application with a Server backend. Slate Kit is a Kotlin first framework architected with the explicit goals below in mind:
</p>
<table class="table table-bordered table-striped">
    <tr><td><strong>Simple</strong></td><td>Designed with simplicity in mind, modular and very light-weight.</td></tr>
    <tr><td><strong>Kotlin</strong></td><td>100% Kotlin, emphasis on functional, immutable code (pragmatically, without pure FP)</td></tr>
    <tr><td><strong>Shared</strong></td><td>Resonably light-weight, yet suitable for most needs on both Android and Server.</td></tr>
    <tr><td><strong>Platforms</strong></td><td>Long-term goals of making this support Kotlin Multi-Platform/Native</td></tr>
    <tr><td><strong>Costs</strong></td><td>Reducing costs with rapid development, full-stack / sharable code for both Server and Android.</td></tr>
    <tr><td><strong>Portability</strong></td><td>Reasonable abstractions for Cloud components for portability.</td></tr>
</table>
{{% section-end mod="start/overview" %}}

# Uses
<p>
  Slate Kit can be used to quickly build well structured and scalable architecture and applications for different target groups.
</p>
<table class="table table-bordered table-striped">
    <tr><td><strong>Startups</strong></td><td> Start with a strong base architecture to quickly build high-quality MVPs that can scale</td></tr>
    <tr><td><strong>SMB</strong></td><td>For small to medium sized businesses, build backend applications with pre-built application/service templates/components</td></tr>
    <tr><td><strong>Mobile</strong></td><td> Set up the back-end for your mobile apps. Use Kotlin code for both Android and the Server</td></tr>
    <tr><td><strong>Personal</strong></td><td>Learn Kotlin, functional programming, for self-improvement or side projects</td></tr>
    <tr><td><strong>Enterprise</strong></td><td>There are several components in Slate Kit that can currently be used in the Enterprise, such as the App, Utilities, CLI. However, Slate Kit is simpler and thus less comprehensive than enterprises offerings such as Spring.io, Vertx.io, Micronaut.io, all of which are very solid solutions and recommendations for the enterprise.</td></tr>
</table>
{{% section-end mod="start/overview" %}}


# Tech
<p>
  Slate Kit is built for the <strong>JVM</strong> using <strong>100% Kotlin</strong>. There are thin abstractions over some infrastructure services such as Files, queues, docs. Currently, only AWS implementations are available for the infrastructure abstractions. However, in the future, support for Google and Azure cloud services may be implemented. Other services are using directly.
</p>
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Item</strong></td>
        <td><strong>Infrastructure</strong></td>
        <td><strong>Usage</strong></td>
        <td>Provider</td>
    </tr>
    <tr>
        <td><strong>1</strong></td>
        <td><strong>Files</strong></td>
        <td><strong>Abstracted</strong></td>
        <td>AWS S3 ( see {{% sk-link-arch name="files" %}} )</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong>Queues</strong></td>
        <td><strong>Abstracted</strong></td>
        <td>AWS SQS ( see {{% sk-link-arch name="queues" %}} )</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong>Documents</strong></td>
        <td><strong>Abstracted</strong></td>
        <td>AWS Dynamo ( see {{% sk-link-arch name="docs" %}} )</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong>Entities</strong></td>
        <td><strong>Abstracted</strong></td>
        <td>Support for MySql ( see {{% sk-link-arch name="orm" %}} )</td>
    </tr>
    <tr>
        <td><strong>5</strong></td>
        <td><strong>Http Server</strong></td>
        <td><strong>Direct Usage</strong></td>
        <td>Ktor ( from JetBrains ) - used as the API server</td>
    </tr>
    <tr>
        <td><strong>6</strong></td>
        <td><strong>Http Client </strong></td>
        <td><strong>Partially Abstracted</strong></td>
        <td>OkHttp ( see {{% sk-link-arch name="HttpRPC" %}} )</td>
    </tr>
    <tr>
        <td><strong>7</strong></td>
        <td><strong>Metrics</strong></td>
        <td><strong>Partially Abstracted</strong></td>
        <td>Micrometer.io ( see {{% sk-link-arch name="tracking" %}} )</td>
    </tr>
    <tr>
        <td><strong>8</strong></td>
        <td><strong>Logs</strong></td>
        <td><strong>Abstracted</strong></td>
        <td>Logback ( see {{% sk-link-util name="logs" %}} )</td>
    </tr>
    <tr>
        <td><strong>9</strong></td>
        <td><strong>SMS</strong></td>
        <td><strong>Abstracted</strong></td>
        <td>Twilio ( see {{% sk-link-arch name="sms" %}} )</td>
    </tr>
    <tr>
        <td><strong>10</strong></td>
        <td><strong>Email</strong></td>
        <td><strong>Abstracted</strong></td>
        <td>SendGrid ( see {{% sk-link-arch name="email" %}} )</td>
    </tr>
    <tr>
        <td><strong>11</strong></td>
        <td><strong>Alerts</strong></td>
        <td><strong>Abstracted</strong></td>
        <td>Slack Web Hooks ( see {{% sk-link-arch name="alerts" %}} )</td>
    </tr>
</table>
{{% section-end mod="start/overview" %}}


# Components
These are some of the main architecture components available in Slate Kit.
<table class="table table-bordered table-striped">
  <tr>
    <td ><strong>Component</strong></td>
    <td ><strong>Visit</strong></td>
    <td ><strong>About</strong></td>
    <td ><strong>Description</strong></td>
  </tr>
  <tr>
    <td class="text-center"><img src="assets/media/img/white/desktop.png" width="50" alt=""></td>
    <td><strong><a class="url-ch" href="kotlin-core-app.html">App</a></strong></td>
    <td>Base Application</td>
    <td>A powerful base app with support for command line args, environment selection, configs per environment, logging, life-cycle events, encryption, diagnostics and more</td>
  </tr>
  <tr>
    <td class="text-center"><img src="assets/media/img/white/webapi.png" width="50" alt=""></td>
    <td><strong><a class="url-ch" href="kotlin-core-apis.html">API</a></strong></td>
    <td>Protocol Independent APIs</td>
    <td>Easily build protocol independent APIs using simple classes/methods with annotations, that can be hosted / run on both Slate Command Line Shell and/or as Web APIs in the Slate Server </td>
  </tr>
  <tr>
    <td class="text-center"><img src="assets/media/img/white/terminal.png" width="50" alt=""></td>
    <td><strong><a class="url-ch" href="kotlin-mod-shell.html">CLI</a></strong></td>
    <td>Command Line Interface</td>
    <td>A command line interface shell that you can extend to host your "Protocol Independent APIs" and/or hook into to handle and execute any user commands in a shell.</td>
  </tr>
  <tr>
    <td class="text-center"><img src="assets/media/img/white/gears.png" width="50" alt=""></td>
    <td><strong><a class="url-ch" href="arch/jobs">Jobs</a></strong></td>
    <td>Background Jobs</td>
    <td>Backgrounds Jobs / Task Queue library to process one-time, paged, and queue based jobs.</td>
  </tr>
  <tr>
    <td class="text-center"><img src="assets/media/img/white/layers.png" width="50" alt=""></td>
    <td><strong><a class="url-ch" href="arch/orm">ORM</a></strong></td>
    <td>Domain-Driven ORM</td>
    <td>A simple, light-weight, Domain-Driven ORM to map your entities to and from database tables. MySql is currently supported with support for PostGres coming soon.</td>
  </tr>
  <tr>
    <td class="text-center"><img src="assets/media/img/white/server.png" width="50" alt=""></td>
    <td><strong><a class="url-ch" href="kotlin-core-server.html">Server</a></strong></td>
    <td>Web API Server</td>
    <td>A Web API server built using Akka Http that can host your "Protocol Independent APIs". You can extend this server and register your own APIs.</td>
  </tr>
  <tr>
    <td class="text-center"><img src="assets/media/img/white/build.png" width="50" alt=""></td>
    <td><strong><a class="url-ch" href="infra.html">Architecture</a></strong></td>
    <td>Architecture Components</td>
    <td>Architectural abstractions and implementations for Files, Queues, Tasks and more. Files and Queues are abstracted with default implementations for AWS S3, SQS.</td>
  </tr>
  <tr>
    <td class="text-center"><img src="assets/media/img/white/multitool.png" width="50" alt=""></td>
    <td><strong><a class="url-ch" href="utils.html">Utilities</a></strong></td>
    <td>Utility Components</td>
    <td>Many useful utility components and code that can be used for any application. All these are located in the Slate.Common project and independent and modular.</td>
  </tr>
  <tr>
    <td class="text-center"><img src="assets/media/img/white/mobile.png" width="50" alt=""></td>
    <td><strong><a class="url-ch" href="features.html">Mobile</strong></td>
    <td>Mobile / Web Features</td>
    <td>Mobile and web features for most applications, such as Users, Devices, Registration, Invites, Settings and more.</td>
  </tr>
</table>
{{% section-end mod="start/overview" %}}


## Philosophy
{{% sk-philosophy %}}
{{% section-end mod="start/overview" %}}
