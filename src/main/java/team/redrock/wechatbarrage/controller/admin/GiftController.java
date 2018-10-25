package team.redrock.wechatbarrage.controller.admin;

import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.redrock.wechatbarrage.been.*;
import team.redrock.wechatbarrage.dao.GiftMapper;
import team.redrock.wechatbarrage.dao.UserMapper;
import team.redrock.wechatbarrage.service.BarrageCoreService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description
 * @Author 余歌
 * @Date 2018/9/25
 **/
@RestController()
@RequestMapping("/admin/gift")
public class GiftController {

    @Autowired
    private BarrageCoreService coreService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    GiftMapper giftMapper;

    @PostMapping("/begin")
    public ResponseEntity giftbegin(Integer peopleNum, String keyWord, Integer duration) {
        if (peopleNum != null && peopleNum != 0 && keyWord != null && keyWord.length() != 0 && duration != null && duration != 0) {
            coreService.setHigh(true);
            coreService.setGiftKeyWord(keyWord);
            GiftBarrageMessage gift = BarrageMessage.createGift(peopleNum, keyWord, duration);
            coreService.send(gift);
            giftOpen(giftMapper, duration, peopleNum, keyWord);
            return ResponseEntity.OK;
        }
        return ResponseEntity.REQUEST_ERROR;
    }

    public void giftOpen(GiftMapper giftMapper, Integer duration, Integer peopleNum, String keyword) {
        new Thread(() -> {
            try {
                Thread.sleep(duration * 1000);
                coreService.setHigh(false);
                Set<WechatUserInfo> userSet;
                userSet = userMapper.findOpenidRandom(keyword, String.valueOf(System.currentTimeMillis() - duration * 1000), peopleNum);
                for (WechatUserInfo userInfo : userSet) {
                    giftMapper.insert(userInfo.getOpenid(), "barrage", String.valueOf(System.currentTimeMillis()));
                }
                coreService.send(BarrageMessage.openGift(peopleNum, userSet));
            } catch (InterruptedException | MyBatisSystemException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @PostMapping("/add")
    public ResponseEntity add(String openid) {
        giftMapper.insert(openid, "random", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.OK;
    }

    @GetMapping("/show")
    public ResponseEntity show() {
        List<Map<String, String>> list = giftMapper.select();
        return new ResponseEntity(200, "ok", list);
    }

    @PostMapping("/randomGift")
    public ResponseEntity randomGift(){
        ServerBarrageMessage serverBarrageMessage = BarrageMessage.createServerMessage("randomGift","begin");
        coreService.send(serverBarrageMessage);
        return ResponseEntity.OK;
    }
}
