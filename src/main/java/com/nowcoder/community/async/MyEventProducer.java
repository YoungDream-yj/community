package com.nowcoder.community.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MyEventProducer {

    @Autowired
    private RedisTemplate redisTemplate;

    public void fireEvent(Event event){
        // 可做优先队列优化
        redisTemplate.opsForList().leftPush(RedisKeyUtil.getEventQueueKey(), JSONObject.toJSONString(event));
    }
}
