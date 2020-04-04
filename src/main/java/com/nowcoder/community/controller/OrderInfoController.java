package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Course;
import com.nowcoder.community.entity.OrderInfo;
import com.nowcoder.community.service.CourseService;
import com.nowcoder.community.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private CourseService courseService;

    @RequestMapping(path = "/order/detail/{orderId}", method = RequestMethod.GET)
    public String getOrderInfo(Model model, @PathVariable("orderId") int orderId){
        OrderInfo orderInfo = orderInfoService.getOrderInfoById(orderId);
        Course course = courseService.findCourseById(orderInfo.getCourseId());

        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("course", course);

        return "/site/order-detail";
    }

}
