import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Class representing the NFA, consists of 5 components
 *
 * Q - set of states
 * Σ - alphabet
 * δ - transition function
 * s - start state
 * F - set of accept states
 *
 * @author Brennan Reed
 */

public class NfaToDfa {

    /*
    Set of the possible states in the NFA
     */
    private Set<String> Q = new HashSet<>();

    /*
      Set of the readable characters for the NFA
     */
    private Set<String> Sigma = new HashSet<>();

    /*
      Set of all accept states for the NFA
     */
    private Set<String> F = new HashSet<>();

    /*
      String representation of the start state for the NFA
     */
    private String s;

    /*
      Map containing all transition functions for the NFA
     */
    private Map<String, List<String>> delta = new HashMap<>();

    /*
    Set of the possible states in the DFA
     */
    private Set<String> Q_ = new HashSet<>();

    /*
      Set of the readable characters for the DFA
     */
    private Set<String> Sigma_ = new HashSet<>();

    /*
      Set of all accept states for the DFA
     */
    private Set<String> F_ = new HashSet<>();

    /*
      String representation of the start state for the DFA
     */
    private String s_;

    /*
      Map containing all transition functions for the DFA
     */
    private Map<String, String> delta_ = new HashMap<>();

    /**
     * Constructor for the NFA class. Reads the specified file, builds the NFA, then creates
     * and outputs the equivalent DFA
     *
     * @param filename name of file containing the specifications for a NFA
     */

