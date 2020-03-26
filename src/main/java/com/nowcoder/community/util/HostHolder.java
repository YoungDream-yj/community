package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 代替session对象，持有用户信息
 */
@Component
public class HostHolder {

    // 存取值时都获取当前线程对象，再利用当前线程对象创建map存值
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }

}
