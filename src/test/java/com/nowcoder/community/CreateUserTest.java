package com.nowcoder.community;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CreateUserTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Test
    public void createUserTest() throws Exception{

        List<User> users = new ArrayList<>();

//        for (int i = 0; i < 5000; i ++){
//            User user = new User();
//
//            user.setUsername("test" + String.valueOf(i));
//            user.setPassword("123456");
//            user.setEmail("nowdsda@11.com");
//
//            // 注册用户
//            user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
//            user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
//            user.setType(0);
//            user.setStatus(1);
//            user.setActivationCode(CommunityUtil.generateUUID());
//            user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
//            user.setCreateTime(new Date());
//            userMapper.insertUser(user);
//
//            users.add(user);
//        }

        users = userMapper.selectUsers(43, 5000);


        //登录，生成token
        File file = new File("E:/tickets.txt");
        if(file.exists()) {
            file.delete();
        }
        file.createNewFile();
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0);

        for (User user: users){
            Map<String, Object> map = userService.login(user.getUsername(), "123456", 3600 * 24 * 100);
            String ticket = map.get("ticket").toString();

            System.out.println("userId: " + user.getId() + ", ticket: " + ticket);

            String row = user.getId()+","+ticket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + user.getId());
        }
        raf.close();

    }

}
