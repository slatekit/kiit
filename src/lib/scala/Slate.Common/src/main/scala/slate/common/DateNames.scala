/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.common

object DateNames {

  val dayNamesToNumber = Map(
    "Sunday"    -> 1,
    "Monday"    -> 2,
    "Tuesday"   -> 3,
    "Wednesday" -> 4,
    "Thursday"  -> 5,
    "Friday"    -> 6,
    "Saturday"  -> 7
  )


  val dayNumberToNames = Map(
    1 -> "Sunday"   ,
    2 -> "Monday"   ,
    3 -> "Tuesday"  ,
    4 -> "Wednesday",
    5 -> "Thursday" ,
    6 -> "Friday"   ,
    7 -> "Saturday"
  )


  val monthNumberToNames = Map(
    1  -> "January"  ,
    2  -> "February" ,
    3  -> "March"    ,
    4  -> "April"    ,
    5  -> "May"      ,
    6  -> "June"     ,
    7  -> "July"     ,
    8  -> "August"   ,
    9  -> "September",
    10 -> "October"  ,
    11 -> "November" ,
    12 -> "December"
  )


  val monthNameToNumbers = Map(
    "January"   -> 1,
    "February"  -> 2,
    "March"     -> 3,
    "April"     -> 4,
    "May"       -> 5,
    "June"      -> 6,
    "July"      -> 7,
    "August"    -> 8,
    "September" -> 9,
    "October"   -> 10,
    "November"  -> 11,
    "December"  -> 12
  )
}
