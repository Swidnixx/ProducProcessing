package FilesManagement;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ExcelDataReader extends ExcelDataManager {

    public ExcelDataReader(String fileLocation)throws IOException {
        super(fileLocation);
    }

    public ExcelDataReader(ExcelDataManager receiver){
        super(receiver);
    }

    public void printAllData() {

        printHeaders();

        excelData.forEach(new BiConsumer<String, List<String>>() {
            @Override
            public void accept(String key, List<String> row) {
                printRow(key, row);
            }
        });
    }

    public void printHeaders(){

        headerNames.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.print(s + "\t");
            }
        });
        System.out.println();
    }


    public void printProduct(String search) {

        System.out.println("Drukuje tylko: " + search);

        getProductsLike(search).forEach(new BiConsumer<String, List<String>>() {
            @Override
            public void accept(String key, List<String> row) {
                printRow(key, row);
            }
        });
    }

    private void printRow(String id, List<String> row) {

        System.out.print(id+ ":\t");

        row.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.print(s + "\t");
            }
        });

        System.out.println("");
    }



    //    public void testPrint(int index){
//        System.out.println(excelData.get(new Integer(1)).get(index));
//    }
}
