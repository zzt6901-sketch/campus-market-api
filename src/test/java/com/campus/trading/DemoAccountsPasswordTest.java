package com.campus.trading;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 保证 init.sql 中的演示账号哈希始终匹配 README 文档里的演示密码 123456。
 * 避免再出现"固定哈希与密码不符导致登录 401"的回归。
 */
class DemoAccountsPasswordTest {

    private static final String DEMO_PASSWORD = "123456";
    private static final Pattern BC_HASH = Pattern.compile("\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}");

    @Test
    void everyDemoAccountHashMatchesDemoPassword() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/init.sql"));
        Matcher matcher = BC_HASH.matcher(sql);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        int count = 0;
        while (matcher.find()) {
            String hash = matcher.group();
            assertTrue(
                    encoder.matches(DEMO_PASSWORD, hash),
                    "init.sql 中的哈希与演示密码 123456 不匹配: " + hash);
            count++;
        }
        assertTrue(count >= 4, "init.sql 至少应包含 4 个演示账号哈希，实际 " + count);
    }
}
