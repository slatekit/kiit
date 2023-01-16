package app;

import kiit.entities.Entities;
import kiit.entities.EntityRepo;
import kiit.entities.EntityService;

public class SimpleEventService extends EntityService<Integer, SimpleEvent> implements ISimpleEventService {

    public SimpleEventService(Entities entities, EntityRepo<Integer, SimpleEvent> repo){
        super( repo);
    }
}
