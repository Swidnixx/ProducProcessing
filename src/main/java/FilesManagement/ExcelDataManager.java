package FilesManagement;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;

public class ExcelDataManager {

    public static int ID_COLUMN_INDEX = 0;
    public static int NAME_COLUMN_INDEX = 1;
    public static int PRICE_COLUMN_INDEX = 2;
    public static int DESCRIPTION_COLUMN_INDEX = 3;
    public static int SIZES_COLUMN_INDEX = 4;
    public static int CLOTH_OR_SHOE_COLUMN_INDEX = 5;


    private String fileLocation;
    private Workbook workbook;
    private Sheet sheet;
    public List<String> headerNames= List.of("Id", "Name", "Price", "Description", "isCloth", "Sizes");

    protected Map<String, List<String>> excelData;

    protected ExcelDataSaver getSaver(){
        return (ExcelDataSaver)this;
    }

    public static class idComparator implements Comparator<String>{
        @Override
        public int compare(String o1, String o2) {
            int id1 = getIndex(o1);
            int id2 = getIndex(o2);
            return Integer.compare(id1,id2);
        }
    }

    public ExcelDataManager(String fileLocation)throws IOException {

        this.fileLocation = fileLocation;
        excelData = new TreeMap<String, List<String>>(new idComparator());

        File file = new File(fileLocation);

        if(!file.exists()){

            initializeCreatingExcelFile(file);

            System.out.println("Stworzono plik excel!");

        }else {
            rewriteExcel();
            System.out.println("Odczytano dane");
        }
    }

    protected ExcelDataManager(ExcelDataManager existingReceiver){
        this.fileLocation = existingReceiver.getFileLocation();
        this.headerNames = existingReceiver.getHeaderNames();
        this.excelData = existingReceiver.getExcelData();
    }

    private void initializeCreatingExcelFile(File file) throws IOException{

        try {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Products");

            prepareHeaderToWrite();

            file.createNewFile();

            closeConnectionAndSave();
        } catch (IOException e) {
            System.out.println("Problem z utworzeniem pliku");
            throw e;
        }
    }

    protected void prepareHeaderToWrite() {
        Row headerRow = sheet.createRow(0);
        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle cellStyle = workbook.createCellStyle();

        for (int i =0; i<headerNames.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headerNames.get(i));
            cell.setCellStyle( cellStyle );
        }


        for (int i =0; i< headerNames.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void initializeRetrievingExcelData() {

        openConnectionWithExistingExcel();

        retrieveHeader(sheet.getRow(0));

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);

            Cell cell = null;
            try {
                cell = row.getCell(0);
            } catch (Exception e) {
                continue;
            }

            String id = cell.getRichStringCellValue().getString();

            if(id.trim().equals("") || id == null)
                continue;

            excelData.put(id, new ArrayList<String>());

            for(int j = 1; j < row.getLastCellNum(); j++){

                cell = row.getCell(j);

                switch(cell.getCellType()){

                    case STRING:
                        excelData.get(id).add(cell.getRichStringCellValue().getString());
                        break;

                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            excelData.get(id).add(cell.getDateCellValue() + "");
                        } else {
                            excelData.get(id).add(cell.getNumericCellValue() + "");
                        }
                        break;

                    case BOOLEAN:
                        excelData.get(id).add(cell.getBooleanCellValue()+"");
                        break;

                    case FORMULA:
                        excelData.get(id).add(cell.getCellFormula());
                        break;

                    default: excelData.get(id).add("");
                }
            }
        }
        try {
            workbook.close();
        } catch (IOException e) {
            System.out.println("Próbowano zamknąć niepoprawnie zainicjalizowane połączenie z plikiem");
        }
    }

    protected boolean openConnectionWithExistingExcel() {

        try{
            FileInputStream fileInput = new FileInputStream(fileLocation);//file not found
            workbook = new XSSFWorkbook(fileInput);//IOex
            fileInput.close(); //IO ex
            sheet = workbook.getSheetAt(0);
        }catch(FileNotFoundException e){
            System.out.println("Nie znaleziono pliku do odczytu");
            return false;
        }
        catch(IOException e) {
            System.out.println("Pojawił się błąd  przy próbie odczytnia pliku");
            return false;
        }
        return true;
    }

    protected boolean closeConnectionAndSave(){
        try {
            FileOutputStream output = new FileOutputStream(fileLocation);//FileNotFound
            workbook.write(output);//IOex
            output.close();//IOex
            workbook.close();//IOex
        }catch(FileNotFoundException e){
            System.out.println("Nie znaleziono pliku do zapisu");
            return false;
        }
        catch(IOException e) {
            System.out.println("Pojawił się błąd  przy próbie zapisu pliku");
            return false;
        }
        return true;
    }

    public void rewriteExcel(){
        initializeRetrievingExcelData();
        save();
    }

    public void save(){

        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Products");

        prepareHeaderToWrite();

        int i=1;
        String currUUID;
        TreeMap<String,List<String>> mapAfterRefactoringIDs = new TreeMap<>(new idComparator());

        for(Map.Entry<String,List<String>> entry:excelData.entrySet()){

            if(Integer.compare(getIndex(entry.getKey()),i)!=0){

               currUUID = getUUID(entry.getKey());

               mapAfterRefactoringIDs.put(currUUID+"#"+i, entry.getValue());
                addRowToSheet(currUUID+"#"+i, entry.getValue());
            }else{
                mapAfterRefactoringIDs.put(entry.getKey(), entry.getValue());
                addRowToSheet(entry.getKey(), entry.getValue());
            }
            i++;
        }

        excelData = mapAfterRefactoringIDs;

        for (int j =0; j< getSheet().getRow(0).getLastCellNum();j++) {
            getSheet().autoSizeColumn(j);
        }

        closeConnectionAndSave();
    }

    private String getUUID(String key) {
        String[] parts = key.split("#");
        return parts[0];
    }

    private void addRowToSheet(String key, List<String> values) {
        int index = getIndex(key);

        Row row = getSheet().createRow(index);

        Cell idCell = row.createCell(0);
        idCell.setCellValue(key);

        int i=1;
        for(String value:values){
            Cell cell = row.createCell(i);
            cell.setCellValue(value);
            i++;
        }
    }

    protected static int getIndex(String key) {
        String[] parts = key.split("#");
        String index = parts[parts.length-1];
        return Integer.parseInt(index);
    }

    private void retrieveHeader(Row row) {
        headerNames= new ArrayList<>();
        for(Cell cell:row){
            headerNames.add(cell.getRichStringCellValue().getString());
        }
    }

    protected Sheet getSheet(){
        return sheet;
    }

    protected String getFileLocation(){
        return fileLocation;
    }

    public Map<String, List<String>> getExcelData() {
        return excelData;
    }

    public List<String> getHeaderNames(){
        return headerNames;
    }


    public int getCount(){
        return excelData.size();
    }


    public Map<String,List<String>> getProductsLike(String search){

        HashMap<String, List<String>> products = new HashMap<>();

        excelData.forEach(new BiConsumer<String, List<String>>() {
            @Override
            public void accept(String key, List<String> row) {

                if( row.get( NAME_COLUMN_INDEX-1 ).contains(search) ){
                    products.put( key, row);
                }

            }
        });

        return products;
    }

}
