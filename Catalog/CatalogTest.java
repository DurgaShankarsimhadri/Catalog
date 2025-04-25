import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.JSONObject;

public class CatalogTest{

    public static void main(String[] args) throws Exception {
        String filePath1 = "testcase1.json";  // Replace with your actual file path
        String filePath2 = "testcase2.json";  // Replace with your actual file path

        JSONObject json1 = new JSONObject(new String(Files.readAllBytes(Paths.get(filePath1))));
        JSONObject json2 = new JSONObject(new String(Files.readAllBytes(Paths.get(filePath2))));

        BigInteger secret1 = findSecret(json1);
        BigInteger secret2 = findSecret(json2);

        System.out.println("Secret 1: " + secret1);
        System.out.println("Secret 2: " + secret2);
    }

    private static BigInteger findSecret(JSONObject json) {
        int k = json.getJSONObject("keys").getInt("k");

        List<BigInteger> xValues = new ArrayList<>();
        List<BigInteger> yValues = new ArrayList<>();

        for (String key : json.keySet()) {
            if (key.equals("keys")) continue;

            int x = Integer.parseInt(key);
            JSONObject valueObj = json.getJSONObject(key);
            int base = Integer.parseInt(valueObj.getString("base"));
            String value = valueObj.getString("value");

            BigInteger y = new BigInteger(value, base);

            xValues.add(BigInteger.valueOf(x));
            yValues.add(y);

            if (xValues.size() == k) break;
        }

        return lagrangeInterpolation(BigInteger.ZERO, xValues, yValues);
    }

    // Lagrange interpolation at x = 0 gives constant term 'c'
    private static BigInteger lagrangeInterpolation(BigInteger x, List<BigInteger> xs, List<BigInteger> ys) {
        BigInteger result = BigInteger.ZERO;
        int k = xs.size();

        for (int i = 0; i < k; i++) {
            BigInteger term = ys.get(i);
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger numerator = x.subtract(xs.get(j));
                    BigInteger denominator = xs.get(i).subtract(xs.get(j));
                    term = term.multiply(numerator).divide(denominator);
                }
            }
            result = result.add(term);
        }

        return result;
    }
}
