---
title: "Utilities"
date: 2019-03-17T14:30:52-04:00
draft: true
---
# Overview
Slate Kit <strong>Utilities</strong> supplement the existing Kotlin standard library by offering a powerful set of general purpose Kotlin components that can be used for any application. Many of these components are located in the <a class="url-ch" href="#project">SlateKit.Common</a> project. However, there are a few components located in other projects. Refer to each utility component for more info.
{{% break %}}

## Setup
You can setup the slatekit.common project with the configuration below.
{{< highlight kotlin >}}

    // other setup ...
    repositories {
        maven { url  "https://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // slatekit-common: Utilities for Android or Server
        compile 'com.slatekit:slatekit-common:1.0.0'
    }

{{< /highlight >}}

{{% break %}}

## Module
Links to this modules info. Click on any component icon below for example on how to use it.
{{% sk-module 
    name="Common"
    package="slatekit.common"
    jar="slatekit.common.jar"
    git="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common"
    gitAlias="slatekit-common"
    url="utils/utils.html"
    uses="slatekit-results"
    license="Apache 2.0"
    exampleUrl="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples"
    exampleFileName="All Examples"
%}}


<section id="services" class="integration">
  <div class="container">
    <div class="heading text-center wow fadeInUp">
            <h2>Several Utilities</h2>
            <p>Many pre-built utilities to support your Server application or Android client.</p>
        </div>
        
      <div class="row text-center">
          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/args"><img src="assets/media/img/white/gears.png" width="60" alt=""></a>         
              <a href="utils/args"><h3>Args</h3></a>
              <p>A lexical command line arguments parser</p>
            </div>
          </div>
          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
                <a href="utils/auth"><img src="assets/media/img/white/male.png" width="60px" alt=""></a>            
                <a href="utils/auth"><h3>Auth</h3></a>
                <p>Easily access authentication information</p>
            </div>
          </div>
          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/config"><img src="assets/media/img/white/settings.png" width="60px" alt=""></a>          
              <a href="utils/config"><h3>Config</h3></a>
              <p>Java props with types, lists, encryption, and more</p>
            </div>
          </div>

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/console"><img src="assets/media/img/white/console.png" width="60" alt=""></a>          
              <a href="utils/console"><h3>Console</h3></a>
              <p>Console writer with semantics and colors</p>
            </div>
          </div>

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/data"><img src="assets/media/img/white/layers.png" width="60" alt=""></a>          
              <a href="utils/data"><h3>Database</h3></a>
              <p>JDBC database access and utility functions</p>
            </div>
          </div>

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/datetime"><img src="assets/media/img/white/calendar.PNG" width="60" alt=""></a>            
              <a href="utils/datetime"><h3>DateTime</h3></a>
              <p>Easier Java 8 DateTime and Timezone usage</p>
            </div>
          </div>
          

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/encrypt"><img src="assets/media/img/white/lock.png" width="60" alt=""></a>         
              <a href="utils/encrypt"><h3>Encrypt</h3></a>
              <p>AES encryption and decryption support and utilities</p>
            </div>
          </div>  
          

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/env"><img src="assets/media/img/white/nodes.png" width="60" alt=""></a>            
              <a href="utils/env"><h3>Env</h3></a>
              <p>Environment selector ( dev, qa, stg, prod )</p>
            </div>
          </div>   
          

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/env"><img src="assets/media/img/white/folders.png" width="60" alt=""></a>          
              <a href="utils/folders"><h3>Folders</h3></a>
              <p>Standardized runtime app folders and structure.</p>
            </div>
          </div>             

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/info"><img src="assets/media/img/white/info.png" width="60" alt=""></a>            
              <a href="utils/info"><h3>Info</h3></a>
              <p>Useful information about app, host, runtime</p>
            </div>
          </div>

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/lex"><img src="assets/media/img/white/document.png" width="60" alt=""></a>         
              <a href="utils/lex"><h3>Lex</h3></a>
              <p>A light-weight lexer to parse text into tokens</p>
            </div>
          </div>            

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/logger"><img src="assets/media/img/white/error.png" width="60" alt=""></a>         
              <a href="utils/logger"><h3>Logs</h3></a>
              <p>Simple, extensible, customizable logger</p>
            </div>
          </div>

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/model"><img src="assets/media/img/white/prototype.png" width="60" alt=""></a>          
              <a href="utils/model"><h3>Model</h3></a>
              <p>Builts schemas to map models and generate code</p>
            </div>
          </div>

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/random"><img src="assets/media/img/white/question.png" width="60" alt=""></a>          
              <a href="utils/random"><h3>Random</h3></a>
              <p>Random generator for strings, numbers, alpha, guids</p>
            </div>
          </div>

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/reflect"><img src="assets/media/img/white/search.png" width="60" alt=""></a>           
              <a href="utils/reflect"><h3>Reflection</h3></a>
              <p>Reflection utils for classes, methods, annotations</p> 
            </div>
          </div>

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/request"><img src="assets/media/img/white/request.png" width="60" alt=""></a>          
              <a href="utils/request"><h3>Request</h3></a>
              <p>Abstracts and represents HTTP and CLI requests</p> 
            </div>
          </div>

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/results"><img src="assets/media/img/white/result.png" width="60" alt=""></a>           
              <a href="utils/results"><h3>Result</h3></a>
              <p>Models response with success and failures.</p> 
            </div>
          </div> 
          

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/serialization"><img src="assets/media/img/white/print.png" width="60" alt=""></a>          
              <a href="utils/serialization"><h3>Serialization</h3></a>
              <p>Csv, Json, HoCon serialization of data classes</p>
            </div>
          </div>
          

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/serialization"><img src="assets/media/img/white/print.png" width="60" alt=""></a>     
              <a href="utils/smartstrings"><h3>Smart Strings</h3></a>
              <p>Store, validate, describe strongly formatted strings</p>
            </div>
          </div>
          

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/templates"><img src="assets/media/img/white/share.png" width="60" alt=""></a>          
              <a href="utils/templates"><h3>Templates</h3></a>
              <p>A micro templating system for emails, sms and short messages</p>
            </div>
          </div>
          

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/todo"><img src="assets/media/img/white/notes.png" width="60" alt=""></a>           
              <a href="utils/todo"><h3>Notes</h3></a>
              <p>Typesafe, programmatic approach to marking Notes/Todos in code</p>
            </div>
          </div>
          

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/utils"><img src="assets/media/img/white/settings2.png" width="60" alt=""></a>          
              <a href="utils/utils"><h3>Utils</h3></a>
              <p>Several other smaller utilities and components</p>
            </div>
          </div>
          

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href="utils/validations"><img src="assets/media/img/white/checklist.png" width="60" alt=""></a>            
              <a href="utils/validations"><h3>Validations</h3></a>
              <p>Functional validation, RegEx patterns, and helpers</p>
            </div>
          </div>
          

          <div class="col-md-4 wow fadeInUp">
            <div class="box text-center">
              <a href=""><img src="assets/media/img/white/gears.png" width="60" alt=""></a>         
              <a href=""><h3>Extensions</h3></a>
              <p>Extension methods for various Kotlin types and classes</p>
            </div>
          </div>
      </div>
  </div>
</section>

