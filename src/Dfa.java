import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Class representing the DFA, consists of 5 components
 *
 * Q - set of states
 * Σ - alphabet
 * δ - transition function
 * s - start state
 * F - set of accept states
 *
 * @author Brennan Reed
 */

public class Dfa {

    /*
      Set of the possible states in the Dfa
     */
    private Set<String> Q = new HashSet<>();

    /*
      Set of the readable characters for the Dfa
     */
    private Set<String> Sigma = new HashSet<>();

    /*
      Set of all accept states for the Dfa
     */
    private Set<String> F = new HashSet<>();

    /*
      String representation of the Dfa's start state
     */
    private String s;

    /*
      Table containing all transition functions for the Dfa
     */
    private Map<String, String> delta = new HashMap<>();

    /**
     * Constructor for the Dfa Class. Reads the provided file and builds the Dfa
     *
     * @param filename name of file containing the specifications for a Dfa
     */

    public Dfa(String filename) {
        String s1, s2, line;
        File file = new File(filename);
        String[] line_split;
        try {
            Scanner scan = new Scanner(file);
            int count = 0;
            while (scan.hasNextLine()){
                line = scan.nextLine();
                if (line.charAt(0) == '#')
                    continue;
                else
                    count++;
                switch (count){
                    case 1:
                        line_split = line.split("\\s+");
                        Collections.addAll(Q, line_split);
                        break;
                    case 2:
                        line_split = line.split("\\s+");
                        Collections.addAll(Sigma, line_split);
                        break;
                    case 3:
                        s = line;
                        break;
                    case 4:
                        line_split = line.split("\\s+");
                        Collections.addAll(F, line_split);
                        break;
                    default:
                        line_split = line.split("\\s+");
                        s1 = line_split[0] + ", " + line_split[1];
                        s2 = line_split[2];
                        delta.put(s1, s2);
                        break;
                }
            }
            scan.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main function in the program. Takes user input and
     * then passes it to the DFA constructor
     *
     * @param args Possible commandline arguments
     */

    public static void main(String[] args) {
        boolean flag = true;
        boolean reject, trace;
        String line, current_state, input_string;
        Scanner scanner = new Scanner(System.in);
        System.out.print("DFA Specification file name: ");
        String file_name = scanner.nextLine();
        Dfa dfa1 = new Dfa(file_name);
        dfa1.print_Dfa();

        while (flag){
            reject = false;
            trace = false;
            System.out.print("-> ");
            line = scanner.nextLine();
            if (line.equals("")){
                flag = false;
                System.out.println("goodbye");
            } else if(line.length() == 0){
                if (dfa1.F.contains(dfa1.s))
                    System.out.println("accept");
                else
                    System.out.println("reject");
            } else {
                if (line.charAt(0) == '!')
                    trace = true;
                String[] split_line = line.split("");
                current_state = dfa1.s;
                if (trace) {
                    for (int x = 1; x < split_line.length; x++) {
                        input_string = current_state + ", " + split_line[x];
                        if (dfa1.delta.containsKey(input_string)) {
                            current_state = dfa1.delta.get(input_string);
                            System.out.println(input_string + " -> " + current_state);
                        } else {
                            reject = true;
                            break;
                        }
                    }
                } else {
                    for (int x = 0; x < split_line.length; x++) {
                        input_string = current_state + ", " + split_line[x];
                        if (dfa1.delta.containsKey(input_string))
                            current_state = dfa1.delta.get(input_string);
                        else {
                            reject = true;
                            break;
                        }
                    }
                }
                if (!reject && dfa1.F.contains(current_state))
                    System.out.println("accept");
                else
                    System.out.println("reject");
            }
        }
        scanner.close();
    }

    /**
     * Function that displays the DFA
     */

    public void print_Dfa(){
        List<String> output = new ArrayList<>();
        String line = "Q = " + line_builder(Q);
        output.add(line);
        line = "Sigma = " + line_builder(Sigma);
        output.add(line);
        line = "q_0 = " + s;
        output.add(line);
        line = "F = " + line_builder(F);
        output.add(line);
        line = state_builder(delta);
        output.add(line);
        for (String s : output)
            System.out.println(s);
    }

    /**
     * Function that takes a Set and returns its String representation
     *
     * @param set Set to be printed
     * @return String representation of set
     */

    public String line_builder(Set<String> set){
        String line = "{";
        int count = 0;
        int size = set.size();
        for (String s: set){
            count++;
            line += ("'" + s + "'");
            if (count < size)
                line += ", ";
        }
        line += "}";
        return line;
    }

    /**
     * Function that takes a Map and returns its String representation
     *
     * @param map Map to be printed
     * @return String representation of table
     */

    public String state_builder(Map<String, String> map){
        String line = "";
        int count = 0;
        int size = map.size();
        for (String s : map.keySet()) {
            count++;
            if (count < size)
                line += ("transition: (" + s + ") -> " + map.get(s) + "\n");
            else
                line += ("transition: (" + s + ") -> " + map.get(s));
        }
        return line;
    }
}