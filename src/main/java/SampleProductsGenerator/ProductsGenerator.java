package SampleProductsGenerator;

import FilesManagement.ExcelDataManager;
import Model.Product;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Consumer;

public class ProductsGenerator {
    private static String[] NAMES = {"Koszula w kratę", "Bluza z kapturem", "Spodnie Jeans", "T-shirt biały", "T-shirt czarny", "Kurtka snowboard", "Czapka z daszkiem"};
    private int startIndex;

    private Map<String, List<String>> mapOfGeneratedProducts;
    private List<Product> listOfGenertedProducts;

    public ProductsGenerator(int countOfExistingRecords){
        this.startIndex = countOfExistingRecords;
        mapOfGeneratedProducts = new HashMap<>();
        listOfGenertedProducts = new ArrayList<>();
    }

    public Map<String, List<String>> getMapOfGeneratedProducts(){

        convertProductListToMap();
        return mapOfGeneratedProducts;
    }

    private void convertProductListToMap() {
        mapOfGeneratedProducts = new TreeMap<>(new ExcelDataManager.idComparator());
        listOfGenertedProducts.forEach(new Consumer<Product>() {
            @Override
            public void accept(Product product) {
                mapOfGeneratedProducts.put(product.getId(), product.getFields());
            }
        });
    }

    public List<Product> generateProducts(int amount){
        List<Product> products = new ArrayList<Product>();
        for(int i = startIndex; i<(startIndex+amount); i++){
            products.add(generateRandomProduct(i));
        }

        startIndex +=amount;

        listOfGenertedProducts = products;
        return  listOfGenertedProducts;
    }

    public static Product generateRandomProduct(int index){

        return new Product(generateId(index), generateName(),generatePrice(), generateDescription(), true, generateAvailableSizes(),null);
    }

    public static String generateName(){
        Random r = new Random();
        int i = r.nextInt(NAMES.length);
        return NAMES[i];
    }

    public static String generateId(int index){
        return UUID.randomUUID().toString() +"#"+ Integer.toString(index);
    }

    public static BigDecimal generatePrice(){
        Random r = new Random();
        double random = r.nextDouble();
        DecimalFormat df = new DecimalFormat("#.##");  //workaround -- to change
        String textRoundedValue = df.format(random).replace(',','.');

        Double aDouble = Double.valueOf(textRoundedValue);
        BigDecimal price = BigDecimal.valueOf( (aDouble * 600) + 40).setScale(2, RoundingMode.HALF_UP);
        return price;
    }

    public static String generateDescription(){
        Random r = new Random();
        return "Opis"+ Integer.toString(r.nextInt(100));
    }

    public static List<Product.ClothSize> generateAvailableSizes(){
        Random r = new Random();
        int amount = r.nextInt(5);

        List<Product.ClothSize> list = new ArrayList<Product.ClothSize>();

        for(int i = 0; i < amount; i++){
            Product.ClothSize size = Product.ClothSize.getRandomSize();
            if(!list.contains(size)) {
                list.add(size);

            }
        }
        return list;
    }
}
