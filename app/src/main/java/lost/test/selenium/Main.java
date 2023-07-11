package lost.test.selenium;

import static java.lang.System.out;
import static org.openqa.selenium.By.*;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.locks.LockSupport;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;

public class Main {

    public static void main(String[] args) {
        var driverOption = new ChromeOptions()
                .setBinary("E:\\scoop\\global\\shims\\brave-beta.exe")
                //            .addArguments("--headless=new")
                //            .addArguments("--incognito") // private mode for brave
                .addExtensions(new File("C:\\Users\\zzz\\Desktop\\buster\\buster.crx"));

        var proxy = new Proxy().setSocksProxy("localhost:55556").setSocksVersion(5);
        driverOption.setProxy(proxy);

        var driver = new ChromeDriver(driverOption);

        var wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(NoSuchElementException.class);

        var uri = "https://www.cutestcloud.com/#/register";
        try {
            driver.get(uri);
            var title = driver.getTitle();
            out.println("title = " + title);

            // find register button and click
            var register_btn_xpath = "//*[@id=\"root\"]/div[2]/div[2]/div[2]/button";
            var register_btn = wait.until(d -> d.findElement(xpath(register_btn_xpath)));
            out.println("register_btn = " + register_btn);
            register_btn.click();

            // find captcha dialog iframe and enter
            var captcha_dialog_class = "ant-modal";
            var captcha_dialog = wait.until(d -> d.findElement(className(captcha_dialog_class)));
            out.println("captcha_dialog = " + captcha_dialog);
            var captcha_dialog_iframe = wait.until(d -> d.findElement(tagName("iframe")));
            out.println("captcha_dialog_iframe = " + captcha_dialog_iframe);
            driver.switchTo().frame(captcha_dialog_iframe);

            // find captcha check box and click it
            var captcha_checkbox_id = "recaptcha-anchor";
            var captcha_checkbox = driver.findElement(id(captcha_checkbox_id));
            out.println("captcha_checkbox = " + captcha_checkbox);
            captcha_checkbox.click();

            // find captcha challenge iframe and enter
            driver.switchTo().defaultContent();
            var captcha_challenge_iframe_xpath = "/html/body/div[3]/div[4]/iframe";
            var captcha_challenge_iframe = wait.until(d -> d.findElement(xpath(captcha_challenge_iframe_xpath)));
            out.println("captcha_challenge_iframe = " + captcha_challenge_iframe);
            driver.switchTo().frame(captcha_challenge_iframe);

            // find audio button and click it
            var audio_btn_class = "rc-button-audio";
            var audio_btn = wait.until(d -> d.findElement(className(audio_btn_class)));
            audio_btn.click();

            LockSupport.parkNanos(Duration.ofSeconds(1).toNanos());

            // find buster and click it
            var buster_btn_class = "help-button-holder";
            var buster_btn = wait.until(d -> d.findElement(className(buster_btn_class)));
            out.println("buster_btn = " + buster_btn);
            buster_btn.click();

            LockSupport.park();
        } finally {
            driver.quit();
        }
    }
}
