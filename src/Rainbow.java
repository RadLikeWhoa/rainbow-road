import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sacha Schmid
 * @author Tobias Baumgartner
 * @author Rinesch Murugathas
 */
public class Rainbow {

    private String selectedHash = "1d56a37fb6b08aa709fe90e12ca59e12";

    private int chainLength = 2000;
    private int passwords = 2000;
    private int passwordLength = 7;

    private MessageDigest md = MessageDigest.getInstance("MD5");

    private char[] characters = new char[] {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private HashMap<String, String> matches = new HashMap<>(passwords);

    private Rainbow() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        generateTable();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        System.out.println("TASK: Generating table...");
        Rainbow test = new Rainbow();
        System.out.println("DONE: Table generated...");

        BigInteger hash = new BigInteger(test.selectedHash, 16);
        System.out.println("DONE: Plaintext is " + test.find(hash));
    }

    // generate table for the first 2000 7-character passwords

    private void generateTable() throws UnsupportedEncodingException {
        int i = 0;

        while (i < passwords) {
            int j = 0;

            boolean check = false;

            String source = generatePassword(i);
            String result = source;

            // go through the chain, hashing and reducing at each step of the chain

            while (j < chainLength) {
                BigInteger hashed = hash(result);
                result = reduce(hashed, j);
                j++;

                // check if the hash is included in the table, if it is not, the hash
                // cannot be cracked using the table

                if (hashed.toString(16).equals(selectedHash)) {
                    System.out.println("INFO: Hash included... ");
                    check = true;
                }
            }

            if (check) {
                System.out.println("INFO: Hash is included in chain " + source + " => " + result);
            }

            // remember the last and first element of the chain
            // this is reversed to make lookups easier later on

            matches.put(result, source);
            i++;
        }
    }

    // generate a password based on a number

    private String generatePassword(int num) {
        StringBuilder s = new StringBuilder("0000000");

        for (int pos = passwordLength - 1; pos >= 0 && num > 0 ; pos--) {
            char digit = characters[num % characters.length];
            s.setCharAt(pos, digit);
            num = num / characters.length;
        }

        return s.toString();
    }

    // hash a string using md5

    private BigInteger hash(String input) throws UnsupportedEncodingException {
        return new BigInteger(1, md.digest(input.getBytes("UTF-8")));
    }

    // reduce a string following the algorithm provided in class

    private String reduce(BigInteger hashed, int level) {

        // increment the hash by the current level (H + Stufe)

        hashed = hashed.add(BigInteger.valueOf(level));

        List<BigInteger> results = new ArrayList<>();

        for (int i = 1; i <= passwordLength; i++) {

            // calculate the current character index and the next hash (ri, H)

            results.add(0, hashed.mod(BigInteger.valueOf(characters.length)));
            hashed = hashed.divide(BigInteger.valueOf(characters.length));
        }

        // map the character indices to the actual characters and
        // return the reduced value

        return results.stream()
                .map(r -> characters[r.intValue()])
                .map(String::valueOf)
                .collect(Collectors.joining(""));
    }

    // step through the chain to see if we find any reduced values that
    // are final values of a chain
    // if we have an origin, we can start going through its chain to look
    // for the plaintext

    private String find(BigInteger input) throws UnsupportedEncodingException {
        int c = chainLength - 1;

        System.out.println("TASK: Looking for origin...");

        while (c >= 0) {
            int x = c;
            BigInteger current = input;
            String r = reduce(current, x);

            while (x < chainLength) {
                r = reduce(current, x);
                current = hash(r);
                x += 1;
            }

            // keep going if the reduced value is not a final value of a chain

            if (!matches.containsKey(r)) {
                c -= 1;
            } else {
                return findPlain(matches.get(r), input);
            }
        }

        // we cannot crack the hash if we are unable to find a chain
        // origin for the hash, i.e. the hash is not included

        throw new InternalError("No origin found for hash");
    }

    // step through a chain to find the plaintext for a hash value

    private String findPlain(String input, BigInteger targetHash) throws UnsupportedEncodingException {
        System.out.println("DONE: Origin found: " + input);
        System.out.println("TASK: Looking for plaintext for hash '" + selectedHash + "'...");

        String match = input;
        BigInteger current = hash(input);
        int c = 0;

        // attempt to find the targetHash anywhere in the chain

        while (c < chainLength && !current.equals(targetHash)) {
            match = reduce(current, c++);
            current = hash(match);
        }

        // if we've stepped through the entire chain that means that
        // the hash is not included

        if (c == chainLength) {
            throw new InternalError("No plaintext found for hash");
        }

        return match;
    }

}
