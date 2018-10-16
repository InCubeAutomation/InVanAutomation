package collections;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilterLists {
    List<List<String>> filteredList = new ArrayList<List<String>>();
    public List<List<String>> filterList(List<List<String>> listToFilter,Integer indexToFilter,String filterValue){
        filteredList = listToFilter.stream()
                .filter(row -> row.get(indexToFilter).equals(filterValue))
                .collect(Collectors.toList());
        return filteredList;

    }
}