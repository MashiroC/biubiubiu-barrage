package team.redrock.wechatbarrage.been.jwt;

/**
 * @Description
 * @Author 余歌
 * @Date 2018/10/10
 **/
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class SecretUtil {
    public static String encode(String algorithm, String key, String text) {
        String encodeStr = null;
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKey);
            byte[] digest = mac.doFinal(text.getBytes("UTF-8"));
            encodeStr = new HexBinaryAdapter().marshal(digest).toLowerCase();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    public static String encodeBase64(String text) {
        String encodeStr = null;
        try {
            encodeStr = Base64.getEncoder().encodeToString(text.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    public static String decodeBase64(String text) {
        return new String(Base64.getDecoder().decode(text.getBytes()));
    }

}