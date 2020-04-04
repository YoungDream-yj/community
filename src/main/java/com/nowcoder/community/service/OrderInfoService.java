package com.nowcoder.community.service;

import com.nowcoder.community.dao.OrderInfoMapper;
import com.nowcoder.community.entity.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    public int createOrderInfo(OrderInfo orderInfo){
        return orderInfoMapper.insertOrderInfo(orderInfo);
    }

}
