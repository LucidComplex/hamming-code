import java.math.BigInteger;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 * Created by tan on 3/21/16.
 */
public class Hamming {
    private int checkBitCount;

    public static void main(String args[]) {
        Hamming hamming = new Hamming();
        hamming.start();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input data: ");
        String data = scanner.nextLine();
        while (!data.matches("[01]+")) {
            System.out.println("Wrong input, try again.");
            System.out.print("Input data: ");
            data = scanner.nextLine();
        }
        System.out.println("Select parity:");
        System.out.println("  1. Even");
        System.out.println("  2. Odd");
        String parityInput = scanner.nextLine();
        while (!parityInput.matches("[12]+")) {
            System.out.println("Select parity:");
            System.out.println("  1. Even");
            System.out.println("  2. Odd");
            System.out.print("Input: ");
            parityInput = scanner.nextLine();
        }
        Parity parity;
        if (parityInput.equals("1")) {
            parity = Parity.EVEN;
        } else {
            parity = Parity.ODD;
        }
        calculateCodeWord(data, parity);
    }

    private void calculateCodeWord(String data, Parity parity) {
        String tempCodeWord = insertTempParityBits(data);
        String codeWord = replaceParityBits(tempCodeWord, parity);
        System.out.println(codeWord);
    }

    /*
    Inserts X's in place of parity bits
     */
    private String insertTempParityBits(String data) {
        checkBitCount = (int) (Math.log(data.length()) / Math.log(2)) + 1;
        int counter = 0;
        StringBuilder builder = new StringBuilder(data);
        while (counter < checkBitCount) {
            builder.insert((int) Math.pow(2, counter++) - 1, 'X');
        }
        return builder.toString();
    }

    private String replaceParityBits(String data, Parity parity) {
        while(data.contains("X")) {
            int startIndex = data.indexOf('X');
            String replacementBit = calculateCheckBit(data, startIndex, parity);
            data = data.replaceFirst("X", replacementBit);
        }
        return data;
    }

    private String calculateCheckBit(String data, int startIndex, Parity parity) {
        int skip = startIndex + 1;
        boolean use = true;
        char[] bits = new char[checkBitCount * 2];
        int j = 0;
        for (int i = startIndex, counter = 1; i < data.length(); i++, counter++) {
            if (use) {
                bits[j++] = data.charAt(i);
            }
            if (counter % skip == 0) {
                use = !use;
            }
        }
        int counter = 0;
        for (char bit : bits) {
            if (Character.isDigit(bit)) {
                if (bit == '1') {
                    counter++;
                }
            }
        }
        int p = 0;
        switch (parity) {
            case EVEN:
                p = 0;
                break;
            case ODD:
                p = 1;
                break;
        }
        if (counter % 2 == p) {
            return "0";
        } else {
            return "1";
        }
    }
}

enum Parity {
    EVEN, ODD
}
