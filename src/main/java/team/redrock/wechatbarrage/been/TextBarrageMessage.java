package team.redrock.wechatbarrage.been;

import lombok.Data;


/**
 * @Description
 * @Author 余歌
 * @Date 2018/9/12
 **/
@Data
public class TextBarrageMessage extends BarrageMessage {

    private String openid;
    private String text;
    private String color;
    private Integer location;// -下方 0滚动 +上方

    public boolean access() {
        return this.openid != null && this.text != null && this.text.length() > 0 && this.text.length() <= 20;
    }

    protected TextBarrageMessage(){

    }

}
