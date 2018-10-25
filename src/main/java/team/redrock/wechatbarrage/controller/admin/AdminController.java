package team.redrock.wechatbarrage.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import team.redrock.wechatbarrage.been.*;
import team.redrock.wechatbarrage.been.jwt.Jwt;
import team.redrock.wechatbarrage.been.jwt.JwtHelper;
import team.redrock.wechatbarrage.config.ContentConfig;
import team.redrock.wechatbarrage.controller.BarrageCoreController;
import team.redrock.wechatbarrage.dao.BarrageMapper;
import team.redrock.wechatbarrage.dao.UserMapper;
import team.redrock.wechatbarrage.service.BarrageCoreService;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 后台管理的controller
 * @Author 余歌
 * @Date 2018/8/20
 **/
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private ContentConfig contentConfig;
    @Autowired
    private BarrageCoreService coreService;
    @Autowired
    private UserMapper userMapper;
    @Value("${admin.username}")
    private String user;
    @Value("${admin.password}")
    private String pass;
    @Autowired
    private BarrageMapper barrageMapper;
    @Autowired
    private BarrageCoreController coreController;
    @Autowired
    private JwtHelper<WechatUserInfo> jwtHelper;

    private final static ResponseEntity PASSWORD_ERROR = new ResponseEntity(403, "password error");

    private final static ResponseEntity DO_NOT_HAVE_THIS_PARAM = new ResponseEntity(404, "do not have this param");

    @PostMapping("/login")
    public ResponseEntity login(String username, String password, HttpSession session) {
        if (user.equals(username) && pass.equals(password)) {
            WechatUserInfo userInfo = new WechatUserInfo();
            userInfo.setOpenid("adminOpenid");
            Jwt jwt = jwtHelper.createJwt(userInfo);
            Map<String, String> map = new HashMap<>();
            map.put("jwt", jwt.toString());
            return new ResponseEntity(200, "ok", map);
        }
        return PASSWORD_ERROR;
    }

    @PostMapping("/setParam/{key}")
    public ResponseEntity setParam(@PathVariable("key") String key, @RequestParam("value") String value) {
        Field field;
        try {
            field = contentConfig.getClass().getDeclaredField(key);
        } catch (NoSuchFieldException e) {
            return DO_NOT_HAVE_THIS_PARAM;
        }
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            if (field.getType().equals(Integer.class)) {
                field.set(contentConfig, Integer.parseInt(value));
            } else {
                field.set(contentConfig, value);
            }
        } catch (IllegalAccessException e) {
            return ResponseEntity.REQUEST_ERROR;
        }
        return ResponseEntity.OK;
    }

    @GetMapping("/getBarrageList")
    public ResponseEntity<Map<String, Object>> getBarrageMessageList(Integer page) {
        if (page == null || page < 1) {
            page = 1;
        }
        int limit = (page - 1) * 30;
        List<TextBarrageMessage> barrageList = barrageMapper.findBarrageMessage(limit);
        Integer max = barrageMapper.findMaxId();
        if (max == null) {
            max = 0;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("pages", max / 30 + 1);
        param.put("barrages", barrageList);
        return new ResponseEntity<>(200, "ok", param);
    }


    @GetMapping("/getUserList")
    public ResponseEntity getUserList(Integer num) {
        List<WechatUserInfo> userList;
        if (num==null||num <= 0) {
            userList = userMapper.getUserList();
        } else {
            long time = System.currentTimeMillis() - num * 60 * 1000;
            userList = userMapper.getUserListByTime(time);
        }

        return new ResponseEntity<List>(200, "ok", userList);
    }

    @GetMapping("/search")
    public ResponseEntity search(String key) {
        WechatUserInfo userInfo = userMapper.findUserByOpenidORNickname(key);
        return new ResponseEntity<WechatUserInfo>(200, "ok", userInfo);
    }

    @PostMapping("/screen/{commond}")
    public ResponseEntity screenOpen(@PathVariable("commond") String commond) {
        if ("open".equals(commond)) {
            coreController.openScreen();
            return ResponseEntity.OK;
        } else if ("close".equals(commond)) {
            coreController.closeScreen();
            return ResponseEntity.OK;
        } else {
            return ResponseEntity.REQUEST_ERROR;
        }
    }

    @PostMapping("/editFont")
    public ResponseEntity editFont(String font) {
        ServerBarrageMessage serverBarrageMsg = BarrageMessage.createServerMessage("editFont", font);
        coreService.send(serverBarrageMsg);
        return ResponseEntity.OK;
    }

    @PostMapping("/commond")
    public ResponseEntity commond(String key, String value) {
        log.info("向大屏幕发送了：key:{},value:{}", key, value);
        ServerBarrageMessage serverBarrageMessage = BarrageMessage.createServerMessage(key, value);
        coreService.send(serverBarrageMessage);
        return ResponseEntity.OK;
    }

    @PostMapping("/init")
    public ResponseEntity init() {
        coreService.init();
        return ResponseEntity.OK;
    }

}
