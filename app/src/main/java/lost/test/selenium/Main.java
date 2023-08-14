package lost.test.selenium;

import static java.lang.System.err;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.locks.LockSupport.parkNanos;
import static org.openqa.selenium.By.*;

import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.options.WindowsOptions;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;

public class Main {

    public static void main(String[] args) {

        // get subscription
        var subscription = getSubscription();

        if (subscription == null) return;

        // update clash profile
        updateClashProfile(subscription);

        // restart clash
        restartClash();
    }

    @Nullable
    static String getSubscription() {
        String subscription;

        var driver = createDriver();

        var wait = new FluentWait<WebDriver>(driver)
                .withTimeout(ofSeconds(30))
                .pollingEvery(ofSeconds(1))
                .ignoring(NoSuchElementException.class);

        var register_page_uri = "https://www.cutestcloud.com/#/register";
        var dashboard_page_uri = "https://www.cutestcloud.com/#/dashboard";

        try {
            driver.get(register_page_uri);
            var title = driver.getTitle();
            err.println("title = " + title);

            // complete register form
            completeForm(wait);

            var current_dashboard_page = false;

            while (!current_dashboard_page) {
                err.println("register...");

                driver.switchTo().defaultContent();

                // find register button and click
                var register_btn_xpath = "//*[@id=\"root\"]/div[2]/div[2]/div[2]/button";
                var register_btn = wait.until(d -> d.findElement(xpath(register_btn_xpath)));
                err.println("register_btn = " + register_btn);
                register_btn.click();

                try {
                    current_dashboard_page = new FluentWait<WebDriver>(driver)
                            .withTimeout(ofSeconds(5))
                            .pollingEvery(ofSeconds(1))
                            .until(d -> d.getCurrentUrl().equals(dashboard_page_uri));
                } catch (TimeoutException ignored) {
                }

                if (current_dashboard_page) break;

                captcha(wait, driver);

                try {
                    current_dashboard_page = wait.until(d -> d.getCurrentUrl().equals(dashboard_page_uri));
                } catch (TimeoutException ignore) {
                }
            }

            // get subscription on dashboard page
            getSubscriptionOnDashboardPage(driver, wait);

            // get subscription from clipboard
            subscription = getClipboardContent();
            err.println("subscription = " + subscription);
        } finally {
            driver.quit();
        }

        return subscription;
    }

    static void captcha(FluentWait<WebDriver> wait, ChromeDriver driver) {
        err.println("captcha...");

        // find captcha dialog iframe and enter
        var captcha_dialog_class = "ant-modal";
        var captcha_dialog = wait.until(d -> d.findElement(className(captcha_dialog_class)));
        err.println("captcha_dialog = " + captcha_dialog);
        var captcha_dialog_iframe = wait.until(d -> d.findElement(tagName("iframe")));
        err.println("captcha_dialog_iframe = " + captcha_dialog_iframe);

        driver.switchTo().frame(captcha_dialog_iframe);

        // find captcha check box and click it
        var captcha_checkbox_id = "recaptcha-anchor";
        var captcha_checkbox = wait.until(d -> d.findElement(id(captcha_checkbox_id)));
        err.println("captcha_checkbox = " + captcha_checkbox);
        captcha_checkbox.click();

        driver.switchTo().defaultContent();

        // find captcha challenge iframe and enter
        var captcha_challenge_iframe_xpath = "/html/body/div[3]/div[4]/iframe";
        var captcha_challenge_iframe = wait.until(d -> d.findElement(xpath(captcha_challenge_iframe_xpath)));
        err.println("captcha_challenge_iframe = " + captcha_challenge_iframe);

        driver.switchTo().frame(captcha_challenge_iframe);

        // find audio button and click it
        var audio_btn_class = "rc-button-audio";
        var audio_btn = wait.until(d -> d.findElement(className(audio_btn_class)));
        audio_btn.click();

        parkNanos(ofSeconds(1).toNanos());

        // find buster and click it
        var buster_btn_class = "help-button-holder";
        var buster_btn = wait.until(d -> d.findElement(className(buster_btn_class)));
        err.println("buster_btn = " + buster_btn);
        buster_btn.click();
    }

    static void getSubscriptionOnDashboardPage(ChromeDriver driver, FluentWait<WebDriver> wait) {
        err.println("enter dashboard page...");
        driver.switchTo().defaultContent();

        // open subscription dialog
        err.println("open subscription dialog...");
        var open_subscription_dialog_btn_xpath =
                "//*[@id=\"root\"]/div[2]/div[3]/div[2]/div/div[2]/div[1]/div/div[3]/button";
        var open_subscription_dialog_btn = wait.until(d -> d.findElement(xpath(open_subscription_dialog_btn_xpath)));
        open_subscription_dialog_btn.click();

        // find subscription dialog
        var subscription_dialog_classname = "ant-modal";
        var subscription_dialog = wait.until(d -> d.findElement(className(subscription_dialog_classname)));

        // copy subscription
        err.println("copy subscription...");
        var copy_subscription_btn =
                subscription_dialog.findElement(className("ant-modal-body")).findElement(xpath("div[1]"));
        err.println("copy_subscription_btn = " + copy_subscription_btn);
        copy_subscription_btn.click();
    }

