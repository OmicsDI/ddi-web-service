package uk.ac.ebi.ddi.ws.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapUtilsTest {

    @Test
    public void testEliminateSet(){
        Map<String, Set<String>> dateSet = new HashMap<String,Set<String>>();
        dateSet.put("publication", new HashSet<>(Arrays.asList("2013-11-27")));
        dateSet.put("submission", new HashSet<>(Arrays.asList("2013-04-10")));
        Map<String, String> resultedDatesMap = MapUtils.eliminateSet(dateSet);
        Assert.assertEquals(resultedDatesMap.size(),2);

        dateSet = new HashMap<String,Set<String>>();
        resultedDatesMap = MapUtils.eliminateSet(dateSet);
        Assert.assertTrue(resultedDatesMap.isEmpty());

        dateSet = null;
        resultedDatesMap = MapUtils.eliminateSet(dateSet);
        Assert.assertNull(resultedDatesMap);
    }

    @Test
    public void testSortByValue() {
        Map<String, Double> distances = new HashMap<>();
        distances.put("Acc1", 4.0);
        distances.put("Acc2", 3.4);
        distances.put("Acc3", 3.7);

        distances = MapUtils.sortByValue(distances);
        Double expectedDistance = new Double(3.4);
        Assert.assertEquals(expectedDistance , distances.get(distances.keySet().toArray()[0]));
    }
}
