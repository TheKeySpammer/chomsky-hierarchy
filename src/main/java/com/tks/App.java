package com.tks;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.tks.util.IOUtil;
import com.tks.util.StringProcess;

// Test input:
// Ab -> $/abc
// B -> a/A
// a -> $

public class App {

    private static final boolean DEBUG = false;

    public static void main(String[] args) {
        // Input Production functions from user
        // Production Functinos should of form: Aa$ -> abcdEf
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter number of production functions: ");
        // (\$?[A-Z\$a-z]+\$?) -> (\$?[A-Z\$/a-z]+\$?)
        int noOfp = IOUtil.inputInt(scan);
        String rawProductions[] = new String[noOfp];
        System.out.printf("Enter %d production funtions (Use $ for lambda)\n", noOfp);
        for (int i = 0; i < rawProductions.length; i++) {
            while (true) {
                String input = scan.nextLine();
                input = StringProcess.makeSingleSpaced(input);
                if (!input.matches("(\\$?[A-Z\\$a-z]+\\$?) -> (\\$?[A-Z\\/\\$a-z]+\\$?)")) {
                    System.out.println("Wrong input");
                    System.out.println("Input should be of format  <T or V> -> <T or V>. Example: AB -> cd/$/ab");
                    System.out.println("Try again: ");
                    continue;
                } else {
                    rawProductions[i] = input;
                    break;
                }
            }
        }
        ArrayList<String> productions = new ArrayList<>();
        for (String p : rawProductions) {
            StringTokenizer st = new StringTokenizer(p);
            String lsh = st.nextToken();
            st.nextToken();
            String rhs = st.nextToken();
            st = new StringTokenizer(rhs, "/");
            while (st.hasMoreTokens()) {
                productions.add(lsh + " " + st.nextToken());
            }
        }
        for (int i = 0; i < productions.size(); i++) {
            String pVal = productions.get(i);
            productions.set(i, pVal+" "+getType(pVal));
        }

        if (DEBUG) {
            System.out.println("Productions rules are: ");
            for (String prod : productions) {
                System.out.println(prod);
            }
        }

        int commonType = -1;
        for (String prod : productions) {
            StringTokenizer st = new StringTokenizer(prod);
            st.nextToken();
            st.nextToken();
            String type = st.nextToken();
            int t = -1;
            if (type.equals("invalid")) t = -1;
            else if (type.charAt(0) == 't') t = 3;
            else t = Integer.parseInt(type);
            if (t >= commonType) commonType = t;
        }
        if (DEBUG) {
            System.out.println("Common Type: "+commonType);
        }
        if (commonType >= 1) {
            commonType = checkType1Exception(productions) ? 0 : commonType;
        }
        if (commonType == 3) {
            commonType = checkType3Exception(productions) ? 2 : commonType;
        }
        if (commonType != -1) {
            System.out.println("Above laguage is Type-"+commonType+" Language");
        }else{
            System.out.println("Invalid Production Rules");
        }
    }

    public static boolean checkType0(String prod) {
        StringTokenizer st = new StringTokenizer(prod);
        String V = st.nextToken();
        for (char v : V.toCharArray()) {
            if (Character.isUpperCase(v)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkType1(String prod) {
        if (!checkType0(prod)) {
            return false;
        }
        StringTokenizer st = new StringTokenizer(prod);
        String alpha = st.nextToken();
        String beta = st.nextToken();
        return alpha.length() <= beta.length();
    }

    public static boolean checkType2(String prod) {
        if (!checkType1(prod)) {
            return false;
        }
        StringTokenizer st = new StringTokenizer(prod);
        String alpha = st.nextToken();
        return alpha.length() == 1;
    }

    public static String getType(String prod) {
        String type = "invalid";
        if (checkType3(prod) != 0) {
            type = "t"+checkType3(prod);
        } else if (checkType2(prod)) {
            type = "2";
        } else if (checkType1(prod)) {
            type = "1";
        } else if (checkType0(prod)) {
            type = "0";
        }
        return type;
    }

    public static boolean checkType1Exception(ArrayList<String> productions) {
        boolean isSinRhs = false;
        boolean isS$ = false;
        for (String prod : productions) {
            StringTokenizer st = new StringTokenizer(prod);
            String lhs = st.nextToken();
            String rhs = st.nextToken();
            for (char c : rhs.toCharArray()) {
                if (c == 'S') {
                    isSinRhs = true;
                    break;
                }
            }
            if (lhs.equals("S") && rhs.equals("$")) {
                isS$ = true;
            }
        }
        return isS$ && isSinRhs;
    } 

    public static boolean checkType3Exception(ArrayList<String> productions) {
        boolean isT1 = false;
        boolean isT2 = false;
        for (String prod : productions) {
            StringTokenizer st = new StringTokenizer(prod);
            st.nextToken();
            st.nextToken();
            String type = st.nextToken();
            if (type.charAt(0) == 't') {
                if (type.charAt(1) == '2') isT1 = true;
                if (type.charAt(1) == '3') isT2 = true;
            }
        }
        return isT1 && isT2;
    }

    public static int checkType3(String prod) {
        if (!checkType2(prod))
            return 0;
        StringTokenizer st = new StringTokenizer(prod);
        st.nextToken();
        String rhs = st.nextToken();
        // Check if rhs is T*
        if (isAllTerminals(rhs)) {
            return 1;
        }
        // VT* type
        char lV = rhs.charAt(0);
        String remaining = rhs.length() == 1 ? "" : rhs.substring(1);
        if (Character.isUpperCase(lV) && (remaining.length() == 0 || isAllTerminals(remaining))) {
            return 2;
        }
        // T*V type
        lV = rhs.charAt(rhs.length()-1);
        remaining = rhs.length() == 1 ? "" : rhs.substring(0, rhs.length()-2);
        if (Character.isUpperCase(lV) && (remaining.length() == 0 || isAllTerminals(remaining))) {
            return 3;
        }
        return 0;
    }

    public static boolean isAllTerminals(String term) {
        for (char ch : term.toCharArray()){
            if (!Character.isLowerCase(ch) && ch != '$') return false;
        }
        return true;
    }

}
