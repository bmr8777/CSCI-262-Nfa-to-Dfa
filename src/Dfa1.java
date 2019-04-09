//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Dfa1 {
    Set<String> Q;
    Set<String> Sigma;
    Map<String, String> delta;
    String s;
    Set<String> F;

    public Dfa1(String var1) throws FileNotFoundException {
        Scanner var2 = new Scanner(new File(var1));
        this.Q = null;
        this.Sigma = null;
        this.delta = new TreeMap();
        this.s = null;
        this.F = null;

        while(true) {
            while(true) {
                String var3;
                do {
                    do {
                        if (!var2.hasNextLine()) {
                            return;
                        }

                        var3 = var2.nextLine();
                        var3 = var3.trim();
                    } while(var3.length() == 0);
                } while(var3.startsWith("#"));

                Scanner var4 = new Scanner(var3);
                String var9;
                if (this.Q == null) {
                    for(this.Q = new TreeSet(); var4.hasNext(); this.Q.add(var9)) {
                        var9 = var4.next();
                        if (!this.legalState(var9)) {
                            System.out.println("state contains non-alpha-chars  besides @ and - : " + var9);
                            System.exit(1);
                        }
                    }

                    System.out.println("Q = " + this.Q);
                } else if (this.Sigma == null) {
                    this.Sigma = new TreeSet();
                    if (!var3.startsWith("@")) {
                        for(; var4.hasNext(); this.Sigma.add(var9)) {
                            var9 = var4.next();
                            if (var9.length() != 1) {
                                System.out.println("no symbols more than length  1: " + var9);
                                System.exit(1);
                            }

                            if (!Character.isLetterOrDigit(var9.charAt(0))) {
                                System.out.println("non-alphanumeric symbol and not @: " + var9);
                                System.exit(1);
                            }
                        }
                    }

                    System.out.println("Sigma = " + this.Sigma);
                } else {
                    String[] var5;
                    if (this.s == null) {
                        var5 = var3.split("\\s+");
                        if (var5.length > 1) {
                            System.out.println("only one start state allowed " + var3);
                            System.exit(1);
                        }

                        this.s = var4.next();
                        if (!this.Q.contains(this.s)) {
                            System.out.println("start state " + this.s + " must be in set of states " + this.Q);
                            System.exit(1);
                        }
                    } else if (this.F == null) {
                        this.F = new TreeSet();
                        if (!var3.startsWith("@")) {
                            for(; var4.hasNext(); this.F.add(var9)) {
                                var9 = var4.next();
                                if (!this.Q.contains(var9)) {
                                    System.out.println("accept state " + var9 + " not in set of states " + this.Q);
                                    System.exit(1);
                                }
                            }
                        }
                    } else {
                        var5 = var3.split("\\s+");
                        if (var5.length != 3) {
                            System.out.println("illegal transition specification " + var3);
                            System.exit(1);
                        }

                        String var6 = var4.next();
                        String var7 = var4.next();
                        String var8 = var4.next();
                        if (!this.Q.contains(var6) || !this.Q.contains(var8)) {
                            System.out.println("states " + var6 + " and " + var8 + " must both be in " + this.Q);
                            System.exit(1);
                        }

                        if (!this.Sigma.contains(var7)) {
                            System.out.println(var7 + " not in alphabet " + this.Sigma);
                            System.exit(1);
                        }

                        this.delta.put(var6 + "," + var7, var8);
                    }
                }
            }
        }
    }

    private boolean legalState(String var1) {
        for(int var2 = 0; var2 < var1.length(); ++var2) {
            char var3 = var1.charAt(var2);
            if (!Character.isLetterOrDigit(var3) && var3 != '-' && var3 != '@') {
                return false;
            }
        }

        return true;
    }

    public String toString() {
        String var1 = "DFA:\n";
        var1 = var1 + "Q = " + this.Q + "\n";
        var1 = var1 + "Sigma = " + this.Sigma + "\n";
        var1 = var1 + "delta = " + this.delta + "\n";
        var1 = var1 + "s = " + this.s + "\n";
        var1 = var1 + "F = " + this.F;
        return var1;
    }

    public void acceptReject(String var1) {
        boolean var2 = false;
        if (var1.length() > 0 && var1.charAt(0) == '!') {
            var2 = true;
            var1 = var1.substring(1);
        }

        int var3 = var1.length();
        String var4 = this.s;

        for(int var5 = 0; var5 < var3; ++var5) {
            char var6 = var1.charAt(var5);
            String var7 = var4 + "," + var6;
            String var8 = null;
            if (!this.delta.containsKey(var7)) {
                System.out.println("no known transition from state " + var4 + " using symbol " + var6);
                var4 = null;
                break;
            }

            var8 = (String)this.delta.get(var7);
            if (var2) {
                System.out.println(var4 + "," + var6 + " -> " + var8);
            }

            var4 = var8;
        }

        if (var4 != null) {
            if (this.F.contains(var4)) {
                System.out.println("accept");
            } else {
                System.out.println("reject");
            }

        }
    }

    public static void main(String[] var0) throws FileNotFoundException {
        Scanner var1 = new Scanner(System.in);
        System.out.print("DFA specification file name: ");

        while(var1.hasNextLine()) {
            String var2 = var1.nextLine();
            Dfa1 var3 = new Dfa1(var2);
            System.out.println(var3);
            System.out.print("> ");
            String var4 = var1.nextLine();

            for(var4 = var4.trim(); !var4.equals("."); var4 = var4.trim()) {
                var3.acceptReject(var4);
                System.out.print("> ");
                var4 = var1.nextLine();
            }

            System.out.print("DFA specification file name: ");
        }

    }
}