package com.tks.util;

import java.util.Scanner;

public class IOUtil {
    
    public static int inputInt(Scanner scan) {
        int input;
        while (true) {
            try {
                input = Integer.parseInt(scan.nextLine());
                break;
            }catch(Exception ex) {
                System.out.println("Wrong input: ");
                System.out.print("Try again: ");
                continue;
            }
        }
        return input;
    }

}