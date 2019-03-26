package keystone;

import keystone.entities.Car;
import keystone.entities.CarAttribute;
import keystone.entities.KeyItem;
import keystone.entities.Specification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openqa.selenium.WebDriver;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class TestClass {
    private static final Logger logger = LogManager.getLogger(TestClass.class.getName());

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
        String url = "https://wwwsc.ekeystone.com/Search/Detail?pid=SKYH7006";
        WebDriver driver = SileniumUtil.initDriver();
        SileniumUtil.openItemPage(driver, url);
        new ItemBuilder().buildItem(driver);
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
