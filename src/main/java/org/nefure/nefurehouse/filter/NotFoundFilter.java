package org.nefure.nefurehouse.filter;

import cn.hutool.core.util.ReflectUtil;
import org.apache.catalina.connector.Response;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 处理资源找不到的情况（否则前端转发到首页）
 * @author nefure
 * @date 2022/3/12 15:03
 */
@Order(0)
@WebFilter(value = "/*")
@Component
public class NotFoundFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest,servletResponse);

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if(Objects.equals(response.getStatus(), HttpStatus.NOT_FOUND.value())){
            Response res = (Response) ReflectUtil.getFieldValue(servletResponse, "response");
            if(res != null){
                //  修改状态，默认情况 放过调用链执行后 response.setStatus 失败
                res.setAppCommitted(false);
                res.setSuspended(false);

                response.setStatus(200);
            }
        }
    }
}
