package team.redrock.wechatbarrage.util;

import java.io.*;

/**
 * @Author 余歌
 * @Date 2018/8/19
 * @Description 处理流的util
 **/
public class StreamUtil {
    public static void writeStream(OutputStream out,String text){
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(out)
            );
        try {
            writer.write(text);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getText(InputStream input) {
        String result = null;
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(input)
        );
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            if (sb.length() != 0) {
                result = sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
