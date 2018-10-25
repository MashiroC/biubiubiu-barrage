package team.redrock.wechatbarrage.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import team.redrock.wechatbarrage.been.BarrageMessage;
import team.redrock.wechatbarrage.been.TextBarrageMessage;

import java.util.List;

/**
 * @Author 余歌
 * @Date 2018/8/19
 * @Description 弹幕存库
 **/
@Mapper
@Component
public interface BarrageMapper {
    @Insert("INSERT INTO barrage(openid,text,color,timestamp) VALUE(#{openid},#{text},#{color},#{timestamp})")
    void insert(BarrageMessage barrageMessage);

    @Select("SELECT *,'text' as type FROM barrage LIMIT #{limit},30")
    List<TextBarrageMessage> findBarrageMessage(Integer limit);

    @Select("SELECT id FROM barrage ORDER BY id DESC LIMIT 1")
    Integer findMaxId();

    @Delete("TRUNCATE barrage")
    void init();

}
