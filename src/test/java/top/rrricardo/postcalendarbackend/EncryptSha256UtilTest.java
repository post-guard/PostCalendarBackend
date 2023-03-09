package top.rrricardo.postcalendarbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import top.rrricardo.postcalendarbackend.utils.EncryptSha256Util;

@SpringBootTest
public class EncryptSha256UtilTest {
    @Test
    void Sha256StringTest1() {
        var input = "4008558123";
        var except = "73b5425c542aeca92e4cfb83f0667ea6ddc1bef3024f4fc59e5435aef2ea6f7c";

        var actual = EncryptSha256Util.sha256String(input);
        Assert.isTrue(except.equals(actual), actual);
    }

    @Test
    void Sha256StringTest2() {
        var input = "2021211180";
        var except = "e0d6ba2d19c0dbb25287043a27979b2fb1da0327a3b61a83669fb687bb2653bb";

        var actual = EncryptSha256Util.sha256String(input);
        Assert.isTrue(!except.equals(actual), actual);
    }
}
