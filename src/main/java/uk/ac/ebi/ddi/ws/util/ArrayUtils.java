package uk.ac.ebi.ddi.ws.util;

import java.lang.reflect.Array;
import java.util.Set;

public class ArrayUtils {

    public static  <T> T[] setToArray(Set<T> argSet, Class<T> type) {
        return argSet.toArray((T[]) Array.newInstance(type, argSet.size()));
    }
}
