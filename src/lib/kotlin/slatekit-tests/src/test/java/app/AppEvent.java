package app;

import core.Event;
import entities.IEntityUnique;
import org.threeten.bp.ZonedDateTime;
import kiit.entities.Entity;
import kiit.meta.models.Model;

/**
 * Created by kv on 6/11/2015.
 */
public abstract class AppEvent extends Event implements Entity<Integer>, IEntityUnique {


    public final String sourceType;


    public AppEvent(String type) {
        sourceType = type;
        this.spaceId = "none";
    }


    public String spaceId;


    protected int _id;


    // Audit fields for time/user
    public String createdBy;
    public ZonedDateTime createdAt;
    public String updatedBy;
    public ZonedDateTime updatedAt;


    // Tracking ( also useful during tracking / upgrades / schema changes )
    public String tag;
    public String label;
    public String uuid;

    /**
     * gets the id
     *
     * NOTES:
     * 1. This is a method instead of a val "id" to allow domain entities more
     *    flexibility in how to implement their own identity.
     * 2. This also allows for 2 default implementations ( EntityWithId and EntityWithSetId )
     *
     * @return
     */
    public Integer identity() {
        return _id;
    }


    /**
     * whether or not this entity is persisted.
     * @return
     */
    public boolean isPersisted() {
        return _id > 0;
    }


    // To conform to interfaces ( IEntity/IEntityUnique )
    public int getId() {
        return _id;
    }

    public void setId(int value) {
        _id = value;
    }


    @Override
    public String getUUID() {
        return uuid;
    }


    @Override
    public void setUUID(String guid) {
        uuid = guid;
    }


    /**
     * Once we have a model schema of the entity,
     * we can do many things with it, such as standardize the serialization
     * to any format.
     *
     * @return
     */
    public abstract Model toModel();

}