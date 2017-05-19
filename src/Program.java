import java.math.BigInteger;

/**
 * Created by Sabakuno on 16.05.17.
 */
public class Program {

    public static void main(String[] args) {

        Code test = new Code();

        String origin = test.findOrigin(1d56a37fb6b08aa709fe90e12ca59e12);
        System.out.println(test.findPlain(origin, BigInteger.valueOf(1d56a37fb6b08aa709fe90e12ca59e12)));

    }

}
