package com.nowcoder.community.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.async.EventHandler;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LikeHandler implements EventHandler, CommunityConstant {

    @Autowired
    private MessageService messageService;

    @Override
    public void doHandle(Event event) {
        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if (!event.getData().isEmpty()){
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    @Override
    public List<String> getSupportEventTypes() {
        return Arrays.asList(TOPIC_LIKE);
    }
}
