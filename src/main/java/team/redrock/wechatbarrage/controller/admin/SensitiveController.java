package team.redrock.wechatbarrage.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.redrock.wechatbarrage.been.ResponseEntity;
import team.redrock.wechatbarrage.dao.SensitiveWordMapper;
import team.redrock.wechatbarrage.util.SensitiveWordFilter;

/**
 * @Description
 * @Author 余歌
 * @Date 2018/9/25
 **/
@RestController
@RequestMapping("/admin/sensitive")
public class SensitiveController {
    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;
    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;


    private final static ResponseEntity SENSITIVE_WORD_FILTER_IS_OPEN = new ResponseEntity(200, "sensitive word filter is open");

    private final static ResponseEntity SENSITIVE_WORD_FILTER_IS_NOT_OPEN = new ResponseEntity(200, "sensitive word filter is not open");

    @PostMapping("/{command}")
    public ResponseEntity openSensitive(@PathVariable("command") String command) {
        synchronized (this) {
            if ("open".equals(command)) {
                if (sensitiveWordFilter.isOpen()) {
                    return SENSITIVE_WORD_FILTER_IS_OPEN;
                } else {
                    sensitiveWordFilter.init();
                    return ResponseEntity.OK;
                }
            } else if ("close".equals(command)) {
                if (!sensitiveWordFilter.isOpen()) {
                    return SENSITIVE_WORD_FILTER_IS_NOT_OPEN;
                } else {
                    sensitiveWordFilter.release();
                    return ResponseEntity.OK;
                }
            }
            return ResponseEntity.REQUEST_ERROR;
        }
    }

    @PostMapping("/add")
    public ResponseEntity addSensitive(String word) {
        if (word != null && word.length() != 0 && sensitiveWordMapper.search(word) == -1) {
            sensitiveWordMapper.insert(word);
            return ResponseEntity.OK;
        }
        return ResponseEntity.REQUEST_ERROR;
    }

    @PostMapping("/remove")
    public ResponseEntity removeSensitive(String word) {
        if (word != null && word.length() != 0 && sensitiveWordMapper.search(word) != -1) {
            sensitiveWordMapper.delete(word);
            return ResponseEntity.OK;
        }
        return ResponseEntity.REQUEST_ERROR;
    }
}
