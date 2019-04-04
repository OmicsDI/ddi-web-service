package uk.ac.ebi.ddi.ws.util;

import java.util.*;

public class MapUtils {

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.naturalOrder()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static Map<String, String> eliminateSet(Map<String, Set<String>> input) {
        Map<String, String> result = new HashMap<>();
        for (String key : input.keySet()) {
            result.put(key, input.get(key).iterator().next());
        }
        return result;
    }
}
