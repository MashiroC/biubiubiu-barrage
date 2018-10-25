package team.redrock.wechatbarrage.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.redrock.wechatbarrage.been.ResponseEntity;
import team.redrock.wechatbarrage.dao.BarrageMapper;
import team.redrock.wechatbarrage.dao.UserMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author 余歌
 * @Date 2018/9/25
 **/
@RestController
@RequestMapping("/admin/count")
public class CountController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BarrageMapper barrageMapper;

    @GetMapping("/user")
    public ResponseEntity getUserCount() {
        Integer id = userMapper.getUserMaxId();
        if (id == null) {
            id = 0;
        }
        Map<String, Integer> countMap = new HashMap<>();
        countMap.put("userCount", id);
        return new ResponseEntity<Map>(200, "ok", countMap);
    }

    @GetMapping("/barrage")
    public ResponseEntity getBarrageCount() {
        Integer id = barrageMapper.findMaxId();
        if (id == null) {
            id = 0;
        }
        Map<String, Integer> countMap = new HashMap<>();
        countMap.put("barrageCount", id);
        return new ResponseEntity<Map>(200, "ok", countMap);
    }
}
