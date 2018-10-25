package team.redrock.wechatbarrage.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import team.redrock.wechatbarrage.been.WechatUserInfo;
import team.redrock.wechatbarrage.been.jwt.JwtHelper;

/**
 * @Author 余歌
 * @Date 2018/8/19
 * @Description 管理后台要用到的配置
 **/
@Component
@Data
public class ContentConfig {

    @Value("${barrage.next-time}")
    private Integer nextBarrageTime;//两条消息之间的间隔时间
    @Value("${barrage.jwt.effectiva-time}")
    private Integer effectiveTime;

    @Bean
    public JwtHelper<WechatUserInfo> jwtHelper(){
        JwtHelper<WechatUserInfo> jwtHelper =  new JwtHelper<>("HmacSHA256", effectiveTime, "redrock;yes!;web;yes!");
        jwtHelper.setIss("redrock");
        return jwtHelper;
    }
}
