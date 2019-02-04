package enigma;



import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Frederick Fan
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine enigma1 = readConfig();

        String next = _input.nextLine();

        while (_input.hasNextLine()) {
            String settings = next;

            if (!next.contains("*")) {
                throw new EnigmaException("Wrong format of message");
            }





            setUp(enigma1, settings);
            if (!enigma1.rotorMapping().get(0).reflecting()) {
                throw new EnigmaException("Reflector first");
            }



            next = _input.nextLine().toUpperCase();
            while (!next.contains("*")) {
                if (!next.isEmpty()) {
                    String concateLine = next.replace(" ", "");
                    String conversion = enigma1.convert(concateLine);
                    printMessageLine(conversion);
                    if (_input.hasNextLine()) {
                        next = _input.nextLine().toUpperCase();
                    } else {
                        break;
                    }
                } else {
                    _output.println();
                    if (_input.hasNextLine()) {
                        next = _input.nextLine().toUpperCase();
                    } else {
                        break;
                    }
                }
            }

        }

    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */




    private Machine readConfig() {
        try {
            String next = _config.nextLine();
            String alphabet = next.replace(" ", "");
            if (alphabet.contains("(")
                    || alphabet.contains(")")
                    || alphabet.contains("*")
                    || alphabet.contains("[0-9]")) {
                throw new EnigmaException("Wrong message format");
            }

            if (next.contains("-")) {
                char q = alphabet.charAt(alphabet.length() - 1);
                _alphabet = new CharacterRange(alphabet.charAt(0), q);
            } else {
                _alphabet = new MixedAlphabet(alphabet);
            }



            if (!_config.hasNextInt()) {
                throw new EnigmaException("Wrong message format");
            }
            int numRotors = _config.nextInt();


            if (!_config.hasNextInt()) {
                throw new EnigmaException("Wrong message format");
            }
            int numPawls = _config.nextInt();


            nextToken = _config.next();
            while (_config.hasNext()) {
                _name = "";
                cycle = "";
                notches = "";
                _allrotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, numPawls, _allrotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }


    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {

            _name = _name.concat(nextToken);


            nextToken = _config.next();
            notches = notches.concat(nextToken);



            nextToken = _config.next();
            while (nextToken.contains("(")) {
                cycle = cycle.concat(nextToken);
                if (cycle.charAt(cycle.length() - 1) != ')') {
                    throw new EnigmaException("Wrong format");
                }
                if (!_config.hasNext()) {
                    break;
                } else {
                    nextToken = _config.next();
                }
            }
            Permutation perm = new Permutation(cycle, _alphabet);



            if (notches.charAt(0) == 'M') {
                return new MovingRotor(_name, perm, notches.substring(1));
            } else if (notches.charAt(0) == 'N') {
                return new FixedRotor(_name, perm);
            } else {
                return new Reflector(_name, perm);
            }

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }



    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] splitsetting = settings.split(" ");
        rotors = new String[M.numRotors()];

        if (splitsetting.length - 1 < M.numRotors()) {
            throw new EnigmaException("Insufficient  settings");
        }

        for (int i = 1; i < M.numRotors() + 1; i++) {
            rotors[i - 1] = splitsetting[i];
        }


        for (int j = 0; j <= rotors.length - 2; j++) {
            for (int k = j + 1; k <= rotors.length - 1; k++) {
                if (rotors[j].equals(rotors[k])) {
                    throw new EnigmaException("Cannot have identical rotors");
                }
            }
        }


        M.insertRotors(rotors);
        M.setRotors(splitsetting[M.numRotors() + 1]);


        String keySwap = "";
        for (int i = rotors.length + 2; i < splitsetting.length; i++) {
            keySwap = keySwap.concat(splitsetting[i] + " ");
        }
        Permutation plugboard = new Permutation(keySwap, _alphabet);
        M.setPlugboard(plugboard);
        String swap = keySwap;

    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        int remainder = msg.length() % 5;
        int divides = msg.length() - remainder;
        int start = 0;
        for (int i = 5; i <= divides; i += 5) {
            _output.print(msg.substring(start, i));
            _output.print(" ");
            start += 5;
        }
        _output.println(msg.substring(divides, remainder + divides));

    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** ArrayList for all the rotors available to use. */
    private ArrayList<Rotor> _allrotors = new ArrayList<Rotor>();

    /** Next string which is the Next token of _config. */
    private String nextToken;

    /** Name of current rotor. */
    private String _name;

    /** String for cycle for readRotor to append. */
    private String cycle;

    /** String for notches for current rotor. */
    private String notches;

    /** Set up rotors that are available to use. */
    private String[] rotors;
}
