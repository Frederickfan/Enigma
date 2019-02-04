package enigma;

import static enigma.EnigmaException.*;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Frederick Fan
 */
class MixedAlphabet extends Alphabet {

    /** An alphabet consisting of all CHARS. */
    MixedAlphabet(String chars) {
        _chars = chars;
    }

    @Override
    int size() {
        return _chars.length();
    }

    @Override
    boolean contains(char ch) {
        for (int i = 0; i < size(); i++) {
            if (_chars.charAt(i) == ch) {
                return true;
            }
        }
        return false;
    }

    @Override
    char toChar(int index) {
        if (index >= size() || index < 0) {
            throw error("character index out of range");
        }
        return _chars.charAt(index);
    }

    @Override
    int toInt(char ch) {
        for (int j = 0; j < size(); j++) {
            if (_chars.charAt(j) == ch) {
                return j;
            }
        }
        throw error("character out of range");
    }

    /** character that contains the alphabet. */
    private String _chars;
}

