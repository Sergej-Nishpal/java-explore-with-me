package ru.practicum.ewmstat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootContextLoader;

import static org.junit.jupiter.api.Assertions.*;

class ExploreWithMeStatAppTest {

    @Test
    void testContextLoads() {
        assertNotNull(SpringBootContextLoader.class);
    }

    @Test
    void testApplicationRun() {
        ExploreWithMeStatApp.main(new String[]{});
        assertDoesNotThrow(() -> {
        });
    }
}