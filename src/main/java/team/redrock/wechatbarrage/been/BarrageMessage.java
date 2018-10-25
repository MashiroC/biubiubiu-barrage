package team.redrock.wechatbarrage.been;

import lombok.Data;

import java.util.Set;

/**
 * @Author 余歌
 * @Date 2018/8/19
 * @Description 弹幕消息对象
 **/
@Data
public class BarrageMessage {

    protected String type;
    protected long timestamp;

    public static GiftBarrageMessage createGift(int peopleNum, String keyWord, int duration) {
        GiftBarrageMessage gift = new GiftBarrageMessage();
        gift.setTimestamp(System.currentTimeMillis());
        gift.setKeyWord(keyWord);
        gift.setPeopleNum(peopleNum);
        gift.setDuration(duration);
        return gift;
    }

    public static GiftBarrageMessage openGift(int people, Set<WechatUserInfo> userInfoSet){
        GiftBarrageMessage gift = new GiftBarrageMessage();
        gift.setTimestamp(System.currentTimeMillis());
        gift.setPeopleNum(people);
        gift.setData(userInfoSet);
        return gift;
    }

    public static ServerBarrageMessage createServerMessage(String command,String value){
        ServerBarrageMessage server = new ServerBarrageMessage();
        server.setTimestamp(System.currentTimeMillis());
        server.setCommand(command);
        server.setValue(value);
        return server;
    }
}
