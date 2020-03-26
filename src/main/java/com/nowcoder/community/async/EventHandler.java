package com.nowcoder.community.async;

import com.nowcoder.community.entity.Event;

import java.util.List;

public interface EventHandler {

    void doHandle(Event event);

    List<String> getSupportEventTypes();
}
