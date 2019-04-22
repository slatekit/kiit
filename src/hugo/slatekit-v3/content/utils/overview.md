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

<section id="services" class="services">
  <div class="container">
      <div class="row text-center">
              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                        <div class="icon-wrap">
                            <a href="utils/args"><img src="assets/media/img/white/gears.png" width="80" alt=""></a>         
                        </div>
                        <a href="utils/args"><h4>Args</h4></a>
                        <p>A lexical command line arguments parser</p>
              </div>

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/auth"><img src="assets/media/img/white/male.png" width="70" alt=""></a>            
                        </div>
                <a href="utils/auth"><h4>Auth</h4></a>
                <p>Easily access authentication information</p>
              </div>

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/config"><img src="assets/media/img/white/settings.png" width="65" alt=""></a>          
                        </div>
                <a href="utils/config"><h4>Config</h4></a>
                <p>Java props with types, lists, encryption, and more</p>
              </div>

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/console"><img src="assets/media/img/white/console.png" width="80" alt=""></a>          
                        </div>
                <a href="utils/console"><h4>Console</h4></a>
                <p>Console writer with semantics and colors</p>
              </div>

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/data"><img src="assets/media/img/white/layers.png" width="80" alt=""></a>          
                        </div>
                <a href="utils/data"><h4>Database</h4></a>
                <p>JDBC database access and utilities</p>
              </div>

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/datetime"><img src="assets/media/img/white/calendar.PNG" width="60" alt=""></a>            
                        </div>
                <a href="utils/datetime"><h4>DateTime</h4></a>
                <p>Easier Java 8 DateTime and Timezone usage</p>
              </div>
              

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/encrypt"><img src="assets/media/img/white/lock.png" width="80" alt=""></a>         
                        </div>
                <a href="utils/encrypt"><h4>Encrypt</h4></a>
                <p>AES encryption/decryption support</p>
              </div>    
              

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/env"><img src="assets/media/img/white/nodes.png" width="80" alt=""></a>            
                        </div>
                <a href="utils/env"><h4>Env</h4></a>
                <p>Environment selector ( dev, qa, stg, prod )</p>
              </div>     
              

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/env"><img src="assets/media/img/white/folders.png" width="80" alt=""></a>          
                        </div>
                <a href="utils/folders"><h4>Folders</h4></a>
                <p>Standardized runtime app folders and structure.</p>
              </div>              

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/info"><img src="assets/media/img/white/info.png" width="65" alt=""></a>            
                        </div>
                <a href="utils/info"><h4>Info</h4></a>
                <p>Useful information about app, host, runtime</p>
              </div>

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/lex"><img src="assets/media/img/white/document.png" width="80" alt=""></a>         
                        </div>
                <a href="utils/lex"><h4>Lex</h4></a>
                <p>A light-weight lexer to parse text into tokens</p>
              </div>             

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/logger"><img src="assets/media/img/white/error.png" width="60" alt=""></a>         
                        </div>
                <a href="utils/logger"><h4>Logs</h4></a>
                <p>Simple, extensible, customizable logger</p>
              </div>

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/model"><img src="assets/media/img/white/prototype.png" width="80" alt=""></a>          
                        </div>
                <a href="utils/model"><h4>Model</h4></a>
                <p>Builts schemas to map models and generate code</p>
              </div>

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/random"><img src="assets/media/img/white/question.png" width="80" alt=""></a>          
                        </div>
                <a href="utils/random"><h4>Random</h4></a>
                <p>Random generator for strings, numbers, alpha, guids</p>
              </div>

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/reflect"><img src="assets/media/img/white/search.png" width="80" alt=""></a>           
                        </div>
                <a href="utils/reflect"><h4>Reflection</h4></a>
                <p>Reflection utils for classes, methods, annotations</p> 
              </div>

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/request"><img src="assets/media/img/white/request.png" width="80" alt=""></a>          
                        </div>
                <a href="utils/request"><h4>Request</h4></a>
                <p>Abstracts and represents HTTP and CLI requests</p> 
              </div> 

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/results"><img src="assets/media/img/white/result.png" width="80" alt=""></a>           
                        </div>
                <a href="utils/results"><h4>Result</h4></a>
                <p>Models response with success and failures.</p> 
              </div> 
              

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/serialization"><img src="assets/media/img/white/print.png" width="80" alt=""></a>          
                        </div>
                <a href="utils/serialization"><h4>Serialization</h4></a>
                <p>Csv, Json, HoCon serialization of data classes</p>
              </div> 
              

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                  <a href="utils/serialization"><img src="assets/media/img/white/print.png" width="80" alt=""></a>     
                </div>
                <a href="utils/smartstrings"><h4>Smart Strings</h4></a>
                <p>Store, validate, describe strongly formatted strings</p>
              </div> 
              

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/templates"><img src="assets/media/img/white/share.png" width="80" alt=""></a>          
                        </div>
                <a href="utils/templates"><h4>Templates</h4></a>
                <p>A micro template system</p>
              </div>
              

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/timer"><img src="assets/media/img/white/timer.png" width="80" alt=""></a>          
                        </div>
                <a href="utils/timer"><h4>Timer</h4></a>
                <p>Timer class to benchmark code</p>
              </div>
              

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/todo"><img src="assets/media/img/white/notes.png" width="80" alt=""></a>           
                        </div>
                <a href="utils/todo"><h4>Todo</h4></a>
                <p>Programmatic approach to marking TODOs in code</p>
              </div>
              

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/utils"><img src="assets/media/img/white/settings2.png" width="80" alt=""></a>          
                        </div>
                <a href="utils/utils"><h4>Utils</h4></a>
                <p>Miscellaneous utilities</p>
              </div>
              

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href="utils/validations"><img src="assets/media/img/white/checklist.png" width="80" alt=""></a>            
                        </div>
                <a href="utils/validations"><h4>Validations</h4></a>
                <p>Functional validation</p>
              </div>
              

              <div class="item col-lg-4 col-md-6 wow fadeInUp">
                <div class="icon-wrap">
                          <a href=""><img src="assets/media/img/white/gears.png" width="80" alt=""></a>         
                        </div>
                <a href=""><h4>Extensions</h4></a>
                <p>Extension methods ( Coming soon )</p>
              </div>

              </div>
  </div>
</section>

# Sources
The Slate Kit Utilities source code is fully available from Git.
{{% sk-module 
    name="Common"
    package="slatekit.common"
    jar="slatekit.common.jar"
    git="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common"
    gitAlias="slatekit-common"
    url="utils/utils.html"
    uses="slatekit-results"
    exampleUrl="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples"
    exampleFileName="All Examples"
%}}