    public NfaToDfa(String filename) throws IOException{
        String s1, line;
        File file = new File(filename);
        String[] lineSplit;
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
                    lineSplit = line.split("\\s+");
                    Collections.addAll(Q, lineSplit);
                    break;
                case 2:
                    if (line.charAt(0) == '@'){
                        Sigma = null;
                        break;
                    }
                    lineSplit = line.split("\\s+");
                    Collections.addAll(Sigma, lineSplit);
                    Sigma.add(".");
                    break;
                case 3:
                    s = line;
                    break;
                case 4:
                    if (line.charAt(0) == '@'){
                        F = null;
                        break;
                    }
                    lineSplit = line.split("\\s+");
                    Collections.addAll(F, lineSplit);
                    break;
                default:
                    lineSplit = line.split("\\s+");
                    s1 = lineSplit[0] + "," + lineSplit[1];
                    List<String> transition = new ArrayList<>();
                    for (int i = 0; i < lineSplit.length -2; i++)
                        transition.add(lineSplit[i+2]);
                    delta.put(s1, transition);
                    break;
            }
        }
        scan.close();
    }

    /**
     * Takes user input, then converts and outputs the specified NFAs equivalent DFA
     *
     * @param args Possible commandline arguments
     */

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.print("NFA Specification file name: ");
        String fileName = scanner.nextLine();
        try{
            NfaToDfa nfa1 = new NfaToDfa(fileName);
            nfa1.printNfa();
            nfa1.convertNfa();
            nfa1.printDfa();
            System.out.print("Output file name (Equivalent DFA): ");
            String outputFileName = scanner.nextLine();
            nfa1.outputDfa(outputFileName);
            System.out.println("Writing to file: " + outputFileName);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Function that displays the NFA to the console
     */

    public void printNfa(){
        ArrayList<String> output = new ArrayList<>();
        String line = "Q = " + lineBuilder(Q);
        output.add(line);
        if (Sigma != null)
            line = "Sigma_e = " + lineBuilder(Sigma);
        else
            line = "Sigma_e = @";
        output.add(line);
        line = "delta = " + nfaStateBuilder(delta);
        output.add(line);
        line = "s = " + s;
        output.add(line);
        if (F != null)
            line = "F = " + lineBuilder(F);
        else
            line = "F = @";
        output.add(line);
        System.out.println("NFA:");
        for (String s : output)
            System.out.println(s);
    }

    /**
     * Function that takes a Set and returns its String representation
     *
     * @param set Set to be printed
     * @return String representation of set
     */

    public String lineBuilder(Set<String> set){
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

    public String nfaStateBuilder(Map<String, List<String>> map){
        String line = "{";
        int count = 0;
        int size = map.size();
        for (String s : map.keySet()) {
            count++;
            line += "'" + s + "': [";
            List<String> list = map.get(s);
            for (int i = 0; i < list.size(); i++){
                line += "'" + list.get(i) + "'";
                if (i + 1 < list.size())
                    line += ", ";
            }
            if (count < size)
                line += "], ";
            else
                line += "]";
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

    public String dfaStateBuilder(Map<String, String> map){
        String line = "{";
        int count = 0;
        int size = map.size();
        for (String s : map.keySet()) {
            count++;
            line += "'" + s + "': " + "'" + map.get(s) + "'";
            if (count < size)
                line += ", ";
        }
        line += "}";
        return line;
    }

    /**
     * Function that creates the specified NFAs equivalent DFA
     */

    public void convertNfa(){
        Set<Set<String>> states = dfaStates(Q);
        String state;
        for (Set<String> set: states){
            List<String> stateList = new ArrayList<>(set);
            Collections.sort(stateList);
            state = String.join("-", stateList);
            Q_.add(state);
        }

        if (Q_.contains("")){
            Q_.remove("");
            Q_.add("@");
        }
        s_ = E(s);
        alphabetBuilder();
        F_ = acceptStates(F, Q_);
        transitionBuilder();
    }

    /**
     * Function that takes the set of NFA states and returns the set of states
     * for the equivalent DFA
     *
     * @param nfaStates Set of states in the NFA
     * @return Set of states in the equivalent DFA
     */

    public Set<Set<String>> dfaStates(Set<String> nfaStates){
        Set<Set<String>> states = new HashSet<>();
        if (nfaStates.isEmpty()){
            states.add(new HashSet<>());
            return states;
        }
        List<String> list = new ArrayList<>(nfaStates);
        String head = list.get(0);
        Set<String> rest = new HashSet<>(list.subList(1, list.size()));
        for (Set<String> set : dfaStates(rest)){
            Set<String> newSet = new HashSet<>();
            newSet.add(head);
            newSet.addAll(set);
            states.add(newSet);
            states.add(set);
        }
        return states;
    }

    /**
     * Function that returns a state representing all reachable states from a
     * specified state using an epsilon transition
     *
     * @param state a state in the NFA
     * @return all reachable states from the specified state
     */

    public String E(String state){
        String result = state;
        String key = state + ",.";
        if (delta.containsKey(key)){
            List<String> list = delta.get(key);
            for (String s: list) {
                result += " " + E(s);
            }
            String[] lineSplit = result.split("\\s+");
            result = String.join("-", lineSplit);
        }
        return result;
    }

    /**
     * Function that returns the set of accept states for the DFA
     *
     * @param nfaStates set of accept states for the NFA
     * @param dfaStates set of states for the DFA
     * @return set of accept states for the DFA
     */

    public Set<String> acceptStates(Set<String> nfaStates, Set<String> dfaStates){
        Set<String> accept = new HashSet<>();
        if (nfaStates != null) {
            for (String s : nfaStates) {
                for (String s1 : dfaStates) {
                    if (s1.contains(s) || s1.equals(s))
                        accept.add(s1);
                }
            }
        }
        return accept;
    }

    /**
     * Function that builds the alphabet for the DFA
     */

    public void alphabetBuilder(){
        Sigma_ = Sigma;
        if (Sigma_ != null)
            Sigma_.remove(".");
    }

    /**
     * Function that constructs the transition table for the DFA
     */

    public void transitionBuilder(){
        LinkedList<String> toVisit = new LinkedList<>();
        toVisit.add(s_);
        Set<String> visited = new HashSet<>();
        String currentState, newState;
        while(!toVisit.isEmpty()){
            currentState = toVisit.remove();
            visited.add(currentState);
            if (Sigma_ != null){
                for (String s: Sigma_){
                    newState = getNextState(currentState, s);
                    String key = currentState + "," + s;
                    delta_.put(key, newState);
                    if (!visited.contains(newState) && !toVisit.contains(newState))
                        toVisit.add(newState);
                }
            }
        }
        Set<String> tempQ = new HashSet<>();
        Set<String> tempF = new HashSet<>();
        List<String> list = new ArrayList<>(visited);
        Collections.sort(list);
        for (String s: list){
            tempQ.add(s);
            if (F_.contains(s))
                tempF.add(s);
        }
        Q_ = tempQ;
        F_ = tempF;
    }

    /**
     * Function that takes a state and a symbol and determines the next state
     *
     * @param current the current state
     * @param symbol the symbol passed to the FA
     * @return the next state of the FA
     */

    public String getNextState(String current, String symbol) {
        Set<String> stateSet = new HashSet<>();
        String[] states = current.split("-+");
        for (String s : states) {
            String key = s + "," + symbol;
            if (delta.containsKey(key)) {
                List<String> stateList = delta.get(key);
                for (String s1 : stateList) {
                    String[] splitList = E(s1).split("-+");
                    Collections.addAll(stateSet, splitList);
                }
            }
        }
        List<String> list = new ArrayList<>(stateSet);
        Collections.sort(list);
        if (list.isEmpty())
            return "@";
        else
            return String.join("-", list);
    }

    /**
     * Function that displays the DFA
     */

    public void printDfa(){
        List<String> output = new ArrayList<>();
        String line = "Q_ = " + lineBuilder(Q_);
        output.add(line);
        if (Sigma != null)
            line = "Sigma_ = " + lineBuilder(Sigma_);
        else
            line = "Sigma_ = @";
        output.add(line);

        line = "delta_ = " + dfaStateBuilder(delta_);
        output.add(line);
        line = "s_ = " + s_;
        output.add(line);
        if (F_ != null)
            line = "F_ = " + lineBuilder(F_);
        else
            line = "F_ = @";
        output.add(line);
        System.out.println("DFA:");
        for (String s : output)
            System.out.println(s);
    }

    /**
     * Function that outputs the equivalent DFA to a specified file
     *
     * @param outputFileName specified file name
     */

    public void outputDfa(String outputFileName){
        File file = new File(outputFileName);
        FileWriter fr = null;

        try{
            fr = new FileWriter(file);
            String output = fileOutput(outputFileName);
            fr.write(output);
        } catch(IOException e){
            e.printStackTrace();
        } finally {
            try{
                fr.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Function that formats the output being written to the output file
     *
     * @param outputName output file name
     * @return formatted output
     */

    public String fileOutput(String outputName){
        String output = "# File: " + outputName + "\n# DFA";
        output += "\n# Q_ - the set of states";
        output += "\n" + String.join(" ", Q_);
        output += "\n# Sigma_ - the alphabet";
        if (Sigma_ != null)
            output += "\n" + String.join(" ", Sigma_);
        else
            output += "\n@";
        output += "\n# q_0_ - the start state";
        output += "\n" + s_;
        output += "\n# F_ - the set of accept states";
        if (F_.isEmpty())
            output += "\n@";
        else
            output += "\n" + String.join(" ", F_);
        output += "\n# delta_ - the transition function";
        for (String key: delta_.keySet()){
            String[] splitList = key.split(",");
            List<String> list = new ArrayList<>();
            Collections.addAll(list, splitList);
            list.add(delta_.get(key));
            output += "\n" + String.join(" ", list);
        }
        return output;
    }
}