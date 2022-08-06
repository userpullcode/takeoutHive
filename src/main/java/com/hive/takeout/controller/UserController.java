package com.hive.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hive.takeout.common.R;
import com.hive.takeout.entity.User;
import com.hive.takeout.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody User user, HttpServletRequest request) {
        log.info("移动端用户登录");
        //得到用户的手机号，并检查在数据库中是否有该手机号，如果有就直接进行登录，如果没有就进行自动注册
        LambdaQueryWrapper<User> getQueryWrapper = new LambdaQueryWrapper();
        getQueryWrapper.eq(User::getPhone,user.getPhone());

        User userFromMapper = userService.getOne(getQueryWrapper);

        if (userFromMapper!=null){
            //将该用户Id保存在session域
            request.getSession().setAttribute("userPhoneId",userFromMapper.getId());
            return R.success("登录成功");
        }

        //进行该手机号自动注册
        //设置该手机号的状态为开启
        user.setStatus(1);
        userService.save(user);

        System.out.println("user============>"+user);
        //将该用户Id保存在session域
        request.getSession().setAttribute("userPhoneId",user.getId());

        return R.success("登录成功");
    }

    //退出登录
    @PostMapping("/loginout")
    public R<String> loginOut(HttpServletRequest request){
        log.info("退出登录");

        //将session域关于用户的数据删除
        request.getSession().removeAttribute("userPhoneId");
        return R.success("退出成功");
    }
}
