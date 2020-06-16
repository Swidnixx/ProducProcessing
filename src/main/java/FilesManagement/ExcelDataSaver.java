package FilesManagement;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ExcelDataSaver extends ExcelDataManager {

    public ExcelDataSaver(String fileLocation)throws IOException{
        super(fileLocation);
    }

    public ExcelDataSaver(ExcelDataManager manager){
        super(manager);
    }

    public void deleteRow(String key){
            openConnectionWithExistingExcel();

            int index = getIndex(key);

            getSheet().removeRow(getSheet().getRow(index));

            closeConnectionAndSave();

            excelData.remove(key);
    }

    public void deleteMultipleRows(List<String> keys){
        openConnectionWithExistingExcel();

        keys.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                int index = getIndex(s);

                getSheet().removeRow(getSheet().getRow(index));
                excelData.remove(s);
            }
        });
        closeConnectionAndSave();

        save();
    }

    public void addToExcel(Map<String,List<String>> productsToAdd){
        productsToAdd.forEach(new BiConsumer<String, List<String>>() {
            @Override
            public void accept(String key, List<String> row) {
                excelData.put(key, row);
            }
        });
        save();
    }

}
