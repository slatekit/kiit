---
layout: start_page_mods_utils
title: module Encrypt
permalink: /kotlin-mod-encrypt
---

# Encrypt

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Encryption using AES | 
| **date**| 2018-03-18 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common.encrypt  |
| **source core** | slatekit.common.encrypt.Encryptor.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Encryptor.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Encryptor.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import slatekit.common.encrypt.Encryptor


// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok


```

## Setup
```kotlin


  // SETUP 1: Create your singleton encryptor that can encrypt/decrypt using your custom key/secret.
  // and use it as a singleton.
  object TestEncryptor : Encryptor("wejklhviuxywehjk", "3214maslkdf03292")


  // SETUP 2: Create an instance encryptor
  val encryptor = Encryptor("wejklhviuxywehjk", "3214maslkdf03292")

  

```

## Usage
```kotlin


    // CASE 1: Encrypt using AES ( text is base64 encoded without newlines )
    val input = "basMoAKSKDFJrd789"
    val encrypted = TestEncryptor.encrypt(input)
    println(encrypted)


    // CASE 2: Decrypt using AES
    val decrypted = TestEncryptor.decrypt(encrypted)
    println(decrypted)


    // CASE 3: Ensure decrypted matches original
    println(input == decrypted)


    // CASE 4: Use the EncryptSupportIn trait to have built in encrypt/decrypt methods
    // NOTE: You just have to have an _enc member field
    println( encryptor.encrypt(input))
    println( encryptor.decrypt(encrypted) )
    

```

