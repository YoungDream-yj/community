package com.nowcoder.community.dao;

import com.nowcoder.community.entity.MiaoshaOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MiaoshaOrderMapper {

    MiaoshaOrder getMiaoshaOrderByUserIdCourseId(int userId, int courseId);

    int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

}
