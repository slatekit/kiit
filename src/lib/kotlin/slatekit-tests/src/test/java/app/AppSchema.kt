package app

import app.AppEvent
import slatekit.meta.models.Model

object AppSchema {

    @JvmStatic
    fun setupEventMappings(model: Model): Model {
        val model = Model.of<Long, AppEvent> {

                    field(AppEvent::calendarId)
                    field(AppEvent::title, true)
                    field(AppEvent::details, false)
                    field(AppEvent::startTime)
                    field(AppEvent::endTime)
                    field(AppEvent::timeZone)

                    // Attributes
                    field(AppEvent::status)
                    field(AppEvent::icon)
                    field(AppEvent::value)
                    field(AppEvent::priority)
                    field(AppEvent::isFavorite)
                    field(AppEvent::isEnabled)
        }
        return finalModel
    }
}
