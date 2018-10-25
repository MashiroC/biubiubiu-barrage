package team.redrock.wechatbarrage.been;

import lombok.Data;

/**
 * @Author 余歌
 * @Date 2018/8/19
 * @Description 用来发送给客户端的用户信息
 **/
@Data
public class WechatUserInfo {
String openid;
String nickname;
String headimgurl;

    @Override
    public boolean equals(Object o) {
        WechatUserInfo that = (WechatUserInfo) o;

        if(that==null||that.getOpenid()==null||this.getOpenid()==null) return false;

        return that.getOpenid().equals(this.getOpenid());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (openid != null ? openid.hashCode() : 0);
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        return result;
    }

    public boolean access() {
        return openid != null && openid.length() != 0 && headimgurl != null && headimgurl.length() != 0 && nickname != null && nickname.length() != 0;
    }
}
