/**
  * <slate_header>
  * author: Kishore Reddy
  * url: www.github.com/code-helix/slatekit
  * copyright: 2016 Kishore Reddy
  * license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  * desc: A tool-kit, utility library and server-backend
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slatekit.examples

//<doc:import_required>
import slatekit.common.utils.Random.alpha6
import slatekit.common.utils.Random.alphaN
import slatekit.common.utils.Random.alphaSym6
import slatekit.common.utils.Random.alphaSymN
import slatekit.common.utils.Random.digits6
import slatekit.common.utils.Random.digitsN
import slatekit.common.utils.Random.string6
import slatekit.common.utils.Random.uuid
import slatekit.common.utils.Random.stringN

//</doc:import_required>

//<doc:import_examples>


import slatekit.common.utils.Random
import slatekit.common.utils.Random.alphaNum6
import slatekit.results.Try
import slatekit.results.Success

//</doc:import_examples>

class Example_Random : Command("random") {

  override fun execute(request: CommandRequest) : Try<Any>
  {
    //<doc:examples>
    // CASE 1: Create random strings ( characters only ) of different lengths
    println("Random STRINGS - UPPER + lower case")
    println( "len 6 : " + string6()  )
    println( "len n : " + stringN(9) )
    println()

    println("Random STRINGS - lower case only")
    println( "len 3 : " + stringN(3, Random.ALPHA_LCASE) )
    println( "len 6 : " + stringN(6, Random.ALPHA) )
    println( "len n : " + stringN(9, Random.ALPHA_SAFE) )
    println()

    // CASE 2: Create Guid
    println("Random GUID")
    println( "dashes - yes : " + uuid() )
    println( "dashes - no  : " + uuid(includeDashes = false) )
    println( "casing - no  : " + uuid(includeDashes = false, upperCase = true) )
    println()

    // CASE 3: Create numbers of different lengths
    println("Random NUMBERS")
    println( "len 6 : " + digits6()  )
    println( "len n : " + digitsN(9) )
    println()

    // CASE 4: Create alpha-numeric strings ( chars + digits ) of different lengths
    println("Random ALPHA-NUMERIC")
    println( "len 6 : " + alpha6()  )
    println( "len n : " + alphaNum6() )
    println( "len n : " + alphaN(9) )
    println()

    // CASE 5: Create alpha-numeric-symbol strings ( chars + digits + symbols ) of different lengths
    println("Random ALPHA-NUMERIC-SYMBOL")
    println( "len 6 : " + alphaSym6()  )
    println( "len n : " + alphaSymN(9) )

    //</doc:examples>
    return Success("")
  }

  /*
  //<doc:output>
{{< highlight kotlin >}}
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
{{< /highlight >}}
  //</doc:output>
  */
}
