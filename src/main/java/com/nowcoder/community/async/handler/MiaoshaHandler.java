package com.nowcoder.community.async.handler;

import com.nowcoder.community.async.EventHandler;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.*;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MiaoshaHandler implements EventHandler, CommunityConstant {

    @Autowired
    private MiaoshaCourseService miaoshaCourseService;

    @Autowired
    private MiaoshaOrderService miaoshaOrderService;

    @Autowired
    private MiaoshaService miaoshaService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Override
    public void doHandle(Event event) {
        // 判断库存
        MiaoshaCourse miaoshaCourse = miaoshaCourseService.findMiaoshaCourseById((Integer) event.getData().get("miaoshaCourseId"));
        if (miaoshaCourse.getStockCount() <= 0){
            return;
        }

        // 判断是否重复秒杀，这里是从数据库查询秒杀订单判断，上一个秒杀是利用唯一主键不能重复插入来判断，后续可以看哪个效果好
        // 使用userId和courseId构成unique唯一索引，防止重复秒杀
        MiaoshaOrder miaoshaOrder = miaoshaOrderService.getMiaoshaOrderByUserIdCourseId(event.getUserId(), miaoshaCourse.getCourseId());
        if (miaoshaOrder != null){
            return;
        }

        User user = userService.findUserById(event.getUserId());
        Course course = courseService.findCourseById(miaoshaCourse.getCourseId());
        // 减库存，下订单，写入秒杀订单
        // 销量增加还没做
        miaoshaService.miaosha(user, miaoshaCourse, course);
    }

    @Override
    public List<String> getSupportEventTypes() {
        return Arrays.asList(TOPIC_MIAOSHA);
    }
}
