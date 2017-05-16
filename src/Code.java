import java.util.HashMap;

/**
 * Created by Sabakuno on 16.05.17.
 */
public class Code {

    private int n = 2000;
    private int k = 2000;

    private HashMap<String, String> matches = new HashMap<>(n);

    public Code(int chainLength, int passwords) {
        k = passwords;
        n = chainLength;

        generateTable();
    }

    private void generateTable() {
        int i = 0;

        while (i < n) {
            int j = 0;

            String source = "";
            String result = source;

            while (j < k) {
                String hashed = h(result);
                result = reduce(hashed);
                j++;
            }

            matches.put(source, result);
        }
    }
    
    public String h(String input) {
        return "";
    }

    public String reduce(String hashed) {
        return "";
    }

}
