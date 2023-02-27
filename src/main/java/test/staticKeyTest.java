package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Akang
 * @create 2022-12-16 15:19
 */
public class staticKeyTest {
    private static List<Integer> numList = null;

    static {
        numList = new ArrayList<>() ;
        numList.addAll(Arrays.asList(1,2,3,4,5,6,7,8,9,10)) ;
    }

    public static void addNum(int a ){
        numList.add(a) ;
    }

    public static void main(String[] args) {
        addNum(20);
        System.out.println(Arrays.toString(numList.toArray()));
    }
}
