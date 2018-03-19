---
layout: start_page_mods_utils
title: module Random
permalink: /kotlin-mod-random
---

# Random

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A random generator for strings, guids, numbers, alpha-numeric, and alpha-numeric-symbols for various lengths | 
| **date**| 2018-03-18 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common  |
| **source core** | slatekit.common.Random.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Random.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Random.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import slatekit.common.Random.alpha3
import slatekit.common.Random.alpha6
import slatekit.common.Random.alphaN
import slatekit.common.Random.alphaSym3
import slatekit.common.Random.alphaSym6
import slatekit.common.Random.alphaSymN
import slatekit.common.Random.digits3
import slatekit.common.Random.digits6
import slatekit.common.Random.digitsN
import slatekit.common.Random.string3
import slatekit.common.Random.string6
import slatekit.common.Random.stringGuid
import slatekit.common.Random.stringN



// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok


```

## Setup
```kotlin

n/a

```

## Usage
```kotlin


    // CASE 1: Create random strings ( characters only ) of different lengths
    println("Random STRINGS - UPPER + lower case")
    println( "len 3 : " + string3()  )
    println( "len 6 : " + string6()  )
    println( "len n : " + stringN(9) )
    println()

    println("Random STRINGS - lower case only")
    println( "len 3 : " + stringN(3, allowUpper = false) )
    println( "len 6 : " + stringN(6, allowUpper = false) )
    println( "len n : " + stringN(9, allowUpper = false) )
    println()

    // CASE 2: Create Guid
    println("Random GUID")
    println( "dashes - yes : " + stringGuid() )
    println( "dashes - no  : " + stringGuid(includeDashes = false) )
    println()

    // CASE 3: Create numbers of different lengths
    println("Random NUMBERS")
    println( "len 3 : " + digits3()  )
    println( "len 6 : " + digits6()  )
    println( "len n : " + digitsN(9) )
    println()

    // CASE 4: Create alpha-numeric strings ( chars + digits ) of different lengths
    println("Random ALPHA-NUMERIC")
    println( "len 3 : " + alpha3()  )
    println( "len 6 : " + alpha6()  )
    println( "len n : " + alphaN(9) )
    println()

    // CASE 5: Create alpha-numeric-symbol strings ( chars + digits + symbols ) of different lengths
    println("Random ALPHA-NUMERIC-SYMBOL")
    println( "len 3 : " + alphaSym3()  )
    println( "len 6 : " + alphaSym6()  )
    println( "len n : " + alphaSymN(9) )

    

```


## Output

```java
  Random STRINGS - UPPER + lower case
  len 3 : ndo
  len 6 : mRqDIz
  len n : mjTWZkARV

  Random STRINGS - lower case only
  len 3 : qdc
  len 6 : hqqnab
  len n : chnedbmlp

  Random GUID
  dashes - yes : 54E49A58-5A4E-45F2-994B-6E0D783B95E9
  dashes - no  : 18496EC779EB48ABAC4B61B2DC4357F5

  Random NUMBERS
  len 3 : 697
  len 6 : 909051
  len n : 181651948

  Random ALPHA-NUMERIC
  len 3 : ghs
  len 6 : 194cpb
  len n : vw9upkh4p

  Random ALPHA-NUMERIC-SYMBOL
  len 3 : n0i
  len 6 : m27h6h
  len n : k}y!m+fz)
```
  