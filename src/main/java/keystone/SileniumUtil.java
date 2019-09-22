package keystone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Time;
import java.time.Duration;
import java.util.Properties;
import java.util.Set;

public class SileniumUtil {

    private static final String BASE_LOGIN_URL = "https://wwwsc.ekeystone.com/login?Logout=true&RedirectURL=/";
    private static final String BASE_URL = "https://wwwsc.ekeystone.com/";
    private static final Logger logger = LogManager.getLogger(SileniumUtil.class.getName());

    public static WebDriver initDriver(){
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        while (true){
            try {
                driver.get(BASE_LOGIN_URL);
                break;
            }
            catch (TimeoutException ignored){
            }
        }
        String id = "webcontent_0_txtUserName";
        By testBy = By.id(id);
        while (true){
           try {
               waitForElementLocatedBy(driver, testBy);
               break;
           }
           catch (TimeoutException e){
              if (hasConnection(BASE_LOGIN_URL)){
                  logger.error("couldn't open login page");
                  driver.quit();
                  System.exit(1);
              }
           }
        }
        login(driver);

        return driver;
    }

    //returns true, if item page opened successfully.
    public static boolean openItemPage(WebDriver driver, String url) throws IOException {
     /*  while (true){
           try{
               driver.get(url);
               break;
           }
           catch (TimeoutException|NoSuchWindowException e){
               logger.error("failed to start opening url " + url);
               reboot(driver, url);
           }
       }*/
        logger.debug("opening page for " + url);
        while (true){
            driver.get(url);
            try {
                new FluentWait<>(driver)
                        .withTimeout(Duration.ofSeconds(60))
                        .pollingEvery(Duration.ofMillis(100))
                        .ignoring(WebDriverException.class)
                        .until(ExpectedConditions.urlContains(url));
                break;
            }
            catch (TimeoutException e){
                if (hasConnection(BASE_LOGIN_URL)){
                    logger.error("couldn't open item page " + url);
                    driver.close();
                    driver = SileniumUtil.initDriver();
                }
            }
        }
        logger.debug("waiting for title element for " + url);
        while (true){
            try {
                waitForElementLocatedBy(driver, By.id("webcontent_0_row2_0_productDetailHeader_lblTitle"));
                return true;
            }
            catch (TimeoutException e){
                if (hasConnection(BASE_LOGIN_URL)){
                    logger.error("Couldn't open page " + url +". Couldn't find title element");
                    return false;
                }
            }
        }
    }

    public static void reboot(WebDriver driver, String url) throws IOException {
        Set<Cookie> cookies = null;
       try {
           cookies = driver.manage().getCookies();
       }
       catch (TimeoutException e){
           logger.error("Timeout exception, while getting cookies from driver");
           throw new IOException();
       }
        driver.close();
        driver = new ItemOpener(cookies).openItemPage(url);
    }

    private static void login(WebDriver driver) {
        //this is 100% present, as checked in init method.
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream("src\\main\\resources\\app.properties"));
        } catch (IOException ignored) {
        }
        WebElement loginEl = getElementLocatedBy(driver, By.id("webcontent_0_txtUserName"));

        loginEl.sendKeys(appProps.getProperty("login"));

        //sending password
        WebElement passEl = null;
        while (true){
            try {
                passEl = getElementLocatedBy(driver, By.id("webcontent_0_txtPassword"));
                break;
            }
            catch (TimeoutException e){
                if (hasConnection(BASE_LOGIN_URL)){
                    logger.error("Couldn't find password element");
                    driver.quit();
                    System.exit(1);
                }
            }
        }
        passEl.sendKeys(appProps.getProperty("pass"));

        //clicking login button
        WebElement logBtnEl = null;
        By logBtnBy = By.id("webcontent_0_submit");
        while (true){
            try {
                logBtnEl = new FluentWait<>(driver)
                        .withTimeout(Duration.ofSeconds(120))
                        .pollingEvery(Duration.ofMillis(2))
                        .ignoring(WebDriverException.class)
                        .until(ExpectedConditions.elementToBeClickable(logBtnBy));
                break;
            }
            catch (TimeoutException e){
                if (hasConnection(BASE_LOGIN_URL)){
                    logger.error("Login button is not clickable");
                    driver.quit();
                    System.exit(1);
                }
            }
        }
        logBtnEl.click();
        System.out.println("login clicked");

        //waiting for logged in page
        while (true){
            try {
                new FluentWait<>(driver)
                        .withTimeout(Duration.ofSeconds(600))
                        .pollingEvery(Duration.ofMillis(50))
                        .ignoring(WebDriverException.class)
                        //.until(ExpectedConditions.presenceOfElementLocated(By.id("cartCheckoutContainer")));
                        //.until(ExpectedConditions.presenceOfElementLocated(By.id("siteheadercontent_0_customerName")));
                        //.until(ExpectedConditions.urlMatches("https://wwwsc.ekeystone.com"));
                        .until(ExpectedConditions.and(
                                ExpectedConditions.urlMatches("https://wwwsc.ekeystone.com"),
                                ExpectedConditions.presenceOfElementLocated(By.id("divFlowPlayerScript"))
                                ));
              /*  new WebDriverWait(driver, 60).until(
                        webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));*/
              Thread.sleep(10*1000);
                break;
            }
            catch (TimeoutException e){
                if (hasConnection(BASE_LOGIN_URL)){
                    logger.error("Couldn't load main page");
                    driver.quit();
                    System.exit(1);
                }
            } catch (InterruptedException ignored) {
            }
        }

    }

    public static void waitForElementLocatedBy(WebDriver driver, By by){
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(60))
                .pollingEvery(Duration.ofMillis(2))
                .ignoring(WebDriverException.class)
                .until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public static WebElement getElementLocatedBy(WebDriver driver, By by){
        return  new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(60))
                .pollingEvery(Duration.ofMillis(2))
                .ignoring(WebDriverException.class)
                .until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public static boolean hasConnection(String url) {
        URL testUrl= null;
        try {
            testUrl = new URL(url);
            URLConnection con=testUrl.openConnection();
            con.getInputStream();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static WebDriver reboot() {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        while (true){
            try{
                driver.get(BASE_URL);
                break;
            }
            catch (TimeoutException e){
                logger.error("failed to start opening url " + BASE_URL);
            }
        }
        while (true){
            try {
                new FluentWait<>(driver)
                        .withTimeout(Duration.ofSeconds(120))
                        .pollingEvery(Duration.ofMillis(2))
                        .ignoring(WebDriverException.class)
                        .until(ExpectedConditions.presenceOfElementLocated(By.id("cartCheckoutContainer")));
                logger.debug("driver rebooted.");
                break;
            }
            catch (TimeoutException e){
                if (hasConnection(BASE_URL)){
                    logger.error("Couldn't load main page");
                    driver.quit();
                    System.exit(1);
                }
            }
        }

        return driver;
    }
}
