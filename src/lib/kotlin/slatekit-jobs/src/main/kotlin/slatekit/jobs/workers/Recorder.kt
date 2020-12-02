package slatekit.jobs.workers

import slatekit.jobs.Task
import slatekit.results.Err
import slatekit.tracking.Recorder

typealias Recorder = Recorder<Task, WorkResult, Err>
