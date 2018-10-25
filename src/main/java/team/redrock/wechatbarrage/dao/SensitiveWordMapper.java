package team.redrock.wechatbarrage.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @Description
 * @Author 余歌
 * @Date 2018/8/24
 **/
@Mapper
@Component
public interface SensitiveWordMapper {
    @Select("SELECT word FROM sensitive_word")
    Set<String> getSensitiveWord();

    @Insert("INSERT INTO sensitive_word(word) VALUE(#{word})")
    void insert(String word);

    @Delete("DELETE FROM sensitive_word WHERE word = #{word}")
    void delete(String word);

    @Insert("SELECT id FROM sensitive_word WHERE word = #{word}")
    Integer search(String word);
}
