package team.redrock.wechatbarrage.been.jwt;

/**
 * @Description
 * @Author 余歌
 * @Date 2018/10/10
 **/

import team.redrock.wechatbarrage.been.WechatUserInfo;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

public class JwtHelper<T> {
    private String secretKey;
    private String algorithm;//加密方法
    private long effectiveTime;//有效时间 单位 秒
    private String iss;//jwt签发者
    private String sub;//jwt所面向的用户
    private String aud;//接收jwt的一方

    public void setIss(String iss) {
        this.iss = iss;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    /**
     * 创建一个Helper
     *
     * @param alg           加密算法
     * @param effectiveTime 有效时间
     * @param secretKey     密钥
     */
    public JwtHelper(String alg, long effectiveTime, String secretKey) {
        this.algorithm = alg;
        this.effectiveTime = effectiveTime;
        this.secretKey = secretKey;
    }

    /**
     * 生成一个jwt
     *
     * @param t 放在负载的数据
     * @return 生成的jwt
     */
    public Jwt createJwt(T t) {

        Jwt jwt = new Jwt(algorithm, effectiveTime);
        if (iss != null) {
            jwt.setParameter("iss", iss);
        }
        if (sub != null) {
            jwt.setParameter("sub", sub);
        }
        if (aud != null) {
            jwt.setParameter("aud", aud);
        }

        if (t != null) {
            Field[] fields = t.getClass().getDeclaredFields();

            try {

                for (Field field : fields) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                        jwt.setParameter(field.getName(), String.valueOf(field.get(t)));
                    }
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        jwt.initSecret(secretKey);

        return jwt;
    }

    /**
     * 从一个jwt字符串构造jwt对象
     *
     * @param jwtStr jwt字符串
     * @return 生成的jwt
     */
    public Jwt fromString(String jwtStr) {
        Jwt jwt = null;
        if (jwtStr != null) {
            String[] str = jwtStr.split("\\.");
            try {
                String header = SecretUtil.decodeBase64(str[0]);
                String payload = SecretUtil.decodeBase64(str[1]);
                String secret = str[2];
                jwt = new Jwt(header, payload, secret);
            } catch (Throwable e) {
                System.out.println("传入了错误的jwt");
            }
        }
        return jwt;
    }

    /**
     * 验证jwt有效性
     *
     * @param jwt jwt
     * @return 正确与否
     */
    public boolean isAccess(Jwt jwt) {
        String headerJson = jwt.getHeaderJson();
        String playloadJson = jwt.getPlayloadJson();
        String secret = jwt.getSignature();

        String encodeHeader = SecretUtil.encodeBase64(headerJson);
        String encodePlayload = SecretUtil.encodeBase64(playloadJson);
        String text = encodeHeader + "." + encodePlayload;
        String encodeText = SecretUtil.encode(algorithm, secretKey, text);

        return encodeText.equals(secret);
    }

}
