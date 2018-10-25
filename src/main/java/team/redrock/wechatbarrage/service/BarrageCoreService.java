package team.redrock.wechatbarrage.service;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import team.redrock.wechatbarrage.been.*;
import team.redrock.wechatbarrage.dao.BarrageMapper;
import team.redrock.wechatbarrage.dao.GiftMapper;
import team.redrock.wechatbarrage.dao.UserMapper;
import team.redrock.wechatbarrage.util.SensitiveWordFilter;

/**
 * @Author 余歌
 * @Date 2018/8/19
 * @Description 弹幕核心功能的service
 **/
@Slf4j
@Component
public class BarrageCoreService {

    boolean high = false;
    String giftKeyWord = "";
    int barrageCount = 0;

    private ChannelGroup group;  //在线的弹幕墙

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    private Gson gson = new Gson();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    BarrageMapper barrageMapper;

    @Autowired
    UserMapper userMapper;

    public void setHigh(boolean high) {
        this.high = high;
    }

    public void setGiftKeyWord(String keyWord) {
        this.giftKeyWord = keyWord;
    }

    public BarrageCoreService() {
        group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    /***
     * 增加一个在线的弹幕强墙数量
     */
    public void addChannel(ChannelHandlerContext ctx) {
        log.info("{} 打开了弹幕墙", ctx.channel().remoteAddress());
        group.add(ctx.channel());
    }

    /***
     * 发消息
     * @param message 弹幕消息
     */
    public void send(BarrageMessage message) {
        if (message instanceof TextBarrageMessage) {
            message.setType("text");
            TextBarrageMessage barrageMsg = (TextBarrageMessage) message;
            String text = barrageMsg.getText();
            //红包的高并发模式下 十条给一条
            if (high) {
                if (text.equals(giftKeyWord)) {
                    synchronized (this) {
                        if (barrageCount < 1) {
                            barrageCount++;
                            return;
                        }
                        barrageCount = 0;
                    }
                }
            }

            int flag = sensitiveWordFilter.checkSensitiveWord(text);
            if (flag != -1) {
                log.warn("{}发送了敏感词{}被拦截", barrageMsg.getOpenid(),barrageMsg.getText());
            } else {
                log.info("{}发送了弹幕{}", barrageMsg.getOpenid(), barrageMsg.getText());
                barrageMsg.setOpenid(null);
                group.writeAndFlush(new TextWebSocketFrame(gson.toJson(barrageMsg)));
            }
        } else if (message instanceof GiftBarrageMessage) {
            message.setType("gift");
            GiftBarrageMessage barrageMsg = (GiftBarrageMessage) message;
            group.writeAndFlush(new TextWebSocketFrame(gson.toJson(barrageMsg)));
        } else {
            message.setType("server");
            ServerBarrageMessage serverMsg = (ServerBarrageMessage) message;
            group.writeAndFlush(new TextWebSocketFrame(gson.toJson(message)));
        }
    }

    /***
     * 弹幕墙离线
     */
    public void remove(ChannelHandlerContext ctx) {
        log.info("{} 弹幕墙离线", ctx.channel().remoteAddress());
        group.remove(ctx.channel());
    }

    /***
     * 判断这个该用户能不能发弹幕
     * @param openid 用户的openid
     * @param nowMsg 用户这一次的消息
     * @return
     */
    public boolean isNotBan(String openid, BarrageMessage nowMsg) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String originTime = operations.get(openid);
        return originTime == null;
    }

    @Async
    public void saveBarrage(BarrageMessage barrageMessage) {
        barrageMapper.insert(barrageMessage);
    }

    @Async
    public void saveUser(WechatUserInfo userInfo) {
        if (userMapper.findUserByOpenidORNickname(userInfo.getOpenid()) != null) {
            userMapper.update(userInfo);
        } else {
            userMapper.insertUser(userInfo);
        }
    }

    public boolean isUserExist(String openid) {
        return userMapper.findUserByOpenidORNickname(openid) != null;
    }

    public void updateUser(WechatUserInfo userInfo) {
        userMapper.update(userInfo);
    }

    public void init() {
        userMapper.init();
        barrageMapper.init();
    }
}
