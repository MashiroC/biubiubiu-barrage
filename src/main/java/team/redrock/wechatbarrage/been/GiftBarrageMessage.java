package team.redrock.wechatbarrage.been;

import lombok.Data;

import java.util.Set;

/**
 * @Description
 * @Author 余歌
 * @Date 2018/9/12
 **/
@Data
public class GiftBarrageMessage extends BarrageMessage {

    private Set<WechatUserInfo> data;
    private String keyWord;
    private Integer peopleNum;
    private Integer duration;//持续时间

    protected GiftBarrageMessage() {
    }

}
