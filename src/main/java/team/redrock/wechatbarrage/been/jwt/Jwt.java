package team.redrock.wechatbarrage.been.jwt;

/**
 * @Description
 * @Author 余歌
 * @Date 2018/10/10
 **/

import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Jwt {

    private Gson gson = new Gson();

    private HashMap<String, String> header;//jwt的头部部分

    private HashMap<String, String> payload;//jwt的负载部分

    private String signature;//jwt的签名

    /**
     * 拿到头部的参数的map
     */
    public Map<String, String> getHeaderMap() {
        return header;
    }

    /**
     * 拿到负载的参数的map
     */
    public Map<String, String> getPlayloadMap() {
        return payload;
    }

    /**
     * 拿到未用base64编码的头部json串
     */
    public String getHeaderJson() {
        return gson.toJson(header);
    }

    /**
     * 拿到未用base64编码的负载json串
     */
    public String getPlayloadJson() {
        return gson.toJson(payload);
    }

    /**
     * 拿到签名，在此之前应该生成它
     */
    public String getSignature() {
        return signature;
    }

    /**
     * 私有化无参构造方法，并抛出异常来防止被使用反射调用
     */
    private Jwt() {
        throw new RuntimeException("could not do this");
    }

    /**
     * 在拿到一个jwt串后使用jwt串来构造jwt
     *
     * @param headerStr   已base64解码的头部
     * @param playloadStr 已base64解码的负载
     * @param signature   签名
     */
    public Jwt(String headerStr, String playloadStr, String signature) {

        header = gson.fromJson(headerStr, HashMap.class);
        payload = gson.fromJson(playloadStr, HashMap.class);

        this.signature = signature;
    }

    /**
     * 提供加密算法和有效时间来构造一个jwt
     *
     * @param algorithm     加密算法名
     * @param effectiveTime 有效时间
     */
    public Jwt(String algorithm, long effectiveTime) {
        header = new HashMap<>();
        payload = new HashMap<>();

        header.put("typ", "jwt");
        header.put("alg", algorithm);

        refreshEffectiveTime(effectiveTime);
    }

    /**
     * 刷新此jwt的有效时间
     *
     * @param effectiveTime 有效持续时间
     */
    public void refreshEffectiveTime(long effectiveTime) {
        Date date = new Date();
        long nowTime = date.getTime() / 1000;
        payload.put("nbf", String.valueOf(nowTime));
        payload.put("exp", String.valueOf(nowTime + effectiveTime));
    }

    public boolean isOvertime() {
        long now = System.currentTimeMillis();
        long jwtExp = Long.parseLong(payload.get("exp"));
        long jwtNbf = Long.parseLong(payload.get("nbf"));
        return now < jwtExp && now > jwtNbf;
    }

    /**
     * 设置负载参数
     *
     * @param key   参数名
     * @param value 参数值
     */
    public void setParameter(String key, String value) {
        payload.put(key, value);
    }

    /**
     * 拿到负载参数
     *
     * @param key 参数名
     * @return 参数值
     */
    public String getParameter(String key) {
        return payload.get(key);
    }

    /**
     * 生成签名
     *
     * @param key 加密的私钥
     */
    public void initSecret(String key) {
        String text = SecretUtil.encodeBase64(getHeaderJson()) + "." + SecretUtil.encodeBase64(getPlayloadJson());
        signature = SecretUtil.encode(header.get("alg"), key, text);
    }

    /**
     * 得到jwt串
     *
     * @return jwt
     */
    @Override
    public String toString() {
        if (signature != null) {
            String headerJson = getHeaderJson();
            String payloadJson = getPlayloadJson();
            return SecretUtil.encodeBase64(headerJson) + "." + SecretUtil.encodeBase64(payloadJson) + "." + signature;
        } else {
            throw new RuntimeException("jwt signature not init!");
        }
    }

}