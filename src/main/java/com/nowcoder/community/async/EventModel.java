package com.nowcoder.community.async;

import java.util.HashMap;
import java.util.Map;

public class EventModel {
    private EventType type;
    private int userId;
    private int entityType;
    private int entityId;
    private int entityUserId;
    private Map<String, Object> data = new HashMap<>();

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public EventModel setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public EventModel setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public EventModel setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
