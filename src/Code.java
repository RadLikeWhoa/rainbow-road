import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Sabakuno on 16.05.17.
 */
public class Code {

    // Kettenlänge
    public int k = 2000;

    // Hashfunktion
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
            System.out.println(sb.toString());
            return new BigInteger(output);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return BigInteger.ZERO;
    }

}
