package app;

import slatekit.entities.core.Entities;
import slatekit.entities.core.EntityRepo;
import slatekit.entities.core.EntityService;

public class SimpleEventService extends EntityService<Integer, SimpleEvent> implements ISimpleEventService {

    public SimpleEventService(Entities entities, EntityRepo<Integer, SimpleEvent> repo){
        super(entities, repo);
    }
}
