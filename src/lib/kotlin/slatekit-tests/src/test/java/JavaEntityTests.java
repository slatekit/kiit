
import app.SimpleEvent;
import app.SimpleEventService;
import entities.SqliteDb;
import kotlin.reflect.KClass;
import meta.ModelHelpers;
import org.junit.Assert;
import org.junit.Test;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import slatekit.common.db.DbCon;
import slatekit.common.db.DbLookup;
import slatekit.common.log.LogsDefault;
import slatekit.entities.core.Entities;
import slatekit.entities.core.EntityRepo;
import slatekit.entities.repos.EntityRepoInMemoryWithIntId;
import slatekit.entities.repos.IdGenerator;
import slatekit.entities.repos.IntIdGenerator;
import slatekit.meta.models.Model;

import java.util.List;

public class JavaEntityTests {

    @Test
    public void can_create_model() {
        Model model = SimpleEvent.asModel();

        KClass cls = ModelHelpers.model(SimpleEvent.class);
        Assert.assertTrue(model.getDataType() == cls);
        Assert.assertEquals(12, model.getFields().size());
        Assert.assertEquals("title", model.getFields().get(1).name);
        Assert.assertEquals(true, model.getFields().get(1).isRequired);
        Assert.assertEquals("details", model.getFields().get(2).name);
        Assert.assertEquals(false, model.getFields().get(2).isRequired);
    }


    @Test
    public void can_create_service() {
        Entities entities = new Entities( (con) -> new SqliteDb(), DbLookup.defaultDb(DbCon.empty), null, LogsDefault.INSTANCE, null);
        IdGenerator<Integer> idGen = new IntIdGenerator();
        EntityRepo<Integer, SimpleEvent> repo = new EntityRepoInMemoryWithIntId(ModelHelpers.model(SimpleEvent.class), idGen);
        SimpleEventService service = new SimpleEventService(entities, repo);
        List<SimpleEvent> simpleEvents = service.getAll();
        Assert.assertTrue(simpleEvents.size() == 0);
    }


    @Test
    public void can_use_repo() {
        Entities entities = new Entities( (con) -> new SqliteDb(), DbLookup.defaultDb(DbCon.empty), null, LogsDefault.INSTANCE, null);
        IdGenerator<Integer> idGen = new IntIdGenerator();
        EntityRepo<Integer, SimpleEvent> repo = new EntityRepoInMemoryWithIntId(ModelHelpers.model(SimpleEvent.class), idGen);
        SimpleEventService service = new SimpleEventService(entities, repo);
        List<SimpleEvent> simpleEvents = service.getAll();
        Assert.assertTrue(simpleEvents.size() == 0);

        LocalDateTime timestamp = LocalDateTime.of(2019, 3, 1, 9, 30, 0);
        SimpleEvent simpleEvent = new SimpleEvent();
        simpleEvent.title = "event 1";
        simpleEvent.details = "details 1";
        simpleEvent.startTime = ZonedDateTime.of(timestamp, ZoneId.systemDefault());
        simpleEvent.endTime = ZonedDateTime.of(timestamp.plusHours(1), ZoneId.systemDefault());
        simpleEvent.url = "www.event1.com";

        int id = service.create(simpleEvent);
        Assert.assertEquals(id , 1);
        List<SimpleEvent> all = service.getAll();
        Assert.assertEquals(1, all.size());
    }
}
