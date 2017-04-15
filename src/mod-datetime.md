---
layout: start_page_mods_utils
title: module DateTime
permalink: /mod-datetime
---

# DateTime

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | DataTime wrapper around Java 8 LocalDateTime providing a simplified interface, some convenience, extra features. | 
| **date**| 2017-04-12T22:59:13.931 |
| **version** | 1.4.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common  |
| **source core** | slate.common.DateTime.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/common](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/common)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_DateTime.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_DateTime.scala) |
| **depends on** |   |

## Import
```scala 
// required 
import slate.common.{Result, DateTime}


// optional 
import java.time._
import slate.common.results.ResultSupportIn
import slate.core.cmds.Cmd


```

## Setup
```scala

n/a

```

## Usage
```scala


    // Case 1. local date time by default
    val dt = DateTime.now()
    println("slate Current DateTime: " + dt)

    // Case 2. utc
    val dateOnly = dt.atUtc()
    println("dateOnly : " + dateOnly)

    // Case 3. Date parts
    println( "year  : " + dt.year    )
    println( "month : " + dt.month   )
    println( "day   : " + dt.day     )
    println( "hour  : " + dt.hours   )
    println( "mins  : " + dt.minutes )
    println( "secs  : " + dt.day     )

    // Case 4. add time
    val dt1 = DateTime(2016, 7, 22, 8, 30, 30)
    println( dt1.addSeconds(1).toString )
    println( dt1.addMinutes(1).toString    )
    println( dt1.addHours(1).toString   )
    println( dt1.addDays(1).toString    )
    println( dt1.addMonths(1).toString  )
    println( dt1.addYears(1).toString   )

    // Case 5. Compare local
    ensureTrue(dt1, "> ", dt1.addHours(-1), dt1 >  dt1.addHours(-1) )
    ensureTrue(dt1, ">=", dt1.addHours(-1), dt1 >= dt1.addHours(-1) )
    ensureTrue(dt1, ">=", dt1.addHours( 0), dt1 >= dt1.addHours( 0) )
    ensureTrue(dt1, "< ", dt1.addHours( 1), dt1 <  dt1.addHours( 1) )
    ensureTrue(dt1, "<=", dt1.addHours( 1), dt1 <= dt1.addHours( 1) )
    ensureTrue(dt1, "<=", dt1.addHours( 0), dt1 <= dt1.addHours( 0) )
    ensureTrue(dt1, "==", dt1.addHours( 0), dt1 == dt1.addHours( 0) )
    ensureTrue(dt1, "!=", dt1.addHours( 2), dt1 != dt1.addHours( 2) )

    // Case 6. Compare local with utc
    ensureTrue(dt1, "> ", dt1.addHours(-1), dt1 >  dt1.addHours(-1).atUtc() )
    ensureTrue(dt1, ">=", dt1.addHours(-1), dt1 >= dt1.addHours(-1).atUtc() )
    ensureTrue(dt1, ">=", dt1.addHours( 0), dt1 >= dt1.addHours( 0).atUtc() )
    ensureTrue(dt1, "< ", dt1.addHours( 1), dt1 <  dt1.addHours( 1).atUtc() )
    ensureTrue(dt1, "<=", dt1.addHours( 1), dt1 <= dt1.addHours( 1).atUtc() )
    ensureTrue(dt1, "<=", dt1.addHours( 0), dt1 <= dt1.addHours( 0).atUtc() )
    ensureTrue(dt1, "==", dt1.addHours( 0), dt1 == dt1.addHours( 0).atUtc() )
    ensureTrue(dt1, "!=", dt1.addHours( 2), dt1 != dt1.addHours( 2).atUtc() )

    // Case 7. Get the duration
    println( dt1.addSeconds(2).durationFrom( dt1 ) )
    println( dt1.addMinutes(2).durationFrom( dt1 ) )
    println( dt1.addHours(2).durationFrom( dt1 ) )
    println( dt1.addDays(2).durationFrom( dt1 ) )
    println( dt1.addMonths(2).periodFrom( dt1 ) )

    

```

