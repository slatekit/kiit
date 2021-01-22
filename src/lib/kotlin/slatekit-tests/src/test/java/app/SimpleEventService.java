package app;

import slatekit.entities.Entities;
import slatekit.entities.EntityRepo;
import slatekit.entities.EntityService;

public class SimpleEventService extends EntityService<Integer, SimpleEvent> implements ISimpleEventService {

    public SimpleEventService(Entities entities, EntityRepo<Integer, SimpleEvent> repo){
        super( repo);
    }
}
