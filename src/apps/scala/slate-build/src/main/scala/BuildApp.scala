

object BuildApp {

  def main(args: Array[String]): Unit = {
    println("slate-scala build script")
    BuildScript.run(new SlateSamplesBuildScript())
  }
}