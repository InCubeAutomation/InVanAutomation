package collections;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortLists {

    public void sortListofLists(List<List<String>> listToSort,int firstIndexToCompare,boolean firstIndexAscending,int secondIndexToCompare,boolean secondIndexAscending){

        Collections.sort(listToSort, (Comparator) (o1, o2) -> {
            String x1 =  ((List<String>) o1).get(firstIndexToCompare);
            String x2 =  ((List<String>) o2).get(firstIndexToCompare);
            int sComp;
            if(firstIndexAscending) {
                sComp = x1.compareTo(x2);
            } else {
                sComp = x2.compareTo(x1);
            }

            if (sComp != 0) {
                return sComp;
            }

                String y1 = ((List<String>) o1).get(secondIndexToCompare);
                String y2 = ((List<String>) o2).get(secondIndexToCompare);
                if(secondIndexAscending){
                return y1.compareTo(y2);
                } else {
                    return y2.compareTo(y1);
                }

        });
    }
}
