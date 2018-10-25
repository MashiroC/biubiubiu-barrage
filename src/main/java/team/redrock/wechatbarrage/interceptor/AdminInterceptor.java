package team.redrock.wechatbarrage.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import team.redrock.wechatbarrage.been.jwt.Jwt;
import team.redrock.wechatbarrage.been.jwt.JwtHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description
 * @Author 余歌
 * @Date 2018/8/20
 **/
@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtHelper jwtHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String jwtStr = request.getHeader("jwt");
        if (jwtStr != null) {
            Jwt jwt = jwtHelper.fromString(jwtStr);
            if (jwtHelper.isAccess(jwt) && "adminOpenid".equals(jwt.getParameter("openid"))) {
                return true;
            }
        }
        return false;
    }
}