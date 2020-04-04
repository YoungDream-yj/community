package com.nowcoder.community.service;

import com.nowcoder.community.dao.CourseMapper;
import com.nowcoder.community.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    @Autowired
    private CourseMapper courseMapper;

    public Course findCourseById(int id){
        return courseMapper.selectCourseById(id);
    }
}
