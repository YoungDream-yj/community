package com.nowcoder.community.service;

import com.nowcoder.community.entity.*;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class MiaoshaService {

    @Autowired
    private MiaoshaCourseService miaoshaCourseService;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private MiaoshaOrderService miaoshaOrderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    public OrderInfo miaosha(User user, MiaoshaCourse miaoshaCourse, Course course){
        // 减库存
        // 暂时用返回值判断解决 库存不超卖，订单却超卖的问题
        int count = miaoshaCourseService.reduceStock(miaoshaCourse.getId());
        if (count != 0){
            // 下订单
            OrderInfo orderInfo = new OrderInfo();
            orderInfo = new OrderInfo();
            orderInfo.setCreateDate(new Date());
            orderInfo.setCourseCount(1);
            orderInfo.setCourseId(course.getId());
            orderInfo.setCourseName(course.getCourseName());
            orderInfo.setCoursePrice(miaoshaCourse.getMiaoshaPrice());
            orderInfo.setOrderChannel(1);
            orderInfo.setStatus(0);
            orderInfo.setUserId(user.getId());
            orderInfoService.createOrderInfo(orderInfo);

            // 写入秒杀订单
            MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
            miaoshaOrder.setCourseId(course.getId());
            miaoshaOrder.setOrderId(orderInfo.getId());
            miaoshaOrder.setUserId(user.getId());
            miaoshaOrderService.createMiaoshaOrder(miaoshaOrder);

            // 将秒杀订单存入redis
            String key = RedisKeyUtil.getMiaoshaOrder(user.getId(), course.getId());
            redisTemplate.opsForValue().set(key, miaoshaOrder);

            return orderInfo;
        }

        setCourseOver(miaoshaCourse.getCourseId());
        return null;
    }


    public int getMiaoshaResult(int userId, int courseId) {
        MiaoshaOrder miaoshaOrder = miaoshaOrderService.getMiaoshaOrderByUserIdCourseId(userId, courseId);
        if (miaoshaOrder != null){
            return miaoshaOrder.getOrderId();
        }else {
            boolean isOver = getCourseOver(courseId);
            if (isOver){
                return -1;
            }else {
                return 0;
            }
        }
    }

    private void setCourseOver(int courseId) {
        redisTemplate.opsForValue().set(RedisKeyUtil.getMiaoshaOverKey(courseId), true);
    }

    private boolean getCourseOver(int courseId) {
        return redisTemplate.hasKey(RedisKeyUtil.getMiaoshaOverKey(courseId));
    }

    public void deleteCourseOver(int courseId){
        if (getCourseOver(courseId)){
            redisTemplate.delete(RedisKeyUtil.getMiaoshaOverKey(courseId));
        }
    }
}
