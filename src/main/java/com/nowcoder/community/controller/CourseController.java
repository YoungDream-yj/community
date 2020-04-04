package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Course;
import com.nowcoder.community.entity.MiaoshaCourse;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.CourseService;
import com.nowcoder.community.service.MiaoshaCourseService;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private MiaoshaCourseService miaoshaCourseService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 压测环境全部本地：5000 * 10，吞吐量330-350
     * tomcat连接池配置未调优时，吞吐量300-320
     *
     * 页面级缓存优化后本地：5000 * 10，吞吐量2892, keep-alive-java:3000
     *
     * @param model
     * @param page
     * @return
     */
    //@RequestMapping(path = "/course/list", method = RequestMethod.GET)
    @RequestMapping(path = "/course/list", method = RequestMethod.GET, produces = "text/html") //produces指定返回类型
    @ResponseBody
    public String getMiaoshaCourseList(Model model, Page page,
                                       HttpServletRequest request, HttpServletResponse response){

        // 页面级缓存优化
        // 从redis缓存中取页面缓存
        String redisKey = RedisKeyUtil.getHtmlCacheKey("courselist", page.getOffset());
        String html = (String) redisTemplate.opsForValue().get(redisKey);
        if (!StringUtils.isEmpty(html)){
            return html;
        }


        page.setLimit(5);
        page.setRows(miaoshaCourseService.findMiaoshaCourseRows());
        page.setPath("/course/list");

        List<MiaoshaCourse> miaoshaCourses = miaoshaCourseService.findMiaoshaCourses(page.getOffset(), page.getLimit());
        // 秒杀课程VO列表
        List<Map<String, Object>> miaoshaCoursesVo = new ArrayList<>();
        if (miaoshaCourses != null){
            for (MiaoshaCourse miaoshaCourse: miaoshaCourses){
                Map<String, Object> map = new HashMap<>();
                map.put("miaoshaCourse", miaoshaCourse);
                map.put("course", courseService.findCourseById(miaoshaCourse.getCourseId()));
                miaoshaCoursesVo.add(map);
            }
        }
        model.addAttribute("miaoshaCourses", miaoshaCoursesVo);
        //return "/site/course";


        // 手动渲染需要无法通过拦截器返回user
        model.addAttribute("loginUser", hostHolder.getUser());
        // 若缓存中没有，手动渲染，存入缓存
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("/site/course", webContext);
        if (!StringUtils.isEmpty(html)){
            redisTemplate.opsForValue().set(redisKey, html, 60, TimeUnit.SECONDS);
        }
        return html;

    }

    //@RequestMapping(path = "/course/detail/{miaoshaCourseId}", method = RequestMethod.GET)
    // 秒杀商品详情页使用页面级缓存会出现一些问题，比如倒计时出错
    // 比如在前一秒秒杀未开启，但页面一缓存，一分钟内秒杀即使开启，刷新就是从未开启开始
    // 可以使用前后端分离做静态化改造，但是改造的话需要改动太多的页面，主要是也会改动到头和尾部页面，需要去改index,就算了，也不是很难的技术
    @RequestMapping(path = "/course/detail/{miaoshaCourseId}", method = RequestMethod.GET, produces = "text/html")
    @ResponseBody
    public String getMiaoshaCourse(@PathVariable("miaoshaCourseId") int miaoshaCourseId, Model model,
                                   HttpServletRequest request, HttpServletResponse response){

        // 页面级缓存优化
        // 从redis缓存中取页面缓存
        String redisKey = RedisKeyUtil.getHtmlCacheKey("coursedetail", miaoshaCourseId);
        String html = (String) redisTemplate.opsForValue().get(redisKey);
        if (!StringUtils.isEmpty(html)){
            return html;
        }

        MiaoshaCourse miaoshaCourse = miaoshaCourseService.findMiaoshaCourseById(miaoshaCourseId);
        model.addAttribute("miaoshaCourse", miaoshaCourse);

        Course course = courseService.findCourseById(miaoshaCourse.getCourseId());
        model.addAttribute("course", course);

        long start = miaoshaCourse.getStartDate().getTime();
        long end = miaoshaCourse.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if (now < start){
            // 秒杀未开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)((start - now) / 1000);
        }else if (now > end){
            // 秒杀已结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else {
            // 秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        //return "/site/course-detail";


        // 手动渲染需要无法通过拦截器返回user
        model.addAttribute("loginUser", hostHolder.getUser());
        // 若缓存中没有，手动渲染，存入缓存
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("/site/course-detail", webContext);
        if (!StringUtils.isEmpty(html)){
            redisTemplate.opsForValue().set(redisKey, html, 60, TimeUnit.SECONDS);
        }
        return html;

    }

}
