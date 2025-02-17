package app;

import meta.ModelHelpers;
import org.jetbrains.annotations.NotNull;
import kiit.entities.Entity;
import kiit.entities.EntityUpdatable;
import kiit.meta.models.Model;

// Temp to represent a vacation day
// e.g. date of vacation, total days, and reason
public class SimpleEvent extends AppEvent implements Entity<Integer>, EntityUpdatable<Integer, SimpleEvent> {

    public SimpleEvent() {
        super("simple-event");
    }


    @Override
    public Model toModel() {
        return asModel();
    }


    public static Model asModel() {

        Model model = new Model(ModelHelpers.model(SimpleEvent.class), "", "");
        Model finalModel = AppSchema.setupEventMappings(model);
        return finalModel;
    }

    @NotNull
    @Override
    public SimpleEvent withId(@NotNull Integer id) {
        this._id = id;
        return this;
    }

    @NotNull
    @Override
    public SimpleEvent withIdAny(@NotNull Object id) {
        this._id = (Integer)id;
        return this;
    }
}
