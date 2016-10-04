# scala-slate
A scala micro-framework

```scala
    import slate.core.commands._
    
    def test() 
    {
      // Use case 1: run a single command
      _cmd = new CleanupCommand()
      var cmdResult = _cmd.execute()
      
      // Use case 2: get information about status of command.
      // e.g. 
      // - name of the command
      // - has run at all ?
      // - how many times run?
      // - last run time
      // - how many errors
      // - last result
      print("Ran Example_Command from samples")
      print("command name :" + _cmd.state.name)
      print("command last run time: " + _cmd.state.lastRunTime)
      print("command error count: " + _cmd.state.errorCount)
      print("command run count: " + _cmd.state.runCount)
      print("command has run: " + _cmd.state.hasRun)
      
      // Use case 3: get the returned result of the command itself.
      // NOTE: The commandresult contains the succes, message, result, time in millieconds
      // of the opertaion. 
      cmdResult = _cmd.execute()
      var result = cmdResult.result
      
      // Use case 4: get the start/end duration time of the command operation.
      // NOTE: The commandresult contains the succes, message, result, time in millieconds
      // of the opertaion. 
      cmdResult = _cmd.execute()
      val started = cmdResult.start
      val ended = cmdResult.end
      val runTimeInMs = cmdResult.totalMilliseconds
      
      // Use case 5: run multple commands using command executor.
      val commands = new Seq<ICommand>()
      commands.add(new CleanupTempDirectoryCommand("/temp/cache"))
      commands.add(new CleanupTempDirectoryCommand("/temp/logs"))
      val cmdExec = new CommandExecutor(commands)
      cmdExec.run()
      
      // Use case 6 : get status of all commands that were run
      var results = cmdExec.currrentStatus
    }
    
    
    class CleanupTempDirectoryCommand(val directory:String) extends Command 
    {
    
        override def executeInternal(object[] args):Any =
        {
            // do some work here...
            return true;
        }
    }
```
