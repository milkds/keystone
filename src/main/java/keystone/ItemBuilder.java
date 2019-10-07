package keystone;

import keystone.entities.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ItemBuilder {

    private static final Logger logger = LogManager.getLogger(ItemBuilder.class.getName());

    public KeyItem buildItem(WebDriver driver) throws IOException {
        KeyItem item = new KeyItem();
        getMake(item, driver);
        getPartNo(item, driver);
        getFeatures(item, driver);
        getSpecs(item, driver);
        getShortDesc(item, driver);
        getDesc(item, driver);
        getImgLinks(item, driver);
        getHtmlDesc(item, driver);
        getPrices(item, driver);
        getDocs(item, driver);
        getFits(item, driver);
        getKitElements(item, driver);


        logger.debug("Item built: " + item);

        return item;
    }

    private void getKitElements(KeyItem item, WebDriver driver) {
        WebElement kitTabEl = null;
        try {
            kitTabEl = driver.findElement(By.id("kitComponentsTab"));
        }
        catch (NoSuchElementException e){
            item.setKitElements("No Kit Elements");
            return;
        }
        kitTabEl.click();
        WebElement kitElementsListEl = null;
        try {
            kitElementsListEl = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(120))
                    .pollingEvery(Duration.ofMillis(2))
                    .ignoring(WebDriverException.class)
                    .until(ExpectedConditions.visibilityOfElementLocated(By.id("webcontent_0_row2_0_productDetailTabs_upKitComponentsTab")));
        }
        catch (TimeoutException e){
            logger.error("Kit elements list is not available at " + driver.getCurrentUrl());
            return;
        }
        List<WebElement> kitElementsEls = kitElementsListEl.findElements(By.cssSelector("div[id^='webcontent_0_row2_0_productDetailTabs_kitComponentsResultList_rptrResultsRows_divResult_']"));
        if (kitElementsEls.size()==0){
            logger.error("No kit elements available in kit elements list at " + driver.getCurrentUrl());
            return;
        }
        StringBuilder elementBuilder = new StringBuilder();
        elementBuilder.append("---");
        elementBuilder.append(System.lineSeparator());
        elementBuilder.append(System.lineSeparator());
        kitElementsEls.forEach(kitElementEl->{
            WebElement infoEl = kitElementEl.findElement(By.cssSelector("div[id^='webcontent_0_row2_0_productDetailTabs_kitComponentsResultList_rptrResultsRows_divResultContent_']"));
            WebElement headerEl = infoEl.findElement(By.className("resultsContentHeader"));
            WebElement manufactEl = headerEl.findElement(By.cssSelector("span[id^='webcontent_0_row2_0_productDetailTabs_kitComponentsResultList_rptrResultsRows_lblManufacturerName_']"));
            WebElement partNoEl = headerEl.findElement(By.cssSelector("span[id^='webcontent_0_row2_0_productDetailTabs_kitComponentsResultList_rptrResultsRows_lblManufacturerNumber_']"));
            WebElement descEl = infoEl.findElement(By.className("resultsContentDes"));
            WebElement linkEl = descEl.findElement(By.className("descriptionLink"));
            logger.info(linkEl.getAttribute("innerHTML"));
            linkEl = linkEl.findElement(By.tagName("a"));
            WebElement picLinkEl = kitElementEl.findElement(By.tagName("img"));
            String picLink = picLinkEl.getAttribute("src");
            picLink = StringUtils.substringBefore(picLink, "&");

            elementBuilder.append(manufactEl.getText());
            elementBuilder.append(System.lineSeparator());
            elementBuilder.append(partNoEl.getText());
            elementBuilder.append(System.lineSeparator());
            elementBuilder.append(descEl.getText());
            elementBuilder.append(System.lineSeparator());
            elementBuilder.append(linkEl.getAttribute("href"));
            elementBuilder.append(System.lineSeparator());
            elementBuilder.append(picLink);
            elementBuilder.append(System.lineSeparator());
            elementBuilder.append(System.lineSeparator());
        });
        elementBuilder.append("---");
        logger.info(elementBuilder.toString());
        item.setKitElements(elementBuilder.toString());

    }

    private void getDocs(KeyItem item, WebDriver driver) {
        item.setDocLinks("");
        WebElement docEl = null;
        try {
            docEl = driver.findElement(By.id("webcontent_0_row2_0_divDocumentsTab"));
        }
        catch (NoSuchElementException e){
            logger.info("no docs tab at " + driver.getCurrentUrl());
            return;
        }
        docEl.click();
        WebElement docTabEl = null;
        try {
            docTabEl = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(120))
                    .pollingEvery(Duration.ofMillis(2))
                    .ignoring(WebDriverException.class)
                    .until(ExpectedConditions.visibilityOfElementLocated(By.id("documentsDiv")));
        }
        catch (TimeoutException e){
            logger.error("No doc Element at " + driver.getCurrentUrl());
            return;
        }
        List<WebElement> docEls = docTabEl.findElements(By.tagName("a"));
        if (docEls.size()==0){
            return;
        }
        StringBuilder docLinkBuilder = new StringBuilder();
        docEls.forEach(docEl1->{
            String link = docEl1.getAttribute("href");
            if (link!=null){
                docLinkBuilder.append(link);
                docLinkBuilder.append(System.lineSeparator());
            }
        });
        int linksLength = docLinkBuilder.length();
        if (linksLength>0){
            docLinkBuilder.setLength(linksLength-2);
        }
        item.setDocLinks(docLinkBuilder.toString());
        logger.debug("got links " + docLinkBuilder.toString());
    }

    private void getHtmlDesc(KeyItem item, WebDriver driver) {
        WebElement shortDescEl = null;
        try {
            shortDescEl = driver.findElement(By.id("webcontent_0_row2_0_productDetailHeader_lblDescription"));
        }
        catch (NoSuchElementException e){
            logger.info("No short Desc at " + driver.getCurrentUrl());
        }
        String shortDescStr = "";
        if (shortDescEl!=null){
            shortDescStr = shortDescEl.getText();
        }
        WebElement fullDescEl = null;
        try {
            fullDescEl = driver.findElement(By.id("webcontent_0_row2_0_productDetailTabs_upAdditionalInfoTab"));
        }
        catch (NoSuchElementException e){
            logger.info("No Full desc at " + driver.getCurrentUrl());
        }
        if (fullDescEl==null){
          return;
        }
        List<WebElement> divElements = fullDescEl.findElements(By.tagName("div"));
        if (divElements.size()==0){
            return;
        }
        fullDescEl = divElements.get(0);
        String fullDescStr = fullDescEl.getAttribute("innerHTML");

        StringBuilder descHtmlBuilder = new StringBuilder();
        if (shortDescStr.length()!=0){
            descHtmlBuilder.append(shortDescStr);
            descHtmlBuilder.append(System.lineSeparator());
            descHtmlBuilder.append(System.lineSeparator());
            descHtmlBuilder.append(System.lineSeparator());
        }
        descHtmlBuilder.append(fullDescStr);
        item.setHtmlDescription(descHtmlBuilder.toString());
        item.setPlainDesc(fullDescEl.getText());
        logger.debug("GOT DESCRIPTION:");
        logger.debug(descHtmlBuilder.toString());
        logger.info(item.getPlainDesc());

    }

    private void getPrices(KeyItem item, WebDriver driver) {
        openPriceTab(driver);
        BigDecimal myPrice = getPrice(driver, "webcontent_0_row2_0_productDetailBasicInfo_lblMyPrice");
        BigDecimal jobberPrice = getPrice(driver, "webcontent_0_row2_0_productDetailBasicInfo_lblJobberPrice");
        BigDecimal retailPrice = getPrice(driver, "webcontent_0_row2_0_productDetailBasicInfo_lblRetailPrice");

        item.setMyPrice(myPrice);
        item.setJobberPrice(jobberPrice);
        item.setRetailPrice(retailPrice);

    }

    private BigDecimal getPrice(WebDriver driver, String elementID) {
        WebElement priceEl = null;
        try {
            priceEl = driver.findElement(By.id(elementID));
        }
        catch (NoSuchElementException e){
            logger.error("No element with id " + elementID +  " at " + driver.getCurrentUrl());
            return new BigDecimal(0);
        }
        String priceStr = priceEl.getText();

        return processPriceString(priceStr);
    }


    private void openPriceTab(WebDriver driver) {
        logger.debug("getting prices");
        WebElement openPricesEl = null;
        try {
            openPricesEl = driver.findElement(By.className("checkoutHide"));
        }
        catch (NoSuchElementException e){
            logger.error("No Price element <class=checkoutHide> at " + driver.getCurrentUrl());
            return;
        }
        openPricesEl.click();
        WebElement myPriceEl = null;
        try {
            myPriceEl = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(120))
                    .pollingEvery(Duration.ofMillis(2))
                    .ignoring(WebDriverException.class)
                    .until(ExpectedConditions.visibilityOfElementLocated(By.id("webcontent_0_row2_0_productDetailBasicInfo_lblMyPrice")));
        }
        catch (TimeoutException e){
            logger.error("No My Price Element at " + driver.getCurrentUrl());
        }
    }

    private BigDecimal processPriceString(String priceString) {
        if (priceString==null||priceString.length()==0){
            logger.info("empty price string");
            return new BigDecimal(0);
        }
        priceString = priceString.replaceAll("\\$", "");
        priceString = priceString.replaceAll(",", "");
        priceString = priceString.replaceAll("USD", "");
        priceString = priceString.trim();

        BigDecimal result = null;
        try{
            result = new BigDecimal(priceString);
        }
        catch (NumberFormatException e){
            logger.info("invalid price string - " + priceString);
            result = new BigDecimal(0);
        }

        return result;
    }

    private void getFits(KeyItem item, WebDriver driver) throws IOException {
        logger.debug("opening fits");
        if (!fitsOpened(driver)){
            return;
        }
        List<Car> cars = getCars(driver);
        item.setCars(cars);
    }

    private List<Car> getCars(WebDriver driver) throws IOException {
        List<Car> result = new ArrayList<>();
        List<WebElement> carGroupElements = getCarGroupElements(driver);
        if (carGroupElements.size()==0){
            return result;
        }
        List<String> carGroupIDs = getCarGroupIDs(carGroupElements);
        carGroupIDs.forEach(logger::debug);
        AtomicReference<Integer> counter = new AtomicReference<>(0);
        for (String carGroupID : carGroupIDs) {
            List<Car> carsFromCarGroup = getCarsFromCarGroup(driver, carGroupID, counter.get());
            counter.getAndSet(counter.get() + 1);
            result.addAll(carsFromCarGroup);
        }


        return result;
    }

    private List<Car> getCarsFromCarGroup(WebDriver driver, String carGroupID, Integer counter) throws IOException {
        //Element 100% exists, cause we won't get here, if it doesn't.
        WebElement carGroupEl = driver.findElement(By.id(carGroupID));
        Car templateCar = new Car();
        String carString = carGroupEl.getAttribute("href");
        templateCar = getTemplateCar(carString);
        logger.debug("Template car: "+templateCar);
        if(!carPageOpened(driver, carGroupEl)){
            logger.error("Couldn't open page for " + carString);
            return new ArrayList<>();
        }

        WebElement carSectionEl = null;
        try {
            carSectionEl = driver.findElement(By.id("webcontent_0_row2_0_productDetailTabs_rptrApplicationSummary_divRepeater_"+counter));
        }
        catch (NoSuchElementException e){
            logger.error("No element found by id webcontent_0_row2_0_productDetailTabs_rptrApplicationSummary_divRepeater_"+counter);
            return new ArrayList<>();
        }
        logger.debug("got element by ID " + carGroupID);
        List<WebElement> carBlockEls = carSectionEl.findElements
                (By.cssSelector("div[id^='webcontent_0_row2_0_productDetailTabs_rptrApplicationSummary_rptrApplications_"+counter+"_divApplication_']"));
        if (carBlockEls.size()==0){
            logger.error("couldn't find car elements in car section for " + driver.getCurrentUrl());
            return new ArrayList<>();
        }
        List<Car> result = new ArrayList<>();
        for (WebElement carBlockEl : carBlockEls) {
            Car car = buildCar(templateCar, carBlockEl);
            if (car!=null){
                logger.debug("car built " + car);
                result.add(car);
            }
        }


        return result;
    }

    private Car buildCar(Car templateCar, WebElement carBlockEl) {
        Car result = new Car(templateCar);
        StringBuilder attStringBuilder = new StringBuilder();
        WebElement carStringEl = null;
        try {
            carStringEl = carBlockEl.findElement(By.tagName("a"));
        }
        catch (NoSuchElementException e){
            logger.error("Couldn't find <a> tag at car block element");
            return null;
        }
        List<WebElement> attributeElements = carStringEl.findElements(By.className("applicationAttributeValue"));
        if (attributeElements.size()==0){
            result.setAttString("");
        }
        else {
            logger.debug("Car attributes found");
            Set<CarAttribute> attributes = new HashSet<>();
            attributeElements.forEach(attEl->{
                String attValue = attEl.getText();
                if (attValue.endsWith(",")){
                    attValue  = StringUtils.chop(attValue);
                }
                CarAttribute attribute = new CarAttribute();
                attribute.setAttValue(attValue);
                attributes.add(attribute);
                attStringBuilder.append(attValue);
                attStringBuilder.append(" ");
            });
            String attString = attStringBuilder.toString().trim();
            result.setAttString(attString);
            result.setAttributes(attributes);
            attributes.forEach(logger::debug);
        }
        String yearStr = StringUtils.substring(carStringEl.getText(), 0, 4);
        try {
            int year = Integer.parseInt(yearStr);
            result.setYear(year);
        }
        catch (NumberFormatException e){
            logger.error("Couldn't properly extract year from yearStr " + yearStr);
            result.setYear(0);
        }
        List<ItemCarAttribute> iCarAtts = getItemCarAttributes(carBlockEl);
        logger.debug("Size of itemCar attribute list is " + iCarAtts.size());
        iCarAtts.forEach(logger::debug);
        result.setFitAttributes(iCarAtts);


        return result;
    }

    private List<ItemCarAttribute> getItemCarAttributes(WebElement carBlockEl) {
        List<ItemCarAttribute> result = new ArrayList<>();
        List<WebElement> attBlocks = carBlockEl.findElements(By.className("applicationBlock"));
        if (attBlocks.size()==0){
            logger.debug("Couldn't find fitment att element for " + carBlockEl.getText());
            return result;
        }

        attBlocks.forEach(attBlock->{
            //test section
            ////////////////////////////////
            List<WebElement> spanEls = attBlock.findElements(By.tagName("span"));
            if (spanEls.size()>3){
                logger.error("unexpected span element quantity at " + attBlock.getText());
            }
            spanEls.forEach(spanEl->{
                String className = spanEl.getAttribute("class");
                switch (className){
                    case "spanBullet": break;
                    case "applicationAttributeName": break;
                    case "applicationRequiredProducts": break;
                    default: logger.error("Unexpected tag class for fitment attribute " + className);
                }
            });
            ////////////////////////////////
            ItemCarAttribute attribute = new ItemCarAttribute();
            String attName = "";
            String attValue = "";
            try {
                attName = attBlock.findElement(By.className("applicationAttributeName")).getText();
            }
            catch (NoSuchElementException e){
                logger.info("Couldn't find attribute name for " + attBlock.getText());
            }
            try {
                attValue = attBlock.findElement(By.className("applicationRequiredProducts")).getText();
            }
            catch (NoSuchElementException e){
                logger.info("Couldn't find attribute value for " + attBlock.getText());
            }

            attribute.setAttName(attName);
            attribute.setAttValue(attValue);
            result.add(attribute);
        });



        return result;
    }

    private boolean carPageOpened(WebDriver driver, WebElement carGroupEl) throws IOException {
        String url = carGroupEl.getAttribute("href");
        String id = carGroupEl.getAttribute("id");
        if (!SileniumUtil.openItemPage(driver, url)){
            return false;
        }
        logger.debug("page opened " + url);
        try {
            SileniumUtil.waitForElementLocatedBy(driver, By.id(id));
        }
        catch (TimeoutException e){
            logger.error("Couldn't open Car section for " + url);
            return false;
        }

        return true;
    }

    private Car getTemplateCar(String carString) {
        Car car = new Car();

        String years = StringUtils.substringBetween(carString, "yrg=", "&ma=");
        String make = StringUtils.substringBetween(carString, "&ma=", "&mo=");
        make = make.replaceAll("%20", " ");
        String model = StringUtils.substringAfter(carString,"&mo=");
        model = model.replaceAll("%20", " ");

        car.setStartFinish(years);
        car.setMake(make);
        car.setModel(model);

        return car;
    }

    private List<String> getCarGroupIDs(List<WebElement> carGroupElements) {
        List<String> result = new ArrayList<>();
        carGroupElements.forEach(carGrEl->{
            WebElement divEl = null;
            try {
                divEl = carGrEl.findElement(By.tagName("a"));
                result.add(divEl.getAttribute("id"));
            }
            catch (NoSuchElementException e){
                logger.error("Couldn't get element with ID for " + carGrEl.getText());
            }
        });

        return result;
    }

    private List<WebElement> getCarGroupElements(WebDriver driver) {
        List<WebElement> result = new ArrayList<>();
        WebElement carTabEl = null;
        try {
            carTabEl = SileniumUtil.getElementLocatedBy(driver, By.id("webcontent_0_row2_0_productDetailTabs_upThisFitsTab"));
        }
        catch (TimeoutException e){
            logger.error("Couldn't find car element tab at " + driver.getCurrentUrl());
            return result;
        }
        logger.debug("Car tab found by ID webcontent_0_row2_0_productDetailTabs_upThisFitsTab");
        List<WebElement> carGroupEls = driver.findElements(By.cssSelector("div[id='webcontent_0_row2_0_productDetailTabs_upThisFitsTab'] > div"));
        try {
            carGroupEls = carGroupEls.subList(1, carGroupEls.size()-1);
        }
        catch (IndexOutOfBoundsException e){
            logger.error("unexpected quantity of div elements at car tab at " + driver.getCurrentUrl());
            return result;
        }

        return carGroupEls;
    }

    private boolean fitsOpened(WebDriver driver) {
        try {
            driver.findElement(By.id("webcontent_0_row2_0_productDetailTabs_divThisFitsTab")).click();
        }
        catch (NoSuchElementException e){
            logger.error("No fits tab found for " + driver.getCurrentUrl());
            return false;
        }
        logger.debug("fits tab clicked.");
        By waitTabBy = By.cssSelector("div[id='webcontent_0_row2_0_productDetailTabs_divThisFitsTab'][class='PartTabShort PartTabOn']");
        try {
            SileniumUtil.waitForElementLocatedBy(driver, waitTabBy);
        }
        catch (TimeoutException e){
            return false;
        }
        logger.debug("fits tab opened");
        return true;
    }

    private void getImgLinks(KeyItem item, WebDriver driver) {
        logger.debug("getting img links for " + driver.getCurrentUrl());
        WebElement imageBlockEl = null;
        try {
            imageBlockEl = SileniumUtil.getElementLocatedBy(driver, By.id("partImage"));
        }
        catch (TimeoutException e){
            logger.error("Couldn't find image block element for " + driver.getCurrentUrl());
            item.setImgLinks("NO_IMG_LINKS");
            return;
        }
        StringBuilder builder = new StringBuilder();
        WebElement focusImgEl = null;
        try {
            focusImgEl = imageBlockEl.findElement(By.className("partPix"));
            builder.append(focusImgEl.findElement(By.tagName("img")).getAttribute("src"));
        }
        catch (NoSuchElementException e){
            logger.error("Couldn't find image in image block element for " + driver.getCurrentUrl());
            item.setImgLinks("NO_IMG_LINKS");
            return;
        }
        WebElement thumbnailEl = null;
        try {
            thumbnailEl = imageBlockEl.findElement(By.id("webcontent_0_row2_0_divImageScroller"));
            List<WebElement> imgEls = thumbnailEl.findElements(By.className("thslide_list_frame"));
            builder = new StringBuilder(); //in scroller we will always have link to main Img - so we remove one, we already have
            StringBuilder finalBuilder = builder;
            imgEls.forEach(imgEl->{
                try {
                    String imgLink = imgEl.findElement(By.tagName("img")).getAttribute("src");
                    imgLink = imgLink.replace("maxheight=68", "maxheight=250");
                    imgLink = imgLink.replace("maxwidth=68", "maxwidth=400");
                    finalBuilder.append(imgLink);
                    finalBuilder.append(System.lineSeparator());
                }
                catch (NoSuchElementException e){
                    logger.error("No img tag in thumbnail element at " + driver.getCurrentUrl());
                    return;
                }
            });
           item.setImgLinks(finalBuilder.toString());
        }
        catch (NoSuchElementException e){
            item.setImgLinks(builder.toString());
        }

    }

    private void getShortDesc(KeyItem item, WebDriver driver) {
        logger.debug("getting short desc for " + driver.getCurrentUrl());
        WebElement descKeeper = null;
        try {
            descKeeper = SileniumUtil.getElementLocatedBy(driver, By.id("webcontent_0_row2_0_productDetailHeader_lblDescription"));
        }
        catch (TimeoutException e){
            logger.error("Couldn't find short desc element for " + driver.getCurrentUrl());
            item.setShortDescription("NO_SHORT_DESC");
            return;
        }
        item.setShortDescription(descKeeper.getText());
    }

    private void getDesc(KeyItem item, WebDriver driver) {
        logger.debug("getting desc for " + driver.getCurrentUrl());
        WebElement descKeeper = null;
        try {
            descKeeper = SileniumUtil.getElementLocatedBy(driver, By.id("webcontent_0_row2_0_productDetailTabs_lblMarketingDescription"));
        }
        catch (TimeoutException e){
            logger.error("Couldn't find desc element for " + driver.getCurrentUrl());
            item.setDescription("NO_DESCRIPTION");
            return;
        }
        item.setDescription(descKeeper.getText());
    }

    private void getSpecs(KeyItem item, WebDriver driver) {
        logger.debug("getting specs for " + driver.getCurrentUrl());
        WebElement specElsKeeper = null;
        try {
            specElsKeeper = SileniumUtil.getElementLocatedBy(driver, By.id("webcontent_0_row2_0_productDetailTabs_upAdditionalInfoTab"));
        }
        catch (TimeoutException e){
            logger.error("Couldn't find specifications element for " + driver.getCurrentUrl());
            return;
        }
        try {
            specElsKeeper = specElsKeeper.findElement(By.cssSelector("div[class='devMarginBottom']"));
        }
        catch (NoSuchElementException e){
            logger.error("Couldn't find specifications subElement (div[class='devMarginBottom']) for " + driver.getCurrentUrl());
            return;
        }
        List<WebElement> specEls = specElsKeeper.findElements(By.className("productAttribute"));
        List<Specification> specs = new ArrayList<>();
        specEls.forEach(specEl->{
            List<WebElement> specData = specEl.findElements(By.tagName("span"));
            if (specData.size()>1){
                Specification spec = new Specification();
                spec.setSpecName(specData.get(0).getText());
                spec.setSpecValue(specData.get(1).getText());
                specs.add(spec);
            }
        });
        item.setSpecs(specs);
        specs.forEach(logger::debug);
    }

    private void getFeatures(KeyItem item, WebDriver driver) {
        logger.debug("Building item for " + driver.getCurrentUrl());
        WebElement featuresEl = null;
        try {
            featuresEl = SileniumUtil.getElementLocatedBy(driver, By.id("webcontent_0_row2_0_productDetailTabs_divFeatures"));
        }
        catch (TimeoutException e){
            logger.error("Couldn't find features element for " + driver.getCurrentUrl());
            item.setFeatures("FEATURES_NOT_FOUND");
            return;
        }
        try {
            featuresEl = featuresEl.findElement(By.tagName("ul"));
        }
        catch (NoSuchElementException e){
            logger.error("Couldn't find features subElement for " + driver.getCurrentUrl());
            item.setFeatures("FEATURES_NOT_FOUND");
            return;
        }
        item.setFeatures(featuresEl.getText());
    }

    private void getPartNo(KeyItem item, WebDriver driver) {
        WebElement partNoEl = null;
        try {
            partNoEl = SileniumUtil.getElementLocatedBy(driver, By.id("webcontent_0_row2_0_productDetailBasicInfo_lblPartNumber"));
        }
        catch (TimeoutException e){
            logger.error("Couldn't find partNo element for " + driver.getCurrentUrl());
            item.setPartNo("PART_NUMBER_NOT_FOUND");
            return;
        }
        item.setPartNo(partNoEl.getText());
    }

    private void getMake(KeyItem item, WebDriver driver) {
        WebElement makeEl = null;
        try {
            makeEl = SileniumUtil.getElementLocatedBy(driver, By.id("webcontent_0_row2_0_productDetailBasicInfo_lblSupplier"));
           // makeEl = SileniumUtil.getElementLocatedBy(driver, By.id("webcontent_0_row2_0_productDetailBasicInfo_aSupplier")); //gets make + supplier code
        }
        catch (TimeoutException e){
            logger.error("Couldn't find make element for " + driver.getCurrentUrl());
            item.setMake("MAKE_NOT_FOUND");
            return;
        }
        item.setMake(makeEl.getText());
    }
}
