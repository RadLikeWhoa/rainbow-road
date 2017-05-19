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
public class Code {

    private int chainLength = 2000;
    private int passwords = 2000;
    private int passwordLength = 7;

    private char[] characters = new char[] {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private HashMap<String, String> matches = new HashMap<>(passwords);

    public Code() {
        generateTable();
    }

    private void generateTable() {
        int i = 0;

        while (i < passwords) {
            int j = 0;

            String source = "";
            String result = source;

            while (j < chainLength) {
                BigInteger hashed = h(result);
                result = reduce(hashed, j);
                j++;
            }

            matches.put(result, source);
            i++;
        }
    }

    // Hash function
    public BigInteger h(String input) {
        MessageDigest md;
        byte[] output;
        try {
            md = MessageDigest.getInstance("MD5");
            output = md.digest(input.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < output.length; i++) {
                sb.append(Integer.toString((output[i] & 0xff) + 0x100, 16).substring(1));
            }
            // System.out.println(sb.toString());
            return new BigInteger(output);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return BigInteger.ZERO;
    }

    public String reduce(BigInteger hashed, int level) {
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

    public String findOrigin(BigInteger input) {
        int c = 0;

        while (0 < chainLength) {
            String r = reduce(input, c);

            if (!matches.containsKey(r)) {
                c++;
            } else {
                return matches.get(r);
            }
        }

        return "";
    }

    public String findPlain(String input, BigInteger targetHash) {
        String match = input;
        BigInteger current = h(input);
        int c = 0;

        while (!current.equals(targetHash)) {
            match = reduce(current, c++);
            current = h(match);
        }

        return match;
    }

}
