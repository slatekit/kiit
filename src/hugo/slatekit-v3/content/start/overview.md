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

# Uses
<p>
  Slate Kit can be used to quickly build well structured and scalable architecture and applications for different target groups.
</p>
<table class="table table-bordered table-striped">
    <tr><td><strong>Startups</strong></td><td> Start with a strong base architecture to quickly build high-quality MVPs that can scale</td></tr>
    <tr><td><strong>SMB</strong></td><td>For small to medium sized businesses, build backend applications with pre-built application/service templates/components</td></tr>
    <tr><td><strong>Mobile</strong></td><td> Set up the back-end for your mobile apps. Use Kotlin code for both Android and the Server</td></tr>
    <tr><td><strong>Personal</strong></td><td>Learn Kotlin, functional programming, for self-improvement or side projects</td></tr>
</table>
{{% section-end mod="start/overview" %}}


# Tech
<p>
  Slate Kit is built for the <strong>JVM</strong> using <strong>100% Kotlin</strong>. Here are some of the other tools/libraries/integrations 
  available with Slate Kit.
</p>
<table class="table table-bordered table-striped">
        <tr><td>Libs</td><td><strong>Kotlin</strong></td><td> All Slate Kit projects/components are in Kotlin</td></tr>
        <tr><td>HTTP</td><td><strong>Ktor</strong></td><td>  Used as the Http server for Kotlin </td></tr>
        <tr><td>DB</td><td><strong>MySql</strong></td><td>The Slate Kit ORM ( Entity Services ) integrates with MySql ( PostGres coming later ) </td></tr>
        <tr><td>Cloud</td><td><strong>AWS</strong></td><td>Integrates with S3, SQS for file storage and queues. </td></tr>
        <tr><td>SMS</td><td><strong>Twilio</strong></td><td>Used for SMS / Text messages </td></tr>
        <tr><td>Email</td><td><strong>SendGrid</strong></td><td>Used for sending email </td></tr>
        <tr><td>Push</td><td><strong>Push</strong></td><td>Integrates with Google Cloud Messaging for Android </td></tr>
</table>
{{% section-end mod="start/overview" %}}


# Components
        
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
    <td class="text-center"><img src="assets/media/img/white/layers.png" width="50" alt=""></td>
    <td><strong><a class="url-ch" href="kotlin-core-orm.html">ORM</a></strong></td>
    <td>Domain-Driven ORM</td>
    <td>A simple, light-weight, Domain-Driven ORM to map your entities to and from database tables. MySql is currently supported with support for MS Sql Server coming soon.</td>
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
<p>
Philosophy, design goals, approaches and standards
</p>
<table class="table table-bordered table-striped">
    <tbody><tr>
      <td ><strong>Tech</strong></td>
      <td>Built for the JVM, with Cloud Integration, and designed to be deployed and hosted anywhere.</td>
    </tr>
    <tr>
      <td><strong>Design</strong></td>
      <td>Library based approach instead of a "Framework". Use as little or as much as you need.
      </td>
    </tr>
    <tr>
      <td><strong>Standards</strong></td>
      <td>Check our <a class="url-ch" href="kotlin-standards.html">coding standards</a></td>
    </tr>
    <tr>
      <td><strong>Approach</strong></td>
      <td><strong>Simplicity</strong> and <strong>Practicality</strong> above all else</td>
    </tr>
    <tr>
      <td><strong>Components</strong></td>
      <td>Composable, single-purpose components as building blocks. Organized into various projects.
      </td>
    </tr>
    <tr>
      <td><strong>Portable</strong></td>
      <td>Designed for low vendor lock-in via a "library" based approach. Easy to get out of this tool-kit</td>
    </tr>
    <tr>
      <td><strong>Minimal</strong></td>
      <td>Dependencies on external, 3rd-party libraries are kept to a minimum</td>
    </tr>
    <tr>
      <td><strong>Open Source</strong></td>
      <td>Hybrid Open-Source. Majority is open-source except for some parts of the <a class="url-ch" href="#project-slate-ext">SlateKit.Ext</a> project 
        containing the mobile / web <a class="url-ch" href="features.html">features</a></td>
    </tr>
    <tr>
      <td><strong>License</strong></td>
      <td>Apache 2.0 : Please check our <a class="url-ch" href="https://github.com/code-helix/slatekit">git repo</a> for more info</td>
    </tr>
    <tr>
      <td><strong>Style</strong></td>
      <td>Emphasis on <strong>immutability</strong> 
        and <strong>functional programming</strong> as much as possible.
        <br>However, this is <strong>NOT</strong> a 100% purely functional code base.
        <br>We also carefully avoid certain language features in specific times/places in favour of simple, understandable, maintainable code.<br>
        </td>
    </tr>
  </tbody>
</table>
{{% section-end mod="start/overview" %}}
