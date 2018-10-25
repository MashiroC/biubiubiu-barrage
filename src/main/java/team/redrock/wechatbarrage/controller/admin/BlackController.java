package team.redrock.wechatbarrage.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.redrock.wechatbarrage.been.ResponseEntity;
import team.redrock.wechatbarrage.been.WechatUserInfo;
import team.redrock.wechatbarrage.dao.UserMapper;

import java.util.List;
import java.util.Vector;

/**
 * @Description
 * @Author 余歌
 * @Date 2018/9/25
 **/
@RestController
@RequestMapping("/admin/black")
public class BlackController {

    private Vector<WechatUserInfo> blackList = new Vector<>();//这里用vector是因为线程安全

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/list")
    public ResponseEntity getBlackList() {
        return new ResponseEntity<List>(200, "ok", blackList);
    }

    @PostMapping("/add")
    public ResponseEntity addBlackList(String openid) {
        WechatUserInfo userInfo = userMapper.findUserByOpenidORNickname(openid);
        if (userInfo != null) {
            redisTemplate.opsForValue().set(openid, "black");
            blackList.add(userInfo);
            return ResponseEntity.OK;
        } else {
            return ResponseEntity.REQUEST_ERROR;
        }
    }

    @PostMapping("/remove")
    public ResponseEntity removeBlackList(String openid) {
        WechatUserInfo userInfo = userMapper.findUserByOpenidORNickname(openid);
        if (userInfo != null && blackList.contains(userInfo)) {
            blackList.remove(userInfo);
            redisTemplate.delete(openid);
            return ResponseEntity.OK;
        }
        return ResponseEntity.REQUEST_ERROR;
    }
}
