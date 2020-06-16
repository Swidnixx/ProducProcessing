package Model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Product {

    private final String id;
    private final String name;
    private final BigDecimal price;
    private final String description;
    private final boolean isCloth;  // else Shoe
    private final List<ClothSize> availableSizes;
    private final List<Integer> availableShoeSizes;

    public enum ClothSize{
        S, M, L, XL;

        public static ClothSize getRandomSize(){
            Random r = new Random();
            return values()[r.nextInt(values().length)];
        }
    }

    public Product(String id,String name, BigDecimal price, String description, boolean isCloth, List<ClothSize> availableSizes, List<Integer> availableShoeSizes) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.isCloth = isCloth;

        if(availableSizes != null){
            this.availableSizes = new ArrayList<>(availableSizes);
        }else {
            this.availableSizes = null; // If null - the product is shoe and vice versa
        }

        if(availableShoeSizes != null){
        this.availableShoeSizes = new ArrayList<>();
        availableShoeSizes.forEach(new Consumer<Integer>() { // to consider
            @Override
            public void accept(Integer integer) {
                if( integer < Integer.valueOf(25) || integer > Integer.valueOf(51)){
                    return;
                }
                Product.this.availableShoeSizes.add(integer);
            }
        });}else{
            this.availableShoeSizes = null;
        }
    }

    public List<String> getFields() {  // fields list without Id
        List<String> fields = List.of(
                name,
                price.toString(),
                description,
                Boolean.toString(isCloth),
                getSizesAsString(),
                getShoeSizesAsString()
        );

        return fields;
    }

    private String getSizesAsString() {
        String sizes = "";
        if(availableSizes == null)
            return "null";

        for (int i=0; i < availableSizes.size(); i++) {
            if( Integer.valueOf(i+1).equals(availableSizes.size()))
                sizes += availableSizes.get(i).name();
            else
                sizes += availableSizes.get(i).name()+",";
        }
        return sizes;
    }

    private String getShoeSizesAsString() {
        String sizes = "";
        if(availableShoeSizes == null)
            return "null";

        for (int i=0; i < availableShoeSizes.size(); i++) {
            if( Integer.valueOf(i+1).equals(availableSizes.size()))
                sizes += availableShoeSizes.get(i).toString();
            else
                sizes += availableShoeSizes.get(i).toString()+",";
        }
        return sizes;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCloth() {
        return isCloth;
    }

    public List<ClothSize> getAvailableSizes() {
        return availableSizes;
    }

    public List<Integer> getAvailableShoeSizes() {
        return availableShoeSizes;
    }

    public String getId() { return id; }
}
