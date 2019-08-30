package com.tks;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.tks.util.IOUtil;
import com.tks.util.StringProcess;

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
        ArrayList<Production> productions = new ArrayList<>();
        for (String p : rawProductions) {
            StringTokenizer st = new StringTokenizer(p);
            String lhs = st.nextToken();
            st.nextToken();
            String rhs = st.nextToken();
            st = new StringTokenizer(rhs, "/");
            while (st.hasMoreTokens()) {
                Production pr = new Production(lhs, st.nextToken());
                productions.add(pr);
            }
        }
        for (int i = 0; i < productions.size(); i++) {
            Production pVal = productions.get(i);
            pVal.type = getType(pVal);
            productions.set(i, pVal);
        }

        if (DEBUG) {
            System.out.println("Productions rules are: ");
            for (Production prod : productions) {
                System.out.println(prod);
            }
        }

        int commonType = 5;
        for (Production prod : productions) {
            String type = prod.type;
            int t = -1;
            if (type.equals("invalid")) t = -1;
            else if (type.charAt(0) == 't') t = 3;
            else t = Integer.parseInt(type);
            if (t <= commonType) commonType = t;
        }
        if (DEBUG) {
            System.out.println("Before Exception Common Type: "+commonType);
        }
	if (commonType == 3) {
            commonType = checkType3Exception(productions) ? 2 : commonType;

            if (DEBUG) {
                if (checkType3Exception(productions)) {
                    System.out.println("Failed Type 3 Exception");
                }
            }
        }
        if (commonType >= 1) {
            commonType = checkType1Exception(productions) ? 0 : commonType;

            if (DEBUG) {
                if (checkType1Exception(productions)) {
                    System.out.println("Failed Type 1 Exception");
                }
            }
        }
        if (commonType != -1) {
            System.out.println("Above laguage is Type-"+commonType+" Language");
        }else{
            System.out.println("Invalid Production Rules");
        }
    }

    public static boolean checkType0(Production prod) {
        String V = prod.alpha;
        for (char v : V.toCharArray()) {
            if (Character.isUpperCase(v)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkType1(Production prod) {
        if (!checkType0(prod)) {
            return false;
        }
        String alpha = prod.alpha;
        String beta = prod.beta;
        return alpha.length() <= beta.length();
    }

    public static boolean checkType2(Production prod) {
        if (!checkType1(prod)) {
            return false;
        }
        String alpha = prod.alpha;
        return alpha.length() == 1;
    }

    public static String getType(Production prod) {
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

    public static boolean checkType1Exception(ArrayList<Production> productions) {
        boolean isSinRhs = false;
        boolean isS$ = false;
        for (Production prod : productions) {
            String lhs = prod.alpha;
            String rhs = prod.beta;
            for (char c : rhs.toCharArray()) {
                if (c == 'S') {
                    isSinRhs = true;
                }
            }
            if (lhs.equals("S") && rhs.equals("$")) {
                isS$ = true;
            }
        }
        return isS$ && isSinRhs;
    } 

    public static boolean checkType3Exception(ArrayList<Production> productions) {
        boolean isT1 = false;
        boolean isT2 = false;
        for (Production prod : productions) {
            String type = prod.type;
            if (type.charAt(0) == 't') {
                if (type.charAt(1) == '2') isT1 = true;
                if (type.charAt(1) == '3') isT2 = true;
            }
        }
        return isT1 && isT2;
    }

    public static int checkType3(Production prod) {
        if (!checkType2(prod))
            return 0;
        String rhs = prod.beta;
        // Check if rhs is T*
        if (isAllTerminals(rhs) || (rhs.length() == 1 && Character.isUpperCase(rhs.charAt(0)))) {
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
        remaining = rhs.length() == 1 ? "" : rhs.substring(0, rhs.length()-1);
        
        if (Character.isUpperCase(lV) && (remaining.length() == 0 || isAllTerminals(remaining))) {
            return 3;
        }
        return 0;
    }

    public static boolean isAllTerminals(String term) {
        for (char ch : term.toCharArray()){
            if (!Character.isLowerCase(ch) && ch != '$') {
                return false;
            }
        }
        return true;
    }

}

class Production {
    String alpha;
    String beta;
    String type;
    Production() {
        this.alpha = "";
        this.beta = "";
        this.type = "";
    }

    Production(String alpha, String beta) {
        this();
        this.alpha = alpha;
        this.beta = beta;
    }

    Production(String alpha, String beta, String type) {
        this.alpha = alpha;
        this.beta = beta;
        this.type = type;
    }

    @Override
    public String toString() {
        String output = "";
        output = this.alpha + " -> " + this.beta;
        output = this.type.length() == 0 ? output : output + " Type: " + this.type;
        return output;
    }
}
