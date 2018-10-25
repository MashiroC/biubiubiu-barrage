package team.redrock.wechatbarrage.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.redrock.wechatbarrage.been.Node;
import team.redrock.wechatbarrage.dao.SensitiveWordMapper;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * @Description 敏感词过滤
 * @Author 余歌
 * @Date 2018/8/24
 **/
@Component
public class SensitiveWordFilter {
    @Autowired
    private SensitiveWordMapper wordMapper;
    private Node root;
    private boolean open = false;

    @PostConstruct
    public void init() {
        Set<String> set = wordMapper.getSensitiveWord();
        root = new Node();
        Node node = root;
        for (String str : set) {
            node = root;
            for (char ch : str.toCharArray()) {
                if (!node.hasChild(ch)) {
                    node = node.addChild(ch);
                } else {
                    node = node.getChild(ch);
                }
            }
        }
        open = true;
    }

    public void release() {
        root.release();
        open = false;
    }

    public boolean isOpen() {
        return open;
    }

    public int checkSensitiveWord(String text) {
        if (open) {
            Node node = root;
            for (int i = 0; i < text.length(); i++) {
                for (int j = i; j < text.length(); j++) {
                    char c = text.charAt(j);
                    if (node.hasChild(c)) {
                        node = node.getChild(c);
                    } else {
                        break;
                    }
                    if (node.isLeave()) {
                        return i;
                    }
                }
                node = root;
            }
        }
        return -1;
    }
}
