package enigma;

import java.util.ArrayList;


import java.util.Collection;



/** Class that represents a complete enigma machine.
 *  @author Frederick Fan
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;
        _rotors = allRotors;

    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;

    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;

    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */

    void insertRotors(String[] rotors) {
        rotorMapping = new ArrayList<Rotor>(_numRotors);
        for (int i = 0; i < rotors.length; i++) {
            for (Rotor rotor: _rotors) {
                if (rotor.name().equalsIgnoreCase(rotors[i])) {
                    rotor.set(0);
                    rotorMapping.add(rotor);
                }
            }
        }
        if (rotorMapping.size() != numRotors()) {
            throw new EnigmaException("Number of Rotors no match");
        }

    }
    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 1; i < rotorMapping.size(); i++) {
            if (!_alphabet.contains(setting.charAt(i - 1))) {
                throw new EnigmaException("Initial setting out of bound");
            }
            rotorMapping.get(i).set(setting.charAt(i - 1));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;

    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {

        boolean[] checkNotch = new boolean[numRotors()];
        for (int i = _numRotors - 1; i >= 0; i--) {
            if (rotorMapping.get(i).atNotch()) {
                checkNotch[i] = true;
            }
        }

        Rotor first = rotorMapping.get(_numRotors - 1);
        first.advance();
        for (int i = _numRotors - 1; i >= 1; i--) {
            Rotor rotorFront = rotorMapping.get(i);
            Rotor rotorBehind = rotorMapping.get(i - 1);

            if (checkNotch[i] && rotorBehind.rotates()) {
                rotorBehind.advance();
                if (!rotorFront.equals(first)) {
                    rotorFront.advance();
                }
            }
        }


        int transProcess = c;
        int checkRange = _plugboard.getCycle().indexOf(_alphabet.toChar(c));
        if (_plugboard != null
                && checkRange != -1) {
            transProcess = _plugboard.permute(c);
        }

        for (int i = _numRotors - 1; i >= 0; i--) {
            Rotor ins = rotorMapping.get(i);
            transProcess = rotorMapping.get(i).convertForward(transProcess);
        }


        for (int j = 1; j < _numRotors; j++) {
            Rotor ins1 = rotorMapping.get(j);
            transProcess = rotorMapping.get(j).convertBackward(transProcess);
        }


        char f = _alphabet.toChar(transProcess);
        int checkRange1 = _plugboard.getCycle().indexOf(f);
        if (_plugboard != null
                && checkRange1 != -1) {
            transProcess = _plugboard.permute(transProcess);
        }

        return transProcess;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        char[] messages = new char[msg.length()];
        for (int i = 0; i < messages.length; i++) {
            messages[i] = msg.charAt(i);
        }


        for (int j = 0; j < messages.length; j++) {
            int c = convert(_alphabet.toInt(messages[j]));
            messages[j] = _alphabet.toChar(c);
        }

        String transMsg = new String(messages);

        return transMsg;
    }
    /** Returns rotorMapping. */
    ArrayList<Rotor> rotorMapping() {
        return rotorMapping;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors. **/
    private Permutation _plugboard;

    /** Total number of rotors. */
    private int _numRotors;

    /** Total number of pawls. */
    private int _numPawls;

    /** All possible rotors that can be used. */
    private Collection<Rotor> _rotors;

    /** The arraylist of rotors that are available to use. */
    private ArrayList<Rotor> rotorMapping = new ArrayList<Rotor>(_numRotors);

}
