package team.redrock.wechatbarrage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import team.redrock.wechatbarrage.been.ResponseEntity;
import team.redrock.wechatbarrage.been.TextBarrageMessage;
import team.redrock.wechatbarrage.been.WechatUserInfo;
import team.redrock.wechatbarrage.been.jwt.Jwt;
import team.redrock.wechatbarrage.been.jwt.JwtHelper;
import team.redrock.wechatbarrage.config.ContentConfig;
import team.redrock.wechatbarrage.service.BarrageCoreService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author 余歌
 * @Date 2018/8/19
 * @Description 弹幕发送等核心功能
 **/
@Slf4j
@RestController
public class BarrageCoreController {

    @Autowired
    private BarrageCoreService coreService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
//    @Value("${wechat.appid}")
//    private String appid;

    @Value("${barrage.open-font}")
    private Integer openFont;
    @Value("${wechat.response-index}")
    private String responseIndex;

    @Autowired
    private ContentConfig contentConfig;

    @Autowired
    BarrageCoreService barrageCoreService;

    @Autowired
    JwtHelper<WechatUserInfo> jwtHelper;

    private static final ResponseEntity OK = new ResponseEntity<>(200, "ok");

    private static final ResponseEntity BAN = new ResponseEntity<>(401, "you are banned");

    private static final ResponseEntity PLEASE_USE_WECHAT = new ResponseEntity<>(403, "please use wechat");

    private static final ResponseEntity NOT_OPEN = new ResponseEntity(404, "screen not open");

    private boolean open = true;

    /***
     * 发送弹幕
     * @param barrageMsg 收到的弹幕
     * @return 成功与否
     */
    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity sendBarrage(TextBarrageMessage barrageMsg, HttpServletRequest request) {
        if (open) {
            String openid = (String) request.getAttribute("openid");
            if (openid == null || openid.length() == 0) {
                return PLEASE_USE_WECHAT;
            }
            String openidWithLine = "_" + openid;

            barrageMsg.setOpenid(openid);
            barrageMsg.setTimestamp(System.currentTimeMillis());

            //检查消息长度
            barrageCoreService.saveBarrage(barrageMsg);
            if (barrageMsg.access()) {
                //检查在不在间隔限制和有没有被ban
                if (coreService.isNotBan(openid, barrageMsg)) {

                    ValueOperations<String, String> operations = redisTemplate.opsForValue();
                    //redis存这条消息 过期时间为两条弹幕之间的间隔限制
                    int time = contentConfig.getNextBarrageTime();
                    if (time != 0) {
                        operations.set(openid, barrageMsg.getText(), contentConfig.getNextBarrageTime(), TimeUnit.SECONDS);
                    }
                    //看权限取掉字体
                    try {
                        int count = Integer.parseInt(operations.get(openidWithLine));
                        operations.set(openidWithLine, String.valueOf(count + 1));
                        if (count < openFont) {
                            barrageMsg.setColor(null);
                            barrageMsg.setLocation(0);
                        }
                    } catch (Throwable e) {
                        operations.set(openidWithLine, "1");
                        barrageMsg.setLocation(0);
                        barrageMsg.setColor(null);
                    }
                    //发送弹幕给前端
                    coreService.send(barrageMsg);
                    return OK;
                }
            }
            log.info("{}发了一条弹幕{}被ban了", barrageMsg.getOpenid(), barrageMsg.getText());
            return BAN;
        }
        return NOT_OPEN;
    }

    @GetMapping("/barrageIndex")
    @ResponseBody
    public ResponseEntity barrageIndex(WechatUserInfo userInfo, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Jwt jwt;
        String jwtStr = request.getHeader("jwt");
        if (jwtStr == null) {
            coreService.saveUser(userInfo);
            jwt = jwtHelper.createJwt(userInfo);
        } else {
            jwt = jwtHelper.fromString(jwtStr);
        }
        log.info("{}打开了弹幕系统", userInfo.getOpenid());
        response.sendRedirect(responseIndex + "?s=" + jwt.toString());
        return ResponseEntity.OK;
    }

    public void openScreen() {
        open = true;
    }

    public void closeScreen() {
        open = false;
    }

}