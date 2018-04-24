package com.bus.chelaile.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ThreadTest {

    public static void main(String[] args) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("old thread start type");

            }
        }).start();

        // lumbda type

        new Thread(() -> System.out.println("new thread start type")).start();

        new Thread(() -> System.out.println("lambda start type")).start();

        // list
        List<String> list = Arrays.asList("1", "2", "a", "b");
        for (String s : list) {
            System.out.print(s + " ");
        }
        System.out.println("\n");
        list.forEach(s -> System.out.print(s + " "));
        System.out.print("\n");
        list.forEach(System.out::print);
        System.out.print("\n");

        System.out.print("Print all strings:");
        evaluate(list, (n) -> true);
        System.out.println("\n");

        // Stream
        //New way:
        List<Integer> list1 = Arrays.asList(1
//                , 2, 3, 4, 5, 6, 7
                );
        list.stream().map((x) -> x + "|||").forEach(System.out::print);
        System.out.println("\n");
        list1.stream().map((x) -> x * x + " ").forEach(System.out::print);
        System.out.println("\n");
        
        // 用map()给每个元素求平方，再使用reduce()将所有元素计入一个数值：
        int sum = list1.stream().
//                map(x -> x*x).
                reduce((x,y) -> x + y).get();
        System.out.println(sum);
        
        
        
        System.out.println("---->" + list.set(0, "2"));
        
    }

    // 自定义断言函数
    private static void evaluate(List<String> list, Predicate<String> predicate) {
        for (String s : list) {
            if (predicate.test(s)) {
                System.out.print(s + " ");
            }
        }
    }
}
