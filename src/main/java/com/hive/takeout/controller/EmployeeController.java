package com.hive.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hive.takeout.common.R;
import com.hive.takeout.entity.Employee;
import com.hive.takeout.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.digester.Digester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    //进行用户登录
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //将前端发过来的密码加码为MD5
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //根据username到数据库查询到用户
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        LambdaQueryWrapper<Employee> wrapper = queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee user = employeeService.getOne(wrapper);

        //如果查询不到结果就返回登录失败
        if (user==null){
            return R.error("登陆失败");
        }

        //密码对比，如果不一致则返回登录失败
        if (!password.equals(user.getPassword())){
            return R.error("登陆失败");
        }

        //查看员工状态，如果员工状态为禁用则返回员工已禁用结果
        if (user.getStatus()==0){
            return R.error("你已被禁用");
        }

        //登录成功，将员工的信息保存在session中
        request.getSession().setAttribute("userId",user.getId());
        request.getSession().setMaxInactiveInterval(7200);
        return R.success(user);
    }

    //员工退出
    @PostMapping("logout")
    public R<String> logout(HttpServletRequest request){
        //将保存员工的Session去掉
        request.getSession().removeAttribute("userId");
        return R.success("退出成功");
    }

    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("进入到新增员工的方法："+employee.toString());
        //为员工设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    //员工信息分页
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("进入到员工信息分页方法"+page+pageSize+name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        //如果name为空就不执行该方法，不为空就执行
        queryWrapper.like(StringUtils.isNotBlank(name),Employee::getName,name);

        //按照修改时间排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     *1、修改员工状态
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info("进行修改员工状态");
        employeeService.updateById(employee);
        return R.success("修改员工状态成功");
    }

    /**
     * 根据Id查询员工的信息
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        log.info("员工信息："+employee.toString());
        if (employee!=null)
        return R.success(employee);
        return R.error("没有查找到该员工");
    }
}
