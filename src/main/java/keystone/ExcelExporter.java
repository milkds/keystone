package keystone;

import keystone.entities.Car;
import keystone.entities.ExcelCar;
import keystone.entities.KeyItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StrBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;

public class ExcelExporter {

    private static final Logger logger = LogManager.getLogger(ExcelExporter.class.getName());
    private static final String FINAL_FILE_PATH = "src\\main\\resources\\from_db.xlsx";

    public void exportToExcel(){
        Set<KeyItem> allItems = KeyDAO.getAllParsedItems();
        List<String> newLinksList = new JobDispatcher().getFullLinks();
        Set<String> newItems = new HashSet<>(newLinksList);
        Set<KeyItem> itemsToWrite = new HashSet<>();
        allItems.forEach(item->{
            String webLink = item.getWebLink();
            if (webLink!=null&&webLink.length()>0&&newItems.contains(webLink)){
                itemsToWrite.add(item);
            }
        });

        writeDBToExcel(itemsToWrite);
        HibernateUtil.shutdown();
    }

    private void writeDBToExcel(Set<KeyItem> allItems) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        setFirstRow(sheet);
        Integer counter = 1;//sheet counter. 0 is first row.

        for (KeyItem item: allItems){
            counter = setCells(item, counter, sheet);
        }
        
