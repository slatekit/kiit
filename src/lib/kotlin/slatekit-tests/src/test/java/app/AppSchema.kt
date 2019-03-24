package app

import app.AppEvent
import slatekit.meta.models.Model

object AppSchema {

    @JvmStatic
    fun setupEventMappings(model: Model): Model {
        val finalModel = model.add(AppEvent::calendarId)
                .add(AppEvent::title, true)
                .add(AppEvent::details, false)
                .add(AppEvent::startTime)
                .add(AppEvent::endTime)
                .add(AppEvent::timeZone)

                // Attributes
                .add(AppEvent::status)
                .add(AppEvent::icon)
                .add(AppEvent::value)
                .add(AppEvent::priority)
                .add(AppEvent::isFavorite)
                .add(AppEvent::isEnabled)
        return finalModel
    }
}
