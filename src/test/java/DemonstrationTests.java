import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DemonstrationTests {
    @Test
    void thisTestShouldBeFailed() {
        assertFalse(true);
    }

    @Disabled
    @Test
    void thisTestShouldBeSkipped() {
    }

    @BeforeAll
    static void testWithBeforeAll() {
    }

    @AfterAll
    static void testWithAfterAll() {
    }

    @BeforeEach
    void testWithBeforeEach() {
    }

    @AfterEach
    void testWithAfterEach() {
    }

//    @ParameterizedTest
//    void testWithAfterEach(String s) {
//    }
}
