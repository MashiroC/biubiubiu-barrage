package team.redrock.wechatbarrage.interceptor;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import team.redrock.wechatbarrage.been.ResponseEntity;
import team.redrock.wechatbarrage.been.WechatUserInfo;
import team.redrock.wechatbarrage.been.jwt.Jwt;
import team.redrock.wechatbarrage.been.jwt.JwtHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @Description
 * @Author 余歌
 * @Date 2018/9/11
 **/
@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Value("${wechat.index}")
    private String wechatIndex;

    @Autowired
    private JwtHelper<WechatUserInfo> jwtHelper;

    private ResponseEntity INVALID = new ResponseEntity(402, "jwt is invalid");
    private ResponseEntity BREAKDOWN = new ResponseEntity(402, "jwt is breakdown");
    private Gson gson = new Gson();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String jwtStr = request.getHeader("jwt");
        if (jwtStr == null) {
            String state = request.getParameter("state");
            if (state != null) {
                if ("sfasdfasdfefvee".equals(state)) {
                    return true;
                }
            }
            if (request.getRequestURI().contains("barrageIndex")) {
                response.sendRedirect("https://wx.idsbllp.cn/MagicLoop/index.php?s=/addon/Api/Api/oauth&redirect=" + wechatIndex + "?state=sfasdfasdfefvee");
            }
            return false;
        } else {
            Jwt jwt = jwtHelper.fromString(jwtStr);
            ResponseEntity result;
            if (jwt != null && jwtHelper.isAccess(jwt)) {
                if (!jwt.isOvertime()) {
                    request.setAttribute("openid", jwt.getParameter("openid"));
                    return true;
                }else{
                    result=BREAKDOWN;
                }
            } else {
                result=INVALID;
            }
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            response.getOutputStream()
                    )
            );
            writer.write(gson.toJson(result));
            writer.flush();
            writer.close();
            return false;
        }
    }

}
