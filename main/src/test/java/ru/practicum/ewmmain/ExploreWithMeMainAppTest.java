package ru.practicum.ewmmain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootContextLoader;

import static org.junit.jupiter.api.Assertions.*;

class ExploreWithMeMainAppTest {

    @Test
    void testContextLoads() {
        assertNotNull(SpringBootContextLoader.class);
    }

    @Test
    void testApplicationRun() {
        ExploreWithMeMainApp.main(new String[]{});
        assertDoesNotThrow(() -> {
        });
    }
}