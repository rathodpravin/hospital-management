package com.hospital;
import com.hospital.util.JwtUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
class JwtUtilTest {
    @Test void generatesAndParsesToken() {
        JwtUtil jwt = new JwtUtil("test-secret-key-test-secret-key-12345", 60000);
        String token = jwt.generate("admin", "ADMIN");
        assertEquals("admin", jwt.parse(token).getSubject());
        assertEquals("ADMIN", jwt.parse(token).get("role", String.class));
    }
}
