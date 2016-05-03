package app.test;

import org.junit.Test;

import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.common.utils.EncodeAndDecodeUtils;

public class AppTest {
    @Test
    public void test1() {
        String userId = "449";
        long second = DateUtils.getTime();
        String md5 = EncodeAndDecodeUtils.encodeStrMD5("lejj" + userId + "lejj" + second);
        String base64 = EncodeAndDecodeUtils.encodeStrBase64(md5 + "_" + userId + "_" + second);
        System.err.println(base64);
    }
}
