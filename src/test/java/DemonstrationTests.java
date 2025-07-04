import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.fail;

public class DemonstrationTests {
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

    @Test
    void thisTestShouldBeFailed() {
        fail();
    }

    @Disabled
    @Test
    void thisTestShouldBeSkipped() {
    }

    @ParameterizedTest()
    @ValueSource(strings = {"string 1", "string 2", "string 3", "string 4", "string 5"})
    void testWithParameterizedTest(String s) {
        System.out.println(s);
    }
}
