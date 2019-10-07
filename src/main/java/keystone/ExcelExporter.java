package keystone;

import keystone.entities.Car;
import keystone.entities.KeyItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ExcelExporter {

    private static final Logger logger = LogManager.getLogger(ExcelExporter.class.getName());
    private static final String FINAL_FILE_PATH = "src\\main\\resources\\from_db.xlsx";

    public void exportToExcel(){
        Set<KeyItem> allItems = KeyDAO.getAllParsedItems();
        writeDBToExcel(allItems);
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

        currentCell = row.createCell(8);
        currentCell.setCellValue(item.getImgLinks());

        currentCell = row.createCell(9);
        currentCell.setCellValue(item.getHtmlDescription());

        currentCell = row.createCell(10);
        currentCell.setCellValue("");

        counter = setFitments(sheet, counter, carsForItem);

        return counter;
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
        }
        return counter;
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

        cell = row.createCell(8);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("img links");

        cell = row.createCell(9);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("description");

        cell = row.createCell(10);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("plain description");

        cell = row.createCell(10);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("drive");

        cell = row.createCell(11);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("front lift");

        cell = row.createCell(12);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("rear lift");
    }
}
