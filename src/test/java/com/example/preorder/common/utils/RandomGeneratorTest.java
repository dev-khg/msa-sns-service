package com.example.preorder.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class RandomGeneratorTest {

    @Test
    @DisplayName("랜덤으로 생성된 숫자는 6자리 이상이어야 한다.")
    void random_digits_must_6_letters() {
        // given

        for (int i = 0; i < 10; i++) {
            // when
            int randomCode = RandomGenerator.generateSixDigitsRandomCode();

            // then
            assertTrue((randomCode / 100000) > 0);
            assertTrue((randomCode / 100000) < 10);
        }
    }
}