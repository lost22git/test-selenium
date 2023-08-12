import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ATest {

    @Test
    void test() throws IOException, InterruptedException {
        var cmd = List.of("pwsh", "-NoProfile", "-NoLogo", "-Command", "appium");
        var p = new ProcessBuilder(cmd).inheritIO().start();
        p.waitFor();
    }
}
