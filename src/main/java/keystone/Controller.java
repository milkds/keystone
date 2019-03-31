package keystone;

import keystone.entities.KeyItem;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class Controller {

    public static void main(String[] args) {
       // new Controller().getItemsFromFile(50);
        TestClass.testItemBuild();
    }

    public void getItemsFromFile(int maxItemsToParsePerDriver){
        List<String> itemPartsToParse = new JobDispatcher().getNewItems();
        int parsedItemsCounter = 0;
        WebDriver driver = SileniumUtil.initDriver();
        for (String itemLink : itemPartsToParse) {
            new Controller().parseItem(driver, itemLink);
            //checking if driver worked enough for reboot
            parsedItemsCounter++;
            if (parsedItemsCounter==maxItemsToParsePerDriver) {
                driver.close();
                driver = SileniumUtil.initDriver();
            }
        }

        driver.quit();
        HibernateUtil.shutdown();
    }

    public void parseItem(WebDriver driver, String itemLink){
        SileniumUtil.openItemPage(driver, itemLink);
        KeyItem item = new ItemBuilder().buildItem(driver);
        KeyDAO.saveItem(item);
    }
}
