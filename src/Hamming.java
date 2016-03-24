import java.math.BigInteger;
import java.util.Scanner;
import java.util.concurrent.SynchronousQueue;
import java.util.regex.Matcher;

/**
 * Created by tan on 3/21/16.
 */
public class Hamming {
    private int checkBitCount;
    private Scanner scanner;

    public Hamming() {
        scanner = new Scanner(System.in);
    }

    public static void main(String args[]) {
        if (args.length == 0) {
            System.out.println("Usage: java Hamming create\n   or: java Hamming correct\nCreate or correct a Hamming codeword.");
            return;
        }
        Hamming hamming = new Hamming();
        if (args[0].equals("create")) {
            hamming.startCreate();
        } else if (args[0].equals("correct")) {
            hamming.startCorrect();
        }
    }

    public void startCorrect() {
        System.out.println("Codeword correction");
        String codeword = askInput("codeword");
        Parity parity = askParity();
        int errorPosition = checkForError(codeword, parity);
        if (errorPosition != 0) {
            System.out.print("Corrected codeword: ");
            StringBuilder builder = new StringBuilder(codeword);
            char flip = '0';
            if (codeword.charAt(errorPosition - 1) == '0') {
                flip = '1';
            }
            builder.setCharAt(errorPosition - 1, flip);
            System.out.println(builder);
        }
    }

    private int checkForError(String codeword, Parity parity) {
        checkBitCount = (int) (Math.log(codeword.length()) / Math.log(2)) + 1;
        int errorPosition = 0;
        for (int i = 0; i < checkBitCount; i++) {
            int checkBitIndex = (int) Math.pow(2, i) - 1;
            if (calculateCheckBit(codeword, checkBitIndex, parity).equals("1")) {
                errorPosition += checkBitIndex + 1;
            }
        }
        if (errorPosition == 0) {
            System.out.println("No errors found.");
        } else {
            System.out.println("Error found at position " + errorPosition);
        }
        return errorPosition;
    }


    private Parity askParity() {
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
        return parity;
    }

    private String askInput(String type) {
        System.out.print("Input " + type + ": ");
        String data = scanner.nextLine();
        while (!data.matches("[01]+")) {
            System.out.println("Wrong input, try again.");
            System.out.print("Input " + type + ": ");
            data = scanner.nextLine();
        }
        return data;
    }

    public void startCreate() {
        System.out.println("Codeword creation");
        String data = askInput("data");
        Parity parity = askParity();
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

    /*
    Replaces X's with the right bit set
     */
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
        int j = 0;
        int bitCounter = 0;
        for (int i = startIndex, counter = 1; i < data.length(); i++, counter++) {
            if (use) {
                if (data.charAt(i) == '1') {
                    bitCounter++;
                }
            }
            if (counter % skip == 0) {
                use = !use;
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
        if (bitCounter % 2 == p) {
            return "0";
        } else {
            return "1";
        }
    }
}

enum Parity {
    EVEN, ODD
}
