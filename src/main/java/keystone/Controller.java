package keystone;

import keystone.entities.KeyItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Controller {
    private static final Logger logger = LogManager.getLogger(Controller.class.getName());

    public static void main(String[] args) {
      //  new Controller().getItemsFromFile(200);
       // TestClass.testTwoWindows();
        new Controller().parseItemsFromFile();
    }


    public void parseItemsFromFile(){
        //list of item urls
        Set<String> itemPartsToParse = new JobDispatcher().getNewItems();
        WebDriver driver = SileniumUtil.initDriver();
        Set<Cookie> cookies = driver.manage().getCookies();
        int totalItems = itemPartsToParse.size();
        int currentItem = 1;
        for (String itemLink : itemPartsToParse) {
            WebDriver itemDriver = new ItemOpener(cookies).openItemPage(itemLink);
            KeyItem item = null;
          while (true){
              try {
                  item = new ItemBuilder().buildItem(itemDriver);
                  break;
                 } catch (IOException e) {
                  itemDriver.close();
                  itemDriver = new ItemOpener(cookies).openItemPage(itemLink);
              }
          }
            KeyDAO.saveItem(item);
            itemDriver.close();
            logger.info("Parsed item "+ currentItem + " of total " + totalItems);
            currentItem++;
        }

    }

    public void getItemsFromFile(int maxItemsToParsePerDriver) throws IOException {
        Set<String> itemPartsToParse = new JobDispatcher().getNewItems();
        int parsedItemsCounter = 0;
        WebDriver driver = SileniumUtil.initDriver();
        for (String itemLink : itemPartsToParse) {
           while (true){
               try {
                   new Controller().parseItem(driver, itemLink);
                   break;
               }
               catch (TimeoutException e){
                  // driver.close();
                    SileniumUtil.reboot(driver, itemLink);
               }
           }
            //checking if driver worked enough for reboot
            parsedItemsCounter++;
            if (parsedItemsCounter==maxItemsToParsePerDriver) {
                driver.close();
                driver = SileniumUtil.initDriver();
                try {
                    Thread.sleep(5*60*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        driver.quit();
        HibernateUtil.shutdown();
    }

    public void parseItem(WebDriver driver, String itemLink) throws IOException {
        SileniumUtil.openItemPage(driver, itemLink);
        KeyItem item = new ItemBuilder().buildItem(driver);
        KeyDAO.saveItem(item);
    }
}
