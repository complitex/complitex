package ru.complitex.keconnection.heatmeter.service;

import java.util.Arrays;

/**
 * @author inheaven on 007 07.04.15 14:09
 */
public class StreamTest {
    public static void main (String... args){
        System.out.println(0);

        Arrays.asList(1, 2, 3, 4).forEach(i -> {
            System.out.println(i);

            if (i == 3) {
                return;
            }

            System.out.println(i);
        });

        System.out.println(5);
    }
}
