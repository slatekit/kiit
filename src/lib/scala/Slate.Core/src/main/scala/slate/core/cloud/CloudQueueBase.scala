package slate.core.cloud

import slate.common.queues.{QueueSource, QueueSourceMsg}

/**
  * Abstraction for cloud based message queue storage and retrieval
  */
abstract class CloudQueueBase extends QueueSource with CloudActions with QueueSourceMsg {


}
