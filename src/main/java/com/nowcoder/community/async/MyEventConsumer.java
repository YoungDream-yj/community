package com.nowcoder.community.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventConsumer;
import com.nowcoder.community.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class MyEventConsumer implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private RedisTemplate redisTemplate;

    // config初始为空
    private Map<String, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 从容器中找出所有的handler,  为{"likeHandler":LikeHandler@7505}
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);


        if (beans != null){
            for (Map.Entry<String, EventHandler> entry: beans.entrySet()){
                List<String> eventTypes = entry.getValue().getSupportEventTypes();

                // type = "like"
                for (String type: eventTypes){
                    // 如果事件类型未注册
                    if (!config.containsKey(type)){
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    // 将事件类型与handler绑定
                    // 执行后config为{"like":[LikeHandler@7505]}
                    config.get(type).add(entry.getValue());
                }

            }
        }

        // 开启线程执行handler, 以后可做线程池优化
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String key = RedisKeyUtil.getEventQueueKey();
//                    List<String> events = Arrays.asList((String) redisTemplate.opsForList().rightPop(key, 0 , TimeUnit.SECONDS));
                    List<String> events = Arrays.asList((String) redisTemplate.opsForList().rightPop(key, 3600, TimeUnit.SECONDS));
                    for (String message: events){
                        if (message.equals(key)){
                            continue;
                        }

                        Event event = JSONObject.parseObject(message, Event.class);
                        if (!config.containsKey(event.getTopic())){
                            logger.error("不能识别的事件");
                            continue;
                        }

                        for (EventHandler handler: config.get(event.getTopic())){
                            handler.doHandle(event);
                        }

                    }
                }
            }
        });
        thread.start();


    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
