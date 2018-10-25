package team.redrock.wechatbarrage.been;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description
 * @Author 余歌
 * @Date 2018/8/24
 **/
public class Node {
    private char value;//当前点
    private boolean end = false;
    private Set<Node> childNode;

    public char getValue() {
        return value;
    }

    public Node() {
        childNode = new HashSet<>();
    }

    public Node(char ch) {
        this.value = ch;
    }

    /***
     * 在当前节点下增加子节点
     * @param ch 要增加的节点的值
     * @return 增加后的节点
     */
    public Node addChild(char ch) {
        if (childNode == null) {
            childNode = new HashSet<>();
        }
        if (getChild(ch) != null) {
            throw new RuntimeException("该节点已存在");
        }
        Node newNode = new Node(ch);
        childNode.add(newNode);
        return newNode;
    }

    /***
     * 得到值为ch的节点
     * @param ch 节点的值
     * @return 查找到的节点
     */
    public Node getChild(char ch) {
        for (Node node : childNode) {
            if (node.getValue() == ch) {
                return node;
            }
        }
        return null;
    }

    /***
     * 判断是否存在值为ch的节点
     * @param ch 节点的值
     */
    public boolean hasChild(char ch) {
        if (childNode != null) {
            for (Node node : childNode) {
                if (node.getValue() == ch) {
                    return true;
                }
            }
        }
        return false;
    }

    /***
     * 判断是否为叶子节点
     */
    public boolean isLeave() {
        return childNode == null || childNode.size() == 0;
    }

    public void release() {
        if (childNode != null) {
            for (Node node : childNode) {
                node.release();
                node=null;
            }
            childNode=null;
        }
    }

}
