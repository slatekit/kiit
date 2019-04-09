package slatekit.core.alerts

import slatekit.results.Failure
import slatekit.results.Notice
import slatekit.results.Success
import slatekit.results.then

abstract class AlertService {

    abstract val settings: AlertSettings

    /**
     * Sends the email message
     * @param msg
     * @return
     */
    open fun send(alert: Alert, target: String): Notice<Boolean> {
        return validate(target)
                .then {
                    validate(alert, it)
                }.then {
                    send(it.first, it.second)
                }
    }

    /**
     * Sends alert to the target supplied
     * @param msg
     * @return
     */
    abstract fun send(alert: Alert, target: AlertTarget): Notice<Boolean>


    protected fun validate(targetId: String): Notice<AlertTarget> {
        if (targetId.isEmpty()) return Failure("target not provided")
        val target = this.settings.targets.firstOrNull { it.target == targetId }
        return when (target) {
            null -> Failure("Target $targetId does not exist")
            else -> Success(target)
        }
    }

    protected fun validate(alert: Alert, target: AlertTarget): Notice<Pair<Alert, AlertTarget>> {
        return if (alert.name.isEmpty())
            Failure("Alert name not provided")
        else
            Success(Pair(alert, target))
    }
}