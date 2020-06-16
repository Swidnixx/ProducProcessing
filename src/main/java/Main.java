

import FilesManagement.ExcelDataManager;
import FilesManagement.ExcelDataReader;
import FilesManagement.ExcelDataSaver;
import Model.Product;
import SampleProductsGenerator.ProductsGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;


public class Main {

    public static void main(String[] args){

        String testFileLocation = "report.xlsx";

        ExcelDataManager excelDataManager;

        try {
            excelDataManager = new ExcelDataManager(testFileLocation);
        } catch (IOException e) {
            System.out.println("Spróbuj jeszcze raz");
            return;
        }

        ProductsGenerator generator = new ProductsGenerator(excelDataManager.getCount());
        List<Product> randomProducts = generator.generateProducts(1000);
        Map<String,List<String>> randomProductsMap = generator.getMapOfGeneratedProducts();
//
        ExcelDataSaver excelDataSaver = new ExcelDataSaver(excelDataManager);
        excelDataSaver.addToExcel(randomProductsMap);

        generator.generateProducts(100);
        randomProductsMap = generator.getMapOfGeneratedProducts();
        excelDataSaver.addToExcel(randomProductsMap);

        Map<String,List<String>> productsToDelete = excelDataSaver.getProductsLike("Koszula");
        excelDataSaver.deleteMultipleRows(new ArrayList<>(productsToDelete.keySet()));
//        productsToDelete.forEach(new BiConsumer<String, List<String>>() {
//            @Override
//            public void accept(String key, List<String> values) {
//                excelDataSaver.deleteRow(key);
//            }
//        });
       excelDataSaver.save();

        ExcelDataReader excelDataReader = new ExcelDataReader(excelDataSaver);

       excelDataReader.printAllData();


    }
}
