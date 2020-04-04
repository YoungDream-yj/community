package com.nowcoder.community.dao;

import com.nowcoder.community.entity.MiaoshaCourse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MiaoshaCourseMapper {

    MiaoshaCourse selectMiaoshaCourseById(int id);

    List<MiaoshaCourse> selectMiaoshaCourses(int offset, int limit);

    int selectMiaoshaCourseRows();

    int reduceStock(int id);

}
