package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.CourseService;
import com.nowcoder.community.service.MiaoshaCourseService;
import com.nowcoder.community.service.MiaoshaOrderService;
import com.nowcoder.community.service.MiaoshaService;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MiaoshaController implements InitializingBean {

    @Autowired
    private MiaoshaCourseService miaoshaCourseService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private MiaoshaOrderService miaoshaOrderService;

    @Autowired
    private MiaoshaService miaoshaService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private RedisTemplate redisTemplate;

    // 库存清空标记，库存小于0，则结束访问redis
    private Map<Integer, Boolean> localOverMap = new HashMap<>();


    /**
     * 系统初始化时将秒杀商品库存加载到缓存
     * 我在想是不是能在秒杀商品列表页面做这个事情
     * 是否应该只把秒杀未开启或进行中的加载进内存
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<MiaoshaCourse> miaoshaCourses = miaoshaCourseService.findMiaoshaCourses(0, 0);
        if (miaoshaCourses == null){
            return;
        }

        for (MiaoshaCourse miaoshaCourse : miaoshaCourses){
            redisTemplate.opsForValue().set(RedisKeyUtil.getMiaoshaStockKey(miaoshaCourse.getId()),
                    miaoshaCourse.getStockCount());
            localOverMap.put(miaoshaCourse.getId(), false);
        }

    }


    /**
     * 本地测试 5000*10，5000用户，10商品，吞吐量280-325，keep-alive-java:320左右， keep-alive:360-450,有异常
     *
     * redis预减库存优化后
     * 本地测试 5000*10，5000用户，10商品，吞吐量550，keep-alive-java:470， keep-alive:
     *
     * @param model
     * @param miaoshaCourseId
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/miaosha/do_miaosha", method = RequestMethod.POST)
    public String doMiaosha(Model model, @RequestParam("miaoshaCourseId") int miaoshaCourseId){
        // 这版秒杀缺少时间判断，秒杀状态判断

        // 龙虾三少版本在商品库中维持了秒杀活动id，在商品类中添加了秒杀活动类
        // 每次查询商品时，会查询活动并添加进商品类

        User user = hostHolder.getUser();

        // 判断库存
        // 龙虾三少版本减库存省去了查询步骤，使用sql语句判断，通过返回值1或者0判断减库存结果
        // 少执行一个select操作
        // update miaosha_course set stock_count = stock_count - 1 where id = #{id} and stock_count > 0
        // 另外，龙虾三杀版本的库存单独维护一个表，关联商品id，执行减库存操作时不会影响商品表的效率
        // 但这里的查询操作为订单操作提供对象，看后续能不能优化

        //修改sql后只能保证库存不超，但依旧会生成超过库存数量的订单，因为瞬间的高并发很多都会顺利通过库存判断和重复判断
        // 之后进行秒杀操作，虽然不能减库存，但操作并没有失败，后续下订单操作继续进行，不会回滚
        // 先使用简单的判断减库存的返回值来解决这个问题，后续再看如何优化


        // 缓存库存标记，为true表示已秒杀完，直接返回
        if (localOverMap.get(miaoshaCourseId)){
            model.addAttribute("msg", "秒杀失败，商品已抢完，请选择其他商品!");
            model.addAttribute("target", "/course/list");
            return "/site/operate-result";
        }


        // 再优化，从缓存中预减库存, 缓存中库存小于0，则直接返回, 但缓存也会因高并发减为负数
        long stock = (long) redisTemplate.opsForValue().decrement(RedisKeyUtil.getMiaoshaStockKey(miaoshaCourseId));
        if (stock < 0){
            localOverMap.put(miaoshaCourseId, true);
            model.addAttribute("msg", "秒杀失败，商品已抢完，请选择其他商品!");
            model.addAttribute("target", "/course/list");
            return "/site/operate-result";
        }

        MiaoshaCourse miaoshaCourse = miaoshaCourseService.findMiaoshaCourseById(miaoshaCourseId);
        Course course = courseService.findCourseById(miaoshaCourse.getCourseId());

        if (miaoshaCourse.getStockCount() <= 0){
            model.addAttribute("msg", "秒杀失败，商品已抢完，请选择其他商品!");
            model.addAttribute("target", "/course/list");
            return "/site/operate-result";
        }

        // 判断是否重复秒杀，这里是从数据库查询秒杀订单判断，上一个秒杀是利用唯一主键不能重复插入来判断，后续可以看哪个效果好
        // 使用userId和courseId构成unique唯一索引，防止重复秒杀
        MiaoshaOrder miaoshaOrder = miaoshaOrderService.getMiaoshaOrderByUserIdCourseId(user.getId(), miaoshaCourse.getCourseId());
        if (miaoshaOrder != null){
            model.addAttribute("msg", "秒杀失败，您已经秒杀过该商品!");
            model.addAttribute("target", "/course/list");
            return "/site/operate-result";
        }

        // 减库存，下订单，写入秒杀订单
        // 销量增加还没做
        OrderInfo orderInfo = miaoshaService.miaosha(user, miaoshaCourse, course);

        if (orderInfo == null){
            model.addAttribute("msg", "秒杀失败，商品已抢完，请选择其他商品!");
            model.addAttribute("target", "/course/list");
            return "/site/operate-result";
        }

        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("course", course);
        model.addAttribute("miaoshaCourse", miaoshaCourse);

        return "/site/order-detail";
    }


}
