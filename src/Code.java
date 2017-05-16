import java.math.BigInteger;
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

            matches.put(source, result);
            i++;
        }
    }

    public BigInteger h(String input) {
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

}
