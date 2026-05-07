package com.sg.flooringmastery.ui;

import java.math.BigDecimal;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Scanner;

public class UserIOConsoleImpl implements UserIO {
    private Scanner sc = new Scanner(System.in);


    @Override
    public void print(String msg) {
        System.out.println(msg);
    }

    @Override
    public String readString(String prompt) {
        System.out.println(prompt);
        return sc.nextLine();
    }

    @Override
    public int readInt(String prompt) {
        while (true) {
            try {
                System.out.println(prompt);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    @Override
    public int readInt(String prompt, int min, int max) {
        int userInput;
        while (true) {
            userInput = readInt(prompt);
            if (userInput >= min && userInput <= max) return userInput;
            System.out.println("Please enter a number between " + min + " and " + max);
        }
    }

    @Override
    public BigDecimal readBigDecimal(String prompt) {
        while (true) {
            try {
                System.out.println(prompt);
                return new BigDecimal(sc.nextLine().trim());
            } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a decimal number.");
            }
        }
    }

    @Override
    public BigDecimal readBigDecimal(String prompt, BigDecimal min) {
        BigDecimal userInput;
        while (true) {
            userInput = readBigDecimal(prompt);
            if (userInput.compareTo(min) >= 0) return userInput;
            System.out.println("Please enter a number at least " + min);
        }
    }
}