        saveWorkBook(workbook);

    }

    private void saveWorkBook(Workbook workbook) {
        File file = null;
        try {
            file = new File(FINAL_FILE_PATH);
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private Integer setCells(KeyItem item, Integer counter, Sheet sheet) {
        if (item.getMake().equals("MAKE_NOT_FOUND")){
           return counter;
        }
        Set<Car> carsForItem = getCarsForItem(item);
        Set<String> carStringsForItem = getCarStringsForItem(carsForItem);
        String carString = getTotalCarString(carStringsForItem);

        Row row = sheet.createRow(counter);
        counter++;

        Cell currentCell = row.createCell(0);
        String title = getTitle(item, carString);
        currentCell.setCellValue(title);

        currentCell = row.createCell(1);
        currentCell.setCellValue(item.getMake());

        currentCell = row.createCell(2);
        currentCell.setCellValue(item.getPartNo());

        currentCell = row.createCell(3);
        currentCell.setCellValue(convertPrice(item.getMyPrice()));

        currentCell = row.createCell(4);
        currentCell.setCellValue(convertPrice(item.getJobberPrice()));

        currentCell = row.createCell(5);
        currentCell.setCellValue(convertPrice(item.getRetailPrice()));

        currentCell = row.createCell(6);
        currentCell.setCellValue(item.getDocLinks());

        currentCell = row.createCell(7);
        currentCell.setCellValue(carString);

        currentCell = row.createCell(13);
        currentCell.setCellValue(item.getShortDescription());

        currentCell = row.createCell(14);
        currentCell.setCellValue(getHtmlDesc(item));

        currentCell = row.createCell(15);
        currentCell.setCellValue(item.getPlainDesc());

        String frontLift = getFrontLift(item);
        currentCell = row.createCell(16);
        currentCell.setCellValue(frontLift);

        String rearLift = getRearLift(item);
        currentCell = row.createCell(17);
        currentCell.setCellValue(rearLift);

        currentCell = row.createCell(18);
        currentCell.setCellValue(getLiftRange(frontLift));

        currentCell = row.createCell(19);
        currentCell.setCellValue(getLiftRange(rearLift));

        currentCell = row.createCell(20);
        currentCell.setCellValue(getImgLinks(item));

        currentCell = row.createCell(21);
        currentCell.setCellValue(getKitContents(item));

        currentCell = row.createCell(22);
        currentCell.setCellValue(getDriveString(carsForItem));

        currentCell = row.createCell(23);
        currentCell.setCellValue(item.getWebLink());

        currentCell = row.createCell(24);
        currentCell.setCellValue(getCaptions(item));


        int currentRowNo = counter-1;
        counter = setFitments(sheet, counter, carsForItem);
        setCars(sheet, currentRowNo, carsForItem);

        return counter;
    }

    private String getCaptions(KeyItem item) {
        String imgUrls = item.getImgLinks();
        String[]split = imgUrls.split(";;");
        if (split.length==1){
            return "NO CAPTIONS";
        }
        StringBuilder captBuilder = new StringBuilder();
        for (String s: split){
            s = StringUtils.substringBefore(s, "http");
            if (s.length()>0){
                captBuilder.append(s);
                captBuilder.append(System.lineSeparator());
            }
        }

        return captBuilder.toString();
    }

    private String getDriveString(Set<Car> carsForItem) {
        Set<String> drives = new HashSet<>();
        carsForItem.forEach(car -> {
            String drive = getDrive(car);
            if (drive!=null&&drive.length()>0){
                drives.add(drive);
            }
        });
        if (drives.size()==0){
            return "";
        }
        StringBuilder drvBuilder = new StringBuilder();
        drives.forEach(drive->{
            drvBuilder.append(drive);
            drvBuilder.append("|");
        });
        drvBuilder.setLength(drvBuilder.length()-2);

        return drvBuilder.toString();
    }

    private String getKitContents(KeyItem item) {
        String contents = item.getKitElements();
        if (contents==null){
            return "";
        }
        return contents;
    }

    private String getDocLinks(KeyItem item) {
        String docLinks = item.getDocLinks();
        if (docLinks==null){
            return "";
        }
        return docLinks;
    }

    private String getLiftRange(String lift) {
        String rawLift = lift.toLowerCase();
        rawLift = rawLift.replace(" inch", "");
        if (!rawLift.contains("to")){
            if (!rawLift.contains("/")){
                return rawLift;
            }
            rawLift = convertToDecimal(rawLift);
           return rawLift;
        }
        String[] split = rawLift.split(" to ");
        double start = 0;
        String startStr = split[0];
        if (startStr.contains("/")){
            startStr = convertToDecimal(startStr);
        }
        start = Double.parseDouble(startStr);

        double finish = 0;
        String finishStr = split[1];
        if (finishStr.contains("/")){
            finishStr = convertToDecimal(finishStr);
        }
        finish = Double.parseDouble(finishStr);

        StringBuilder rangeBuilder = new StringBuilder();
        BigDecimal startB = new BigDecimal(start).setScale(2, RoundingMode.HALF_UP);
        BigDecimal finishB = new BigDecimal(finish).setScale(2, RoundingMode.HALF_UP);
        while (startB.compareTo(finishB)<0){
            rangeBuilder.append(startB.toString());
            rangeBuilder.append("|");
            startB = startB.add(new BigDecimal(0.25));
        }
        rangeBuilder.append(finishB.toString());

        return rangeBuilder.toString();
    }

    private String convertToDecimal(String rawLift) {
        String convertiblePart = "";
        String persistantPart = "";
        if (!rawLift.contains("-")){
            convertiblePart = rawLift;
        }
        else {
            String[] split = rawLift.split("-");
            persistantPart = split[0];
            convertiblePart = split[1];
        }

        String[] split = convertiblePart.split("/");
        double up = Double.parseDouble(split[0].trim());
        int down = Integer.parseInt(split[1].trim());

        double div = up/down;
        if (persistantPart.length()>0){
            int pers = Integer.parseInt(persistantPart);
            div = pers+div;
        }

        BigDecimal bd = BigDecimal.valueOf(div);
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return bd.toString();
    }

    private String getHtmlDesc(KeyItem item) {
        String htmlDesc = item.getHtmlDescription();
        if (htmlDesc == null){
            return "";
        }
        String shortDesc = item.getShortDescription();
        String result = htmlDesc.replace(shortDesc, "");
        result = result.substring(6); //we added line separators between descs

        return result;
    }

    private void setCars(Sheet sheet, int currentRowNo, Set<Car> carsForItem) {
        Set<ExcelCar> excelCars = getExcelCars(carsForItem);
        for (ExcelCar car: excelCars){
            Row row = sheet.getRow(currentRowNo);
            currentRowNo++;
            Cell curCell = row.createCell(8);
            curCell.setCellValue(car.getYear());

            curCell = row.createCell(9);
            curCell.setCellValue(car.getYearRange());

            curCell = row.createCell(10);
            curCell.setCellValue(car.getMake());

            curCell = row.createCell(11);
            curCell.setCellValue(car.getModel());

            curCell = row.createCell(12);
            curCell.setCellValue(getSubModels(car));
        }
    }

    private String getSubModels(ExcelCar car) {
        Set<String> subModels = car.getSubModels();
        if (subModels.size()==0){
            return "";
        }
        if (subModels.size()==1){
            return subModels.stream().findFirst().get();
        }
        StringBuilder subMoBuilder = new StringBuilder();
        subModels.forEach(s -> {
            subMoBuilder.append(s);
            subMoBuilder.append(System.lineSeparator());
        });

        subMoBuilder.setLength(subMoBuilder.length()-2);

        return subMoBuilder.toString();
    }

    private Set<ExcelCar> getExcelCars(Set<Car> carsForItem) {
        Set<ExcelCar> result = new HashSet<>();
        carsForItem.forEach(rawCar->{
            ExcelCar car = new ExcelCar();
            car.setYear(rawCar.getStartFinish());
            car.setMake(rawCar.getMake());
            car.setModel(rawCar.getModel());
            if (!result.contains(car)){
                car.getSubModels().add(rawCar.getAttString());
                car.setYearRange(getYearRange(car.getYear()));
                result.add(car);
            }
            else {
                for (ExcelCar existingCar : result) {
                    if (existingCar.equals(car)) {
                        existingCar.getSubModels().add(rawCar.getAttString());
                        break;
                    }
                }
            }
        });

        return result;
    }

    private String getYearRange(String year) {
        StringBuilder sb = new StringBuilder();
        String[] split = year.split("-");
        if (split.length==1){
            return year;
        }
        int start = Integer.parseInt(split[0]);
        int finish = Integer.parseInt(split[1]);
        while (start<finish){
            sb.append(start);
            sb.append("|");
            start++;
        }
        sb.append(finish);

        return sb.toString();
    }

    private String getImgLinks(KeyItem item) {
        String imgLinksBulk = item.getImgLinks();
        List <String> picUrls = getPicUrls(imgLinksBulk);
        StringBuilder picLinkBuilder = new StringBuilder();
        picUrls.forEach(picUrl->{
            picUrl = StringUtils.substringBefore(picUrl, "&maxheight");
            picLinkBuilder.append(picUrl);
            picLinkBuilder.append(System.lineSeparator());
        });

      /*
       picLinkBuilder.append(System.lineSeparator());
      int splitLength = split.length;
        for (int i = 0; i < splitLength ; i++) {
            String link = split[i];
            link = StringUtils.substringBefore(link, "&maxheight");
            picLinkBuilder.append(link);
           *//* //adding caption
            String caption = split[i];
            String[] captionSplt = caption.split(";;");
            if (captionSplt.length>1){
                caption = captionSplt[1];
                picLinkBuilder.append(";;");
                picLinkBuilder.append(caption);
            }
*//*

            if (splitLength-i>1){
                picLinkBuilder.append(System.lineSeparator());
            }
        }*/

        return picLinkBuilder.toString();
    }

    private List<String> getPicUrls(String imgLinksBulk) {
        List<String> result = new ArrayList<>();
        String[] split = imgLinksBulk.split(";;");
        if (split.length==1){
            split = imgLinksBulk.split(" ");
            result.addAll(Arrays.asList(split));
            return result;
        }
        else {
            for (String s: split){
                if (s.contains("http")){
                    String url = StringUtils.substringAfter(s, "http");
                    url = "http" + url;
                    result.add(url);
                }
            }
        }

        return result;
    }

    private String getRearLift(KeyItem item) {
        String shortDesc = item.getShortDescription();
        if (shortDesc.equals("NO_SHORT_DESC")){
            return "";
        }
        if (!shortDesc.contains("Rear Lift")){
            return "";
        }
        String result = StringUtils.substringBetween(shortDesc, "; ", "Rear Lift" );
        if (result==null){
            return "";
        }
        while (true){
            if (result.contains("; ")){
                result = StringUtils.substringAfter(result, "; ");
            }
            else {
                break;
            }
        }

        return result;
    }

    private String getPlainDesc(KeyItem item) {
        String shortDesc = item.getDescription();
        String plainDesc = item.getPlainDesc();
        if (shortDesc==null||shortDesc.length()==0||shortDesc.equals("NO_DESCRIPTION")){
            return Objects.requireNonNullElse(plainDesc, "");
        }

        return shortDesc +
                System.lineSeparator() +
                plainDesc;
    }

    private String getFrontLift(KeyItem item) {
        String shortDesc = item.getShortDescription();
        if (shortDesc.equals("NO_SHORT_DESC")){
            return "";
        }
        if (!shortDesc.contains("Front Lift")){
            return "";
        }
        String result = StringUtils.substringBetween(shortDesc, "; ", "Front Lift" );
        if (result==null){
            return "";
        }
        while (true){
            if (result.contains("; ")){
                result = StringUtils.substringAfter(result, "; ");
            }
            else {
                break;
            }
        }

        return result;
    }

    private Integer setFitments(Sheet sheet, Integer counter, Set<Car> carsForItem) {
        for (Car car : carsForItem) {
            Row row = sheet.createRow(counter);
            counter++;

            String carBuilder = car.getYear() +
                    " " +
                    car.getMake() +
                    " " +
                    car.getModel() +
                    " " +
                    car.getAttString();

            Cell fitmentCell = row.createCell(7);
            fitmentCell.setCellValue(carBuilder);

            /*Cell driveCell = row.createCell(11);
            driveCell.setCellValue(getDrive(car));*/
        }
        return counter;
    }

    private String getDrive(Car car) {
        String attString = car.getAttString();
        if (attString==null||attString.length()==0||!attString.contains("Wheel Drive")){
            return "";
        }
        String result = StringUtils.substringBetween(attString," ", "Wheel Drive");
        if (result==null||result.length()==0){
            result = StringUtils.substringBefore(attString, "Wheel Drive");
            if (result.length()==0){
                return result;
            }
            else {
                return result + "Wheel Drive";
            }

        }
        result = result.trim();
        String[] split = result.split(" ");
        result = split[split.length-1] + " Wheel Drive";

        return result;
    }

    private String convertPrice(BigDecimal myPrice) {

        return NumberFormat.getCurrencyInstance(Locale.US).format(myPrice.doubleValue());
    }

    private String getTitle(KeyItem item, String carString) {
        StringBuilder sb = new StringBuilder();
        sb.append(item.getMake());
        sb.append(" ");
        sb.append(item.getPartNo());
        sb.append(" for ");
        sb.append(carString);

        return sb.toString();
    }

    private String getTotalCarString(Set<String> carStringsForItem) {
        if(carStringsForItem.size()==0){
            return "NO CARS FOR ITEM";
        }
        if (carStringsForItem.size()==1){
            return carStringsForItem.stream().findFirst().get();
        }
        StringBuilder sb = new StringBuilder();
        carStringsForItem.forEach(carString->{
            sb.append(carString);
            sb.append(", ");
        });
        sb.setLength(sb.length()-2);

        return sb.toString();
    }

    private Set<String> getCarStringsForItem(Set<Car> carsForItem) {
        Set<String> result = new HashSet<>();
        carsForItem.forEach(car -> {
            String carBuilder = car.getStartFinish() +
                    " " +
                    car.getMake() +
                    " " +
                    car.getModel();
            result.add(carBuilder);
        });

        return result;
    }

    private Set<Car> getCarsForItem(KeyItem item) {
        Set<Car> result = new HashSet<>();
        item.getItemCars().forEach(fitment-> result.add(fitment.getCar()));

        return result;
    }

    private void setFirstRow(Sheet sheet) {
        Row row = sheet.createRow(0);

        Cell cell = row.createCell(0);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("Title");

        cell = row.createCell(1);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("SUPPLIER");

        cell = row.createCell(2);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("MFR PART #:");

        cell = row.createCell(3);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("MY PRICE:");

        cell = row.createCell(4);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("JOBBER PRICE:");

        cell = row.createCell(5);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("RETAIL PRICE:");

        cell = row.createCell(6);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("Documents");

        cell = row.createCell(7);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("Fitment");
//______________________________________________
        cell = row.createCell(8);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("Year");

        cell = row.createCell(9);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("Year Range");

        cell = row.createCell(10);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("Make");

        cell = row.createCell(11);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("Model");

        cell = row.createCell(12);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("Submodel");
//______________________________________________

        cell = row.createCell(13);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("DescriptionShort");

        cell = row.createCell(14);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("DescriptionTags");

        cell = row.createCell(15);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("DescriptionTags plain text");

        cell = row.createCell(16);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("FRONT_LIFT");

        cell = row.createCell(17);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("REAR_LIFT");

        cell = row.createCell(18);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("FRONT_LIFT_RANGE");

        cell = row.createCell(19);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("REAR_LIFT_RANGE");

        cell = row.createCell(20);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("img links");

        cell = row.createCell(21);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("kit contents");

        cell = row.createCell(22);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("drive");

        cell = row.createCell(23);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("item url");

        cell = row.createCell(24);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("img link captions");


        /*cell = row.createCell(11);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("drive");*/
    }
}
