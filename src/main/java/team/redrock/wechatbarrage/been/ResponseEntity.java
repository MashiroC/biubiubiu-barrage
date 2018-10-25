package team.redrock.wechatbarrage.been;

import lombok.Data;

/**
 * @Author 余歌
 * @Date 2018/8/19
 * @Description 请求返回的实体
 **/
@Data
public class ResponseEntity<T> {
    private int status;
    private String success;
    private T data;

    public ResponseEntity(int status,String success){
        this.status=status;
        this.success=success;
    }

    public ResponseEntity(int status,String success,T data){
        this.status=status;
        this.success=success;
        this.data=data;
    }

    public final static ResponseEntity OK = new ResponseEntity(200, "ok");

    public final static ResponseEntity REQUEST_ERROR = new ResponseEntity(403, "request error");
}
