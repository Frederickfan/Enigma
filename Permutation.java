package enigma;



/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Frederick Fan
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */


    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;

    }
    /**get cycle method and @return cycle. */
    public String getCycle() {
        return _cycles;
    }
    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles += cycle;

    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();

    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int pWrap = wrap(p);
        char pChar = _alphabet.toChar(pWrap);
        char pPermute = permute(pChar);
        int pReturn = _alphabet.toInt(pPermute);
        return pReturn;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int cWrap = wrap(c);
        char cChar = _alphabet.toChar(cWrap);
        int cInvert = invert(cChar);
        int cReturn = _alphabet.toInt((char) cInvert);
        return cReturn;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */

    char permute(char p) {
        char pstart = '(';
        char result = '*';
        if (_cycles.equals("")) {
            return p;
        } else {
            for (int i = 0; i < _cycles.length(); i++) {
                if (_cycles.charAt(i) == '(') {
                    pstart = _cycles.charAt(i + 1);
                } else if (p == _cycles.charAt(i)
                        && _cycles.charAt(i + 1) != ')') {
                    result = _cycles.charAt(i + 1);
                } else if (p == _cycles.charAt(i)
                        && _cycles.charAt(i + 1) == ')') {
                    result = pstart;
                }
            }
        }
        return result;
    }


    /** Return the result of applying the inverse of this permutation to C. */
    int invert(char c) {
        char cend = ')';
        char result = '*';

        if (_cycles.equals("")) {
            return c;
        } else {
            for (int i = _cycles.length() - 1; i > 0; i--) {
                if (_cycles.charAt(i) == ')') {
                    cend = _cycles.charAt(i - 1);
                } else if (c == _cycles.charAt(i)
                        && _cycles.charAt(i - 1) != '(') {
                    result = _cycles.charAt(i - 1);
                } else if (c == _cycles.charAt(i)
                        && _cycles.charAt(i - 1) == '(') {
                    result = cend;
                }
            }
        }
        return result;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 1; i < _cycles.length() - 1; i++) {
            char left = _cycles.charAt(i - 1);
            char right = _cycles.charAt(i + 1);
            if (left == '(' && right == ')') {
                return true;
            }
        }
        return false;

    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** cycles for the perm. */
    private String _cycles;

}