    @NotNull
    static ChromeDriver createDriver() {
        String buster_ctx = null;
        try (var in = Main.class.getClassLoader().getResourceAsStream("buster.crx")) {
            buster_ctx = Base64.getEncoder().encodeToString(in.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var driverOption = new ChromeOptions()
                .setBinary("E:\\scoop\\global\\shims\\brave-beta.exe")
                //            .addArguments("--headless=new")
                //            .addArguments("--incognito") // private mode for brave
                .addEncodedExtensions(buster_ctx);

        var proxy = new Proxy().setSocksProxy("localhost:55556").setSocksVersion(5);
        driverOption.setProxy(proxy);

        var driver = new ChromeDriver(driverOption);
        return driver;
    }

    static void completeForm(FluentWait<WebDriver> wait) {
        // email input
        err.println("input email...");
        var email_input_xpath = "//*[@id=\"root\"]/div[2]/div[2]/div[2]/input[1]";
        var email_input = wait.until(d -> d.findElement(xpath(email_input_xpath)));
        err.println("email_input = " + email_input);
        email_input.clear();
        email_input.sendKeys(randomEmail());

        // password input
        err.println("input password...");
        var passwd = randomPasswd();
        var passwd_input_xpath = "//*[@id=\"root\"]/div[2]/div[2]/div[2]/span[1]/input";
        var passwd_input = wait.until(d -> d.findElement(xpath(passwd_input_xpath)));
        err.println("passwd_input = " + passwd_input);
        passwd_input.clear();
        passwd_input.sendKeys(passwd);

        // password resubmit input
        err.println("input password again...");
        var passwd_resubmit_input_xpath = "//*[@id=\"root\"]/div[2]/div[2]/div[2]/span[2]/input";
        var passwd_resubmit_input = wait.until(d -> d.findElement(xpath(passwd_resubmit_input_xpath)));
        err.println("passwd_resubmit_input = " + passwd_resubmit_input);
        passwd_resubmit_input.clear();
        passwd_resubmit_input.sendKeys(passwd);
    }

    static String randomEmail() {
        return UUID.randomUUID().toString().replace("-", "") + "@end.tw";
    }

    static String randomPasswd() {
        return "ringbuffer";
    }

    static String getClipboardContent() {
        err.println("get subscription from clipboard...");
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
        err.println("update clash profile...");
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
        err.println("restart clash...");
        ProcessHandle.allProcesses()
                .filter(p -> p.info()
                        .command()
                        .map(n -> n.contains("Clash Verge.exe"))
                        .orElse(false))
                .forEach(ProcessHandle::destroyForcibly);

        startAndRefreshClash();
    }

    static void startAndRefreshClash() {
        err.println("start appium server...");
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

        URL appium_server_addr = null;
        try {
            appium_server_addr = URI.create("http://127.0.0.1:4723/").toURL();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // test appium server ok?
        while (true) {
            err.println("test appium server ok?...");
            try (var test_socket = new Socket()) {
                test_socket.connect(
                        new InetSocketAddress(appium_server_addr.getHost(), appium_server_addr.getPort()), 3000);
                break;
            } catch (Exception ignore) {
            }
            parkNanos(ofSeconds(1).toNanos());
        }

        parkNanos(ofSeconds(1).toNanos());

        WindowsDriver driver = null;
        try {
            var windowsOptions = new WindowsOptions().setApp("C:\\Users\\zzz\\Desktop\\clash-verge\\Clash Verge.exe");

            // start clash verge
            err.println("start clash verge and connect to appium server...");
            driver = new WindowsDriver(appium_server_addr, windowsOptions);

            var wait = new FluentWait<>(driver)
                    .withTimeout(ofSeconds(5))
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(NoSuchElementException.class);

            // enter profiles page
            err.println("enter profiles page...");
            var profiles_btn_xpath =
                    "/Window/Pane/Pane/Pane[3]/Pane/Pane[1]/Pane/Pane[2]/Pane/Pane/Pane/Document/Group/Group[1]/List/ListItem[2]/Button";
            var profiles_btn = wait.until(d -> d.findElement(xpath(profiles_btn_xpath)));
            profiles_btn.click();

            // select cute profile
            err.println("select cute profile...");
            var profiles_list_pane_xpath =
                    "/Window/Pane/Pane/Pane[3]/Pane/Pane[1]/Pane/Pane[2]/Pane/Pane/Pane/Document/Group/Group/Group[3]";
            var profiles_list_pane = wait.until(d -> d.findElement(xpath(profiles_list_pane_xpath)));
            WebElement cute_profile_pane = null;
            for (WebElement group : profiles_list_pane.findElements(tagName("Group"))) {
                try {
                    var text = group.findElement(tagName("Text"));
                    if ("cute".equals(text.getAttribute("Name"))) {
                        cute_profile_pane = group;
                    }
                } catch (Exception ignore) {
                }
            }
            if (cute_profile_pane == null) {
                err.println("can not find cute profile");
                return;
            }
            cute_profile_pane.click();

            // refresh cute profile
            err.println("refresh cute profile...");
            var cute_profile_refresh_btn = cute_profile_pane.findElement(tagName("Button"));
            cute_profile_refresh_btn.click();

            parkNanos(ofSeconds(1).toNanos());

            // minimize clash verge
            err.println("minimize clash verge...");
            var minimize_btn_xpath =
                    "/Window/Pane/Pane/Pane[3]/Pane/Pane[1]/Pane/Pane[2]/Pane/Pane/Pane/Document/Group/Group/Button[1]";
            var minimize_btn = wait.until(d -> d.findElement(xpath(minimize_btn_xpath)));
            minimize_btn.click();
        } finally {
            if (driver != null) {
                driver.quit();
            }
            if (appium_server_process.isAlive()) {
                err.println("force stop appium server...");
                appium_server_process.children().forEach(ProcessHandle::destroyForcibly);
                appium_server_process.destroyForcibly();
            }
        }
    }
}
