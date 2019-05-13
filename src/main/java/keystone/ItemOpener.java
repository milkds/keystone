package keystone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Set;

public class ItemOpener {
    private Set<Cookie> cookies;
    private static final String TEST_URL = "https://www.example.com/";
    private static final Logger logger = LogManager.getLogger(SileniumUtil.class.getName());

    public ItemOpener(Set<Cookie> cookies) {
        this.cookies = cookies;
    }

    WebDriver openItemPage(String itemLink) {
       WebDriver driver = prepareToOpenTargetURL();
        while (true){
            try {
                driver.get(itemLink);
                break;
            }
            catch (TimeoutException e){
                logger.debug("couldn't open url "+itemLink+" rebooting driver");
                driver.close();
                driver = prepareToOpenTargetURL();
            }
        }
        logger.debug("opened url " + itemLink);

        return driver;
    }

    private WebDriver prepareToOpenTargetURL() {
        WebDriver driver = openTestURL();
        cookies.forEach(cookie->{
            driver.manage().addCookie(cookie);
        });
        return driver;
    }

    /***
     * This method needed to open base url - as before this, we cannot assign cookies to blank driver object
     * @return driver with opened test page
     */
    private WebDriver openTestURL() {
        WebDriver driver = new ChromeDriver();

        int attempts = 0;
        while (true){
            try {
                driver.get(TEST_URL);
                break;
            }
            catch (TimeoutException e){
                attempts++;
                if (attempts==5){
                    driver.close();
                    driver = new ChromeDriver();
                    attempts=0;
                }
            }
        }
        logger.debug("test page opened successfully");

        return driver;
    }

    public Set getCookies() {
        return cookies;
    }
    public void setCookies(Set cookies) {
        this.cookies = cookies;
    }
}
