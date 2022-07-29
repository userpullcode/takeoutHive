package com.hive.takeout.filter;

import com.alibaba.fastjson.JSON;
import com.hive.takeout.common.R;
import com.hive.takeout.common.TreadLocalContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",value = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取请求的URL
        String requestUrl = request.getRequestURI();
        log.info("请求路径:"+requestUrl);

        //设置哪些路径可以直接放行
        String[] nocheckUrl = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };

        //判断本次请求是否要进行放行
        boolean flag = checkRequestUrl(nocheckUrl, requestUrl);
        log.info("登录状态："+flag);

        //如果不需要处理就直接放行
        if (flag){
            filterChain.doFilter(request,response);
            return;
        }

        //判断登录状态，如果已登录就直接放行
        Long employeeId = (Long) request.getSession().getAttribute("userId");
        log.info("登录状态："+employeeId);
        if (employeeId!=null){
            TreadLocalContent.set(employeeId);
            filterChain.doFilter(request,response);
            return;
        }

        //如果未登录，就返回未登录结果
        if (employeeId==null){
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return;
        }
    }

    //请求放行检查
    public boolean checkRequestUrl(String[] noCheckUrls,String requestUrl){
        boolean flag  = false;
        for (String noCheckUrl:noCheckUrls){
            if (PATH_MATCHER.match(noCheckUrl,requestUrl)){
                flag = true;
                break;
            }
        }
        return flag;
    }
}
