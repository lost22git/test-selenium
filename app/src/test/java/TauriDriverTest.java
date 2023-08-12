import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.locks.LockSupport;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;

/**
 * 1. cargo install tauri-driver
 * <p>
 * 2. scoop install -g edgedriver
 * <p>
 * 3. run tauri-driver
 */
public class TauriDriverTest {
    @Test
    void test() throws MalformedURLException {

        var app_exe_path = "C:\\Users\\zzz\\Desktop\\clash-verge\\Clash Verge.exe";
        var server_addr = "http://127.0.0.1:4444";
        var capabilities = new DesiredCapabilities();
        capabilities.setCapability("tauri:options", app_exe_path);
        // TODO: how to add wry to local ?
        capabilities.setBrowserName("wry");

        var driver = new RemoteWebDriver(URI.create(server_addr).toURL(), capabilities);

        var wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(NoSuchElementException.class);

        LockSupport.park();
    }
}
