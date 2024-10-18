import java.util.ArrayList;

public class Item {
    public ArrayList<Double> parameters;
    public String specie;
    public int indexClass;
    public Item(ArrayList<Double> parameters, String specie, int indexClass){
        this.parameters = parameters;
        this.specie=specie;
        this.indexClass = indexClass;
    }
}
