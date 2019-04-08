package keystone;

import keystone.entities.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeyDAO {
    private static final Logger logger = LogManager.getLogger(KeyDAO.class.getName());

    public static void saveItem(KeyItem item) {
        logItem(item);
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
            checkItemSpecs(item, session);
            session.persist(item);
            saveCars(item, session);
            List<ItemCar> itemCars = getItemCars(item, session);
            //next method can be called, only after itemCars list is got.
            saveItemCars(itemCars, session);
            transaction.commit();
            session.close();
            logger.debug("Item saved finally " + item);
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

   /* private static void saveItemCars(List<ItemCar> itemCars, Session session) {
        logger.debug("Saving itemCars");
        itemCars.forEach(session::persist);
        logger.debug("ItemCars saved");
    }*/

    private static void saveItemCars(List<ItemCar> itemCars, Session session) {
        itemCars.forEach(itemCar -> {
            List<ItemCarAttribute> attributes = itemCar.getAttributes();
            if (attributes!=null&&attributes.size()>0){
                List<ItemCarAttribute> finalAttributes = new ArrayList<>();
                attributes.forEach(attribute->{
                    attribute = checkItemCarAttributeExistence(attribute, session);
                    finalAttributes.add(attribute);
                });
                itemCar.setAttributes(finalAttributes);
            }
            session.persist(itemCar);
        });
    }

    private static List<ItemCar> getItemCars(KeyItem item, Session session) {
        logger.debug("Saving cars for item " + item);
        List<Car> cars = item.getCars();
        List<ItemCar> result = new ArrayList<>();
        cars.forEach(car -> {
            ItemCar itemCar = new ItemCar();
            itemCar.setItem(item);
            itemCar.setCar(car);
            itemCar.setAttributes(car.getFitAttributes());
            result.add(itemCar);
        });
        logger.debug("Cars saved for item " + item);
        return result;
    }

    private static void saveCars(KeyItem item, Session session) {
        List<Car> cars = item.getCars();
        List<Car> finalCars = new ArrayList<>();
        cars.forEach(car->{
            List<CarAttribute> attributes = car.getAttributes();
            List<CarAttribute> finalAttributes = new ArrayList<>();
            if (attributes!=null&& attributes.size()!=0){
                attributes.forEach(attribute->{
                    attribute = checkCarAttributeExistence(attribute, session);
                    finalAttributes.add(attribute);
                });
                car.setAttributes(finalAttributes);
            }
            Car dbCar = getExistingCar(car, session);
            if (dbCar!=null){
                dbCar.getAttributes().addAll(finalAttributes);
                session.update(dbCar);
                finalCars.add(dbCar);
            }
            else {
                session.persist(car);
                finalCars.add(car);
            }

        });
        item.setCars(finalCars);
    }

    private static Car getExistingCar(Car car, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Car> crQ = builder.createQuery(Car.class);
        Root<Car> root = crQ.from(Car.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("year"), car.getYear()));
        predicates.add(builder.equal(root.get("make"), car.getMake()));
        predicates.add(builder.equal(root.get("model"), car.getModel()));
        predicates.add(builder.equal(root.get("startFinish"), car.getStartFinish()));
        predicates.add(builder.equal(root.get("attString"), car.getAttString()));
        Predicate[] preds = predicates.toArray(new Predicate[0]);
        crQ.where(builder.and(preds));
        Query q = session.createQuery(crQ);
        Car testCar = null;
        try {
            testCar = (Car) q.getSingleResult();
            logger.debug("car exists " + testCar);
        } catch (NoResultException e) {
            logger.debug("car doesn't exist " + car);
        }

        return testCar;
    }

    private static void checkItemSpecs(KeyItem item, Session session) {
        List<Specification> specs = item.getSpecs();
        if (specs!=null&&specs.size()>0){
            List<Specification> finalSpecs = new ArrayList<>();
            for (Specification tmpSpec : specs) {
                tmpSpec = checkSpecExistence(tmpSpec, session);
                finalSpecs.add(tmpSpec);
            }
            item.setSpecs(finalSpecs);
        }
    }

    private static void logItem(KeyItem item) {
        logger.debug("Saving item: " + item);
        item.getSpecs().forEach(logger::debug);
        logger.debug("Cars:");
        item.getCars().forEach(car -> {
            logger.debug(car);
            car.getAttributes().forEach(logger::debug);
            car.getFitAttributes().forEach(logger::debug);
        });
    }

    private static Specification checkSpecExistence(Specification spec, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Specification> crQ = builder.createQuery(Specification.class);
        Root<Specification> root = crQ.from(Specification.class);
        crQ.where(builder.and(builder.equal(root.get("specName"), spec.getSpecName()),
                builder.equal(root.get("specValue"), spec.getSpecValue())));
        Query q = session.createQuery(crQ);
        Specification testSpec = null;
        try {
            testSpec = (Specification) q.getSingleResult();
            logger.debug("spec exists " + testSpec);
        } catch (NoResultException e) {
            logger.debug("combo doesn't exists for " + spec);
        }
        if (testSpec==null){
            return spec;
        }

        return testSpec;
    }

    private static CarAttribute checkCarAttributeExistence(CarAttribute attribute, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<CarAttribute> crQ = builder.createQuery(CarAttribute.class);
        Root<CarAttribute> root = crQ.from(CarAttribute.class);
        crQ.where(builder.equal(root.get("attValue"), attribute.getAttValue()));
        Query q = session.createQuery(crQ);
        CarAttribute testAtt = null;
        try {
            testAtt = (CarAttribute) q.getSingleResult();
            logger.debug("attribute exists " + attribute);
        } catch (NoResultException e) {
            logger.debug("attribute doesn't exist " + attribute);
        }
        if (testAtt==null){
            return attribute;
        }

        return testAtt;
    }

    private static ItemCarAttribute checkItemCarAttributeExistence(ItemCarAttribute attribute, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ItemCarAttribute> crQ = builder.createQuery(ItemCarAttribute.class);
        Root<ItemCarAttribute> root = crQ.from(ItemCarAttribute.class);
        crQ.where(builder.and(builder.equal(root.get("attName"), attribute.getAttName()),
                builder.equal(root.get("attValue"), attribute.getAttValue())));
        Query q = session.createQuery(crQ);
        ItemCarAttribute testAtt = null;
        try {
            testAtt = (ItemCarAttribute) q.getSingleResult();
            logger.debug("attribute exists " + attribute);
        } catch (NoResultException e) {
            logger.debug("attribute doesn't exist " + attribute);
        }
        if (testAtt==null){
            return attribute;
        }

        return testAtt;
    }


    public static Set<String> getParsedItems() {
        Session session = HibernateUtil.getSession();
        List<String> parsedItemsList = new ArrayList<>();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<String> crQ = builder.createQuery(String.class);
        Root<KeyItem> root = crQ.from(KeyItem.class);
        crQ.select(root.get("partNo"));
        Query q = session.createQuery(crQ);
        parsedItemsList = q.getResultList();
        session.close();
        Set<String> parsedItemSet = new HashSet<>(parsedItemsList);

        return parsedItemSet;
    }
}
