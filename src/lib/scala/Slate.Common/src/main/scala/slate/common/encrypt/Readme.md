# Encrypt

| field | value  | 
|:--|:--|
| **desc** | Encryption using AES | 
| **date**| 2016-11-21T16:49:15.683 |
| **version** | 0.9.1  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common.encrypt  |
| **source core** | slate.common.encrypt.Encryptor.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/encrypt](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/encrypt)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Encryptor.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Encryptor.scala) |
| **depends on** |   |

## Import
```scala 
// required 
import slate.common.encrypt.{Encryptor}
import slate.common.results.ResultSupportIn



// optional 
import slate.common.{Ensure, Strings, Result}
import slate.common.encrypt.{EncryptSupportIn}
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
    Ensure.isTrue(Strings.isMatch(input, decrypted), "Encryption / decrypting does not work")


    // CASE 4: Use the EncryptSupportIn trait to have built in encrypt/decrypt methods
    // NOTE: You just have to have an _enc member field
    _enc = Some(TestEncryptor)
    println( encrypt(input))
    println( decrypt(encrypted) )

    

```

