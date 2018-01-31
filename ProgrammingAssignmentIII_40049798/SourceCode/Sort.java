import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

public class Sort {


    public static TreeMap<String, Double> sortMapByValue(HashMap<String, Double> map){
        Comparator<String> comparator = new ValueComparator(map);
        //TreeMap is a map sorted by its keys.
        //The comparator is used to sort the TreeMap by keys.
        TreeMap<String, Double> result = new TreeMap<String, Double>(comparator);
        result.putAll(map);
        return result;
    }
}

// a comparator that compares Strings
class ValueComparator implements Comparator<String>{
    HashMap<String, Double> map = new HashMap<String, Double>();
    public ValueComparator(HashMap<String, Double> map){
        this.map.putAll(map);
    }
    public int compare(String s1, String s2) {
        if(map.get(s1) >= map.get(s2)){
            return -1;
        }else{
            return 1;
        }
    }
}