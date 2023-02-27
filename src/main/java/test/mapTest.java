package test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Akang
 * @create 2022-12-18 17:23
 */
public class mapTest {
    public static void main(String[] args) {
        Map<String, Integer> numDict = new HashMap<>();

        numDict.put("hadoop", 1);
        numDict.put("spark", 2);
        numDict.put("scala", 3);
        numDict.put("flink", 4);

        System.out.println(numDict);
    }
}
