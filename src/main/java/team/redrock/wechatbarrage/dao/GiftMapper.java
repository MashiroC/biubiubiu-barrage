package team.redrock.wechatbarrage.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author 余歌
 * @Date 2018/10/11
 **/
@Mapper
@Component
public interface GiftMapper {
    @Insert("INSERT INTO gift(openid,type,timestamp) VALUE(#{openid},#{type},#{timestamp})")
    void insert(@Param("openid") String openid, @Param("type") String type, @Param("timestamp") String timestamp);

    @Select("SELECT openid,type,timestamp FROM gift")
    List<Map<String, String>> select();
}
