package app;

import slatekit.entities.Entities;
import slatekit.entities.Repo;
import slatekit.entities.EntityService;

public class SimpleEventService extends EntityService<Integer, SimpleEvent> implements ISimpleEventService {

    public SimpleEventService(Entities entities, Repo<Integer, SimpleEvent> repo){
        super( repo);
    }
}
