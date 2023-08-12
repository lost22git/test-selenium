package lost.test.selenium;

import static java.lang.System.out;
import static org.openqa.selenium.By.*;

import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.options.WindowsOptions;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.LockSupport;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TimeoutException;
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
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);

        var register_page_uri = "https://www.cutestcloud.com/#/register";
        var dashboard_page_uri = "https://www.cutestcloud.com/#/dashboard";

        try {
            driver.get(register_page_uri);
            var title = driver.getTitle();
            out.println("title = " + title);

            // email input
            out.println("input email...");
            var email_input_xpath = "//*[@id=\"root\"]/div[2]/div[2]/div[2]/input[1]";
            var email_input = wait.until(d -> d.findElement(xpath(email_input_xpath)));
            out.println("email_input = " + email_input);
            email_input.clear();
            email_input.sendKeys(randomEmail());

            // password input
            out.println("input password...");
            var passwd = randomPasswd();
            var passwd_input_xpath = "//*[@id=\"root\"]/div[2]/div[2]/div[2]/span[1]/input";
            var passwd_input = wait.until(d -> d.findElement(xpath(passwd_input_xpath)));
            out.println("passwd_input = " + passwd_input);
            passwd_input.clear();
            passwd_input.sendKeys(passwd);

            // password resubmit input
            out.println("input password again...");
            var passwd_resubmit_input_xpath = "//*[@id=\"root\"]/div[2]/div[2]/div[2]/span[2]/input";
            var passwd_resubmit_input = wait.until(d -> d.findElement(xpath(passwd_resubmit_input_xpath)));
            out.println("passwd_resubmit_input = " + passwd_resubmit_input);
            passwd_resubmit_input.clear();
            passwd_resubmit_input.sendKeys(passwd);

            var current_dashboard_page = false;

            while (!current_dashboard_page) {
                out.println("register...");

                driver.switchTo().defaultContent();

                // find register button and click
                var register_btn_xpath = "//*[@id=\"root\"]/div[2]/div[2]/div[2]/button";
                var register_btn = wait.until(d -> d.findElement(xpath(register_btn_xpath)));
                out.println("register_btn = " + register_btn);
                register_btn.click();

                try {
                    current_dashboard_page = new FluentWait<WebDriver>(driver)
                            .withTimeout(Duration.ofSeconds(5))
                            .pollingEvery(Duration.ofSeconds(1))
                            .until(d -> d.getCurrentUrl().equals(dashboard_page_uri));
                } catch (TimeoutException ignored) {
                }

                if (current_dashboard_page) break;

                out.println("captcha...");

                // find captcha dialog iframe and enter
                var captcha_dialog_class = "ant-modal";
                var captcha_dialog = wait.until(d -> d.findElement(className(captcha_dialog_class)));
                out.println("captcha_dialog = " + captcha_dialog);
                var captcha_dialog_iframe = wait.until(d -> d.findElement(tagName("iframe")));
                out.println("captcha_dialog_iframe = " + captcha_dialog_iframe);

                driver.switchTo().frame(captcha_dialog_iframe);

                // find captcha check box and click it
                var captcha_checkbox_id = "recaptcha-anchor";
                var captcha_checkbox = wait.until(d -> d.findElement(id(captcha_checkbox_id)));
                out.println("captcha_checkbox = " + captcha_checkbox);
                captcha_checkbox.click();

                driver.switchTo().defaultContent();

                // find captcha challenge iframe and enter
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

                try {
                    current_dashboard_page = wait.until(d -> d.getCurrentUrl().equals(dashboard_page_uri));
                } catch (TimeoutException ignore) {
                }
            }

            out.println("enter dashboard page...");
            driver.switchTo().defaultContent();

            // open subscription dialog
            out.println("open subscription dialog...");
            var open_subscription_dialog_btn_xpath =
                    "//*[@id=\"root\"]/div[2]/div[3]/div[2]/div/div[2]/div[1]/div/div[3]/button";
            var open_subscription_dialog_btn =
                    wait.until(d -> d.findElement(xpath(open_subscription_dialog_btn_xpath)));
            open_subscription_dialog_btn.click();

            // find subscription dialog
            var subscription_dialog_classname = "ant-modal";
            var subscription_dialog = wait.until(d -> d.findElement(className(subscription_dialog_classname)));

            // copy subscription
            out.println("copy subscription...");
            var copy_subscription_btn =
                    subscription_dialog.findElement(className("ant-modal-body")).findElement(xpath("div[1]"));
            out.println("copy_subscription_btn = " + copy_subscription_btn);
            copy_subscription_btn.click();

            // get subscription from clipboard
            out.println("get subscription from clipboard...");
            var subscription = getClipboardContent();
            out.println("subscription = " + subscription);

            if (subscription == null) return;

            // update clash profile
            out.println("update clash profile...");
            updateClashProfile(subscription);

            // restart clash
            out.println("restart clash...");
            restartClash();

        } finally {
            driver.quit();
        }
    }

    static String randomEmail() {
        return UUID.randomUUID().toString().replace("-", "") + "@end.tw";
    }

    static String randomPasswd() {
        return "ringbuffer";
    }

    static String getClipboardContent() {
        try {
            var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            var contents = clipboard.getContents(null);
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) contents.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    static void updateClashProfile(String subscription) {
        var path = Paths.get("C:\\Users\\zzz\\.config\\clash-verge\\profiles.yaml");
        try {
            var str = Files.readString(path);

            var l_index = str.indexOf("file: r9qrJcrdSehf.yaml");
            var r_index = str.lastIndexOf("- name: 最萌の云 - CuteCloud");

            String newStr = str.substring(0, l_index)
                    + str.substring(l_index, r_index)
                            .replaceFirst(
                                    "([\\s\\S]+r9qrJcrdSehf[\\s\\S]+?url: )\\S+?(\n[\\s\\S]+)",
                                    "$1" + subscription + "$2")
                    + str.substring(r_index);

            Files.writeString(path, newStr);
        } catch (Exception ignore) {
        }
    }

    static void restartClash() {

        ProcessHandle.allProcesses()
                .filter(p -> p.info()
                        .command()
                        .map(n -> n.contains("Clash Verge.exe"))
                        .orElse(false))
                .forEach(ProcessHandle::destroyForcibly);

        startAndRefreshClash();
    }

    static void startAndRefreshClash() {
        out.println("start appium server...");
        ProcessHandle appium_server_process;
        try {
            var cmd = List.of("pwsh", "-NoProfile", "-NoLogo", "-Command", "appium");
            appium_server_process = new ProcessBuilder(cmd)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start()
                    .toHandle();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // appium start is slow..., so we need sleep more time
        LockSupport.parkNanos(Duration.ofSeconds(5).toNanos());

        WindowsDriver driver = null;
        try {
            var windowsOptions = new WindowsOptions().setApp("C:\\Users\\zzz\\Desktop\\clash-verge\\Clash Verge.exe");
            URL appium_server_addr = null;
            try {
                appium_server_addr = URI.create("http://127.0.0.1:4723/").toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            // start clash verge
            out.println("start clash verge...");
            driver = new WindowsDriver(appium_server_addr, windowsOptions);

            var wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(5))
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(NoSuchElementException.class);

            // enter profiles page
            out.println("enter profiles page...");
            var profiles_btn_xpath =
                    "/Window/Pane/Pane/Pane[3]/Pane/Pane[1]/Pane/Pane[2]/Pane/Pane/Pane/Document/Group/Group[1]/List/ListItem[2]/Button";
            var profiles_btn = wait.until(d -> d.findElement(xpath(profiles_btn_xpath)));
            profiles_btn.click();

            // click cute profile
            out.println("click cute profile...");
            var cute_profile_btn_xpath =
                    "/Window/Pane/Pane/Pane[3]/Pane/Pane[1]/Pane/Pane[2]/Pane/Pane/Pane/Document/Group/Group/Group[3]/Group[12]";
            var cute_profile_btn = wait.until(d -> d.findElement(xpath(cute_profile_btn_xpath)));
            cute_profile_btn.click();

            // refresh cute profile
            out.println("refresh cute profile...");
            var cute_profile_refresh_btn_xpath =
                    "/Window/Pane/Pane/Pane[3]/Pane/Pane[1]/Pane/Pane[2]/Pane/Pane/Pane/Document/Group/Group/Group[3]/Group[12]/Button";
            var cute_profile_refresh_btn = wait.until(d -> d.findElement(xpath(cute_profile_refresh_btn_xpath)));
            cute_profile_refresh_btn.click();

            LockSupport.parkNanos(Duration.ofSeconds(1).toNanos());

            // minimize clash verge
            out.println("minimize clash verge...");
            var minimize_btn_xpath =
                    "/Window/Pane/Pane/Pane[3]/Pane/Pane[1]/Pane/Pane[2]/Pane/Pane/Pane/Document/Group/Group/Button[1]";
            var minimize_btn = wait.until(d -> d.findElement(xpath(minimize_btn_xpath)));
            minimize_btn.click();
        } finally {
            if (driver != null) {
                driver.quit();
            }
            if (appium_server_process.isAlive()) {
                out.println("force stop appium server...");
                appium_server_process.children().forEach(ProcessHandle::destroyForcibly);
                appium_server_process.destroyForcibly();
            }
        }
    }
}
