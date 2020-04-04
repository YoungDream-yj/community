package com.nowcoder.community.service;

import com.nowcoder.community.dao.MiaoshaOrderMapper;
import com.nowcoder.community.entity.MiaoshaOrder;
import com.nowcoder.community.entity.OrderInfo;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MiaoshaOrderService {

    @Autowired
    private MiaoshaOrderMapper miaoshaOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    public MiaoshaOrder getMiaoshaOrderByUserIdCourseId(int userId, int courseId){
        //return miaoshaOrderMapper.getMiaoshaOrderByUserIdCourseId(userId, courseId);
        // 从缓存中查询是否有已生成秒杀订单
        String key = RedisKeyUtil.getMiaoshaOrder(userId, courseId);
        return (MiaoshaOrder) redisTemplate.opsForValue().get(key);
    }

    public int createMiaoshaOrder(MiaoshaOrder miaoshaOrder){
        return miaoshaOrderMapper.insertMiaoshaOrder(miaoshaOrder);
    }

}
