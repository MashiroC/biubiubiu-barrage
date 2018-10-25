package team.redrock.wechatbarrage.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import team.redrock.wechatbarrage.been.WechatUserInfo;

import java.util.List;
import java.util.Set;

@Mapper
@Component
public interface UserMapper {

    @Select("SELECT * FROM user")
    List<WechatUserInfo> getUserList();

    @Select("select * from user where openid in (select openid from barrage where timestamp>#{timestamp} group by openid)")
    List<WechatUserInfo> getUserListByTime(Long timestamp);

    @Select("SELECT * FROM user WHERE openid = #{key} OR nickname = #{key} limit 1")
    WechatUserInfo findUserByOpenidORNickname(String key);

    @Insert("INSERT INTO user(openid,nickname,headimgurl) VALUE(#{openid},#{nickname},#{headimgurl})")
    void insertUser(WechatUserInfo userInfo);

    @Select("SELECT id from user order by id desc limit 1")
    Integer getUserMaxId();

    @Select("SELECT * FROM user WHERE openid in (SELECT C.openid FROM (SELECT openid FROM barrage WHERE text= #{keyword} AND timestamp>#{timestamp} ORDER BY RAND() LIMIT #{num}) C);")
    Set<WechatUserInfo> findOpenidRandom(@Param("keyword") String keyword, @Param("timestamp") String timestamp, @Param("num") Integer num);

    @Update("UPDATE user SET headimgurl=#{headimgurl},nickname=#{nickname} WHERE openid = #{openid}")
    void update(WechatUserInfo userInfo);

    @Delete("TRUNCATE user")
    void init();
}
