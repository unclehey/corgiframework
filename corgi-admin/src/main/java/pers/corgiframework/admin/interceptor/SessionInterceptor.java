package pers.corgiframework.admin.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pers.corgiframework.admin.annotation.NoNeedLogin;
import pers.corgiframework.dao.domain.MgrUser;
import pers.corgiframework.tool.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

/**
 * Created by syk on 2017/8/23.
 */
public class SessionInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SessionInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            String reqIp = StringUtil.getIpAddr(request);
            logger.info("IP = {}, url = {}, method = {}, contentType = {}, agent = {}", reqIp, request.getRequestURI(), request.getMethod(), request.getContentType(), request.getHeader("User-Agent"));
            // session管理
            HttpSession session = request.getSession(true);
            MgrUser mgrUser = (MgrUser) session.getAttribute("mgrUser");
            // 拦截需要登录的请求
            HandlerMethod hm = (HandlerMethod) handler;
            Method method = hm.getMethod();
            NoNeedLogin noNeedLogin = method.getAnnotation(NoNeedLogin.class);
            if (null != noNeedLogin || (mgrUser != null)) {
                return true;
            }
            response.sendRedirect("tologin.do");
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

}
