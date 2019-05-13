package keystone;

import keystone.entities.Car;
import keystone.entities.CarAttribute;
import keystone.entities.KeyItem;
import keystone.entities.Specification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestClass {
    private static final Logger logger = LogManager.getLogger(TestClass.class.getName());


    public static void testTwoWindows(){
        WebDriver baseDriver = SileniumUtil.initDriver();
        Set<Cookie> allCookies = baseDriver.manage().getCookies();
        System.out.println(allCookies.size());
        WebDriver newDriver = new ChromeDriver();
        newDriver.get("https://www.example.com/");
        for(Cookie cookie : allCookies) {
            newDriver.manage().addCookie(cookie);
        }
        newDriver.get("https://wwwsc.ekeystone.com/Search/Detail?pid=BLS47-244566");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        newDriver.close();
        baseDriver.quit();
    }

    public static void testLogin(){
        WebDriver driver = SileniumUtil.initDriver();
        System.out.println("Logged in");
        driver.get("https://wwwsc.ekeystone.com/Search/Detail?pid=BLS47-244566");
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.quit();
    }

    public static void testItemBuild(){
       // String url = "https://wwwsc.ekeystone.com/Search/Detail?pid=BLS24-253208";
       // String url = "https://wwwsc.ekeystone.com/Search/Detail?pid=SKYH7006";
        String url = "https://wwwsc.ekeystone.com/Search/Detail?pid=BLS35-197263";
        WebDriver driver = SileniumUtil.initDriver();
        try {
            SileniumUtil.openItemPage(driver, url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            new ItemBuilder().buildItem(driver);
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver.quit();
    }

    public static void testCarSaveWithAtts(){
        Car car = new Car();

        car.setYear(2018);
        car.setMake("FORD");
        car.setModel("F-150");
        car.setAttString("PLATINUM 4WD");

        CarAttribute carAtt = new CarAttribute();
        CarAttribute carAtt1 = new CarAttribute();

        carAtt.setAttValue("PLATINUM");
        carAtt1.setAttValue("4WD");

        car.getAttributes().add(carAtt);
        car.getAttributes().add(carAtt1);

        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
            session.persist(car);
            transaction.commit();
            session.close();
            logger.info("Car saved");
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }

        HibernateUtil.shutdown();
    }
    public static void testItemSaveWithSpecs(){
        KeyItem item = new KeyItem();

        item.setMake("BILSTEIN");
        item.setPartNo("TEST_PART_NO");
        item.setFeatures("TEST FEATURES");
        item.setDescription("TEST DESCRIPTION");
        item.setShortDescription("TEST SHORT DESCRIPTION");
        item.setImgLinks("www.test.img/link/img.png");

        Specification spec = new Specification();
        Specification spec1 = new Specification();

        spec.setSpecName("Length");
        spec.setSpecValue("100");

        spec1.setSpecName("Color");
        spec1.setSpecValue("Black");

        item.getSpecs().add(spec);
        item.getSpecs().add(spec1);

        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
           /* List<Specification> specs = item.getSpecs();
            if (specs!=null&&specs.size()>0){
                List<Specification> finalSpecs = new ArrayList<>();
                for (Specification tmpSpec : specs) {
                    tmpSpec = checkSpecExistence(tmpSpec, session);
                    finalSpecs.add(tmpSpec);
                    *//*logger.debug("Before check" + tmpSpec);
                    tmpSpec = checkSpecExistence(tmpSpec, session);
                    logger.debug("After check" +tmpSpec);
                    tmpSpecs.add(tmpSpec);*//*
                    *//*tmpSpec.getItems().add(item);
                    if (tmpSpec.getSpecID()==0){
                        session.persist(tmpSpec);
                        logger.debug("spec saved " + tmpSpec);
                    }
                    else {
                        session.update(tmpSpec);
                        logger.debug("spec updated " + tmpSpec);
                    }*//*

                }
                item.setSpecs(finalSpecs);
            }*/
            session.persist(item);
            transaction.commit();
            session.close();
            logger.info("Item saved");
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }

        HibernateUtil.shutdown();
    }
}
