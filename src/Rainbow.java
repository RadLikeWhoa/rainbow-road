import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sabakuno on 16.05.17.
 */
public class Rainbow {
    
    private final String WORKING_HASH = "99016e4198c78f19eca4ceb724c884ae";
    private final String TEST_HASH = "1d56a37fb6b08aa709fe90e12ca59e12";

    private int chainLength = 2000;
    private int passwords = 2000;
    private int passwordLength = 7;

    private String selectedHash = WORKING_HASH;

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

    private void generateTable() throws UnsupportedEncodingException {
        int i = 0;

        while (i < passwords) {
            int j = 0;

            String source = String.format("%" + passwordLength + "s", Integer.toHexString(i)).replace(' ', '0');
            String result = source;

            while (j < chainLength) {
                BigInteger hashed = h(result);
                result = reduce(hashed, j);
                j++;

                if (hashed.toString(16).equals(selectedHash)) {
                    System.out.println("INFO: Hash included...");
                }
            }

            matches.put(result, source);
            i++;
        }
    }

    // Hash function
    private BigInteger h(String input) throws UnsupportedEncodingException {
        return new BigInteger(1, md.digest(input.getBytes("UTF-8")));
    }

    private String reduce(BigInteger hashed, int level) {
        hashed = hashed.add(BigInteger.valueOf(level));

        List<BigInteger> results = new ArrayList<>();

        for (int i = 1; i <= passwordLength; i++) {
            results.add(0, hashed.mod(BigInteger.valueOf(characters.length)));
            hashed = hashed.divide(BigInteger.valueOf(characters.length));
        }

        return results.stream()
                .map(r -> characters[r.intValue()])
                .map(String::valueOf)
                .collect(Collectors.joining(""));
    }

    private String find(BigInteger input) throws UnsupportedEncodingException {
        int c = 0;

        System.out.println("TASK: Looking for origin...");

        while (c < chainLength) {
            String r = reduce(input, c);

            if (!matches.containsKey(r)) {
                c++;
            } else {
                return findPlain(matches.get(r), input);
            }
        }

        throw new InternalError("No origin found for hash");
    }

    private String findPlain(String input, BigInteger targetHash) throws UnsupportedEncodingException {
        System.out.println("DONE: Origin found: " + input);
        System.out.println("TASK: Looking for plaintext for hash '" + selectedHash + "'...");

        String match = input;
        BigInteger current = h(input);
        int c = 0;

        while (c < chainLength && !current.equals(targetHash)) {
            match = reduce(current, c++);
            current = h(match);
        }

        if (c == chainLength) {
            throw new InternalError("No plaintext found for hash");
        }

        return match;
    }

}
