package com.nowcoder.community;

import com.nowcoder.community.entity.Course;
import com.nowcoder.community.entity.MiaoshaCourse;
import com.nowcoder.community.service.CourseService;
import com.nowcoder.community.service.MiaoshaCourseService;
import net.bytebuddy.asm.Advice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CourseTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private MiaoshaCourseService miaoshaCourseService;

    @Test
    public void test1(){
        System.out.println(courseService.findCourseById(1));
    }

    @Test
    public void test2(){
        System.out.println(miaoshaCourseService.findMiaoshaCourseById(1));

        List<MiaoshaCourse> miaoshaCourses = miaoshaCourseService.findMiaoshaCourses(0, 5);

        for (MiaoshaCourse miaoshaCourse: miaoshaCourses){
            Course course = courseService.findCourseById(miaoshaCourse.getCourseId());
            System.out.println(miaoshaCourse);
            System.out.println(course);
        }

        System.out.println(miaoshaCourseService.findMiaoshaCourseRows());

        System.out.println(miaoshaCourseService.findMiaoshaCourses(0, 2));
        System.out.println(miaoshaCourseService.findMiaoshaCourses(0, 0));


    }

}
