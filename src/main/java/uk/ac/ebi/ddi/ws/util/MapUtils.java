package uk.ac.ebi.ddi.ws.util;

import java.util.*;
import java.util.stream.Collectors;

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
        return input.keySet().stream()
                .filter(k -> input.get(k) != null)
                .filter(k -> input.get(k).size() > 0)
                .collect(Collectors.toMap(k -> k, k -> input.get(k).iterator().next()));
    }

    public static String getFirst(Map<String, Set<String>> input, String key) {
        if (input.containsKey(key)) {
            return input.get(key).iterator().next();
        }
        return null;
    }
}
