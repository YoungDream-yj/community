package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Course;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper {

    Course selectCourseById(int id);

}
