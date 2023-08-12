import static org.openqa.selenium.By.xpath;

import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.options.WindowsOptions;
import java.net.URL;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;

public class AppiumTest {

    @Test
    public void test() throws Exception {

        var windowsOptions = new WindowsOptions().setApp("C:\\Users\\zzz\\Desktop\\clash-verge\\Clash Verge.exe");
        var driver = new WindowsDriver(new URL("http://127.0.0.1:4723"), windowsOptions);

        var wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);

        // enter profiles page
        var profiles_btn_xpath =
                "/Window/Pane/Pane/Pane[3]/Pane/Pane[1]/Pane/Pane[2]/Pane/Pane/Pane/Document/Group/Group[1]/List/ListItem[2]/Button";
        var profiles_btn = wait.until(d -> d.findElement(xpath(profiles_btn_xpath)));
        profiles_btn.click();

        // open cute profile context menu
        var cute_profile_btn_xpath =
                "/Window/Pane/Pane/Pane[3]/Pane/Pane[1]/Pane/Pane[2]/Pane/Pane/Pane/Document/Group/Group/Group[3]/Group[12]";
        var cute_profile_btn = wait.until(d -> d.findElement(xpath(cute_profile_btn_xpath)));
        cute_profile_btn.click();

        // TODO: ERROR here
        new Actions(driver)
                .contextClick(cute_profile_btn)
                //            .sendKeys(Keys.ARROW_DOWN)
                //            .sendKeys(Keys.ENTER)
                //            .build()
                .perform();

        // select edit info and open cute profile edit modal
        var cute_profile_edit_info_btn_xpath =
                "/Window/Pane/Pane/Pane[3]/Pane/Pane[1]/Pane/Pane[2]/Pane/Pane/Pane/Document/Group/Group[2]/Menu/MenuItem[2]";
        var cute_profile_edit_info_btn = wait.until(d -> d.findElement(xpath(cute_profile_edit_info_btn_xpath)));
        cute_profile_edit_info_btn.click();

        // edit cute profile subscription
        var cute_profile_subscription_edit_xpath =
                "/Window/Pane/Pane/Pane[3]/Pane/Pane[1]/Pane/Pane[2]/Pane/Pane/Pane/Document/Group/Group[2]/Window/Group[4]/Edit";
        var cute_profile_subscription_edit =
                wait.until(d -> d.findElement(xpath(cute_profile_subscription_edit_xpath)));
        System.out.println("cute_profile_subscription_edit.getText() = " + cute_profile_subscription_edit.getText());

        // save cute profile info
        var cute_profile_edit_info_saved_btn_xpath =
                "/Window/Pane/Pane/Pane[3]/Pane/Pane[1]/Pane/Pane[2]/Pane/Pane/Pane/Document/Group/Group[2]/Window/Button[2]";
        var cute_profile_edit_info_saved_btn =
                wait.until(d -> d.findElement(xpath(cute_profile_edit_info_saved_btn_xpath)));
        cute_profile_edit_info_saved_btn.click();

        // refresh cute profile
        var cute_profile_refresh_btn_xpath =
                "/Window/Pane/Pane/Pane[3]/Pane/Pane[1]/Pane/Pane[2]/Pane/Pane/Pane/Document/Group/Group/Group[3]/Group[12]/Button";
        var cute_profile_refresh_btn = wait.until(d -> d.findElement(xpath(cute_profile_refresh_btn_xpath)));
        cute_profile_refresh_btn.click();
    }
}
