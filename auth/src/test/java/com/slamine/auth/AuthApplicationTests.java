package com.slamine.auth;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.IntPredicate;

@SpringBootTest
class AuthApplicationTests {

    @Test
    void contextLoads() {
        IntPredicate intPredicate = i -> i == 0;
        Assertions.assertThat(intPredicate);
    }

}
