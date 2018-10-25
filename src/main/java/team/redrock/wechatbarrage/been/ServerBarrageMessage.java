package team.redrock.wechatbarrage.been;

import lombok.Data;

/**
 * @Description
 * @Author 余歌
 * @Date 2018/9/26
 **/
@Data
public class ServerBarrageMessage extends BarrageMessage {
String command;
String value;
}