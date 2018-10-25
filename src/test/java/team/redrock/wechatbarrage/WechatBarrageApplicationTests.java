package team.redrock.wechatbarrage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import team.redrock.wechatbarrage.config.ContentConfig;
import team.redrock.wechatbarrage.controller.admin.AdminController;
import team.redrock.wechatbarrage.controller.admin.GiftController;
import team.redrock.wechatbarrage.dao.GiftMapper;
import team.redrock.wechatbarrage.dao.SensitiveWordMapper;
import team.redrock.wechatbarrage.util.SensitiveWordFilter;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WechatBarrageApplicationTests {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    @Autowired
    private AdminController adminController;

    @Autowired
    private ContentConfig contentCogainfig;
    @Autowired
    private GiftController giftController;
    @Autowired
    private GiftMapper giftMapper;

    @Test
    public void contextLoads() {
    }
//
    @Test
    public void test(){
        System.out.println(adminController.getUserList(0));
    }

}