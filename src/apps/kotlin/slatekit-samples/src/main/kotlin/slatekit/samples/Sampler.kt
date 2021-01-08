package slatekit.samples


fun main(args: Array<String>) {
    Samples.cli(args)
    // println("slate kit samples")

    // API
    // 1. verb not auto-handled
    // 2. patch with fields of name/value ( any? ) not parsed
    // 3. source restrict to web/api not working


    // CLI
    // samples.cli.about
    // samples.cli.inc
    // samples.cli.value
    // samples.cli.add -value=2
    // samples.cli.greet -greeting="whats up"
    // samples.cli.movies
    // samples.cli.inputs -name="kishore" -isActive=true -age=41 -dept=2 -account=123 -average=2.4 -salary=120000 -date="2019-04-01T11:05:30Z"
}
