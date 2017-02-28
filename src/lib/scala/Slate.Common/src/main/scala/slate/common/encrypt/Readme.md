---
layout: start_page_mods_utils
title: module Encrypt
permalink: /mod-encrypt
---

# Encrypt

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Encryption using AES | 
| **date**| 2017-02-27T17:37:20.116 |
| **version** | 1.2.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common.encrypt  |
| **source core** | slate.common.encrypt.Encryptor.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/encrypt](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/encrypt)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Encryptor.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Encryptor.scala) |
| **depends on** |   |

## Import
```scala 
// required 
import slate.common.encrypt.Encryptor



// optional 
import slate.common.{Strings}
import slate.common.encrypt.EncryptSupportIn
import slate.common.results.ResultSupportIn
import slate.core.cmds.Cmd


```

## Setup
```scala


  // SETUP 1: Create your singleton encryptor that can encrypt/decrypt using your custom key/secret.
  // and use it as a singleton.
  object TestEncryptor extends Encryptor("wejklhviuxywehjk", "3214maslkdf03292"){
  }

  // SETUP 2: Create an instance encryptor
  val encryptor = new Encryptor("wejklhviuxywehjk", "3214maslkdf03292")

  

```

## Usage
```scala


    // CASE 1: Encrypt using AES ( text is base64 encoded without newlines )
    val input = "basMoAKSKDFJrd789"
    val encrypted = TestEncryptor.encrypt(input)
    println(encrypted)


    // CASE 2: Decrypt using AES
    val decrypted = TestEncryptor.decrypt(encrypted)
    println(decrypted)


    // CASE 3: Ensure decrypted matches original
    require(Strings.isMatch(input, decrypted), "Encryption / decrypting does not work")


    // CASE 4: Use the EncryptSupportIn trait to have built in encrypt/decrypt methods
    // NOTE: You just have to have an _enc member field
    println( encrypt(input))
    println( decrypt(encrypted) )

    

```

