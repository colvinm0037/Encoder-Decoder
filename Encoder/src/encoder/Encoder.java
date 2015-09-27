package encoder;

//Micah Colvin
//Reed Solomon Encoder
//For Modern Algebra II
//5/1/2014

//Notes: Asks user for name of file to open
//     Outputs encoded message to output.enc

import java.util.Scanner;
import java.io.*;

public class Encoder {
	public static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ. $";
	public String fileName;
	public static int modulos = 29;
	public static String message = "";
	public static int[] coeffecients = new int[100000];
	public static int[] encoded = new int[100000];
	public static char[] encodedMsg = new char[100000];
	public static int start = 0;
	public static int msgs = 0;
	public static final int MY_ALPHABET_LENGTH = 27;

	// This asks user for a file to open, stores entire line from file into
	// message, then adds $'s so message is a factor of 27
	public void getFileInput() throws IOException {
		Scanner S = new Scanner(System.in);
		System.out
				.println("Please enter name of file to encode[Ex: message.txt]: ");
		fileName = S.nextLine();

		Scanner in = new Scanner(new BufferedReader(new FileReader(fileName)));
		message = in.nextLine();
		in.close();

		int extraspace = 27 - (message.length() % MY_ALPHABET_LENGTH);
		for (int i = 0; i < extraspace; i++)
			message = message + '$';

		msgs = message.length() / MY_ALPHABET_LENGTH;
	}

	// This converts each character of message to it's corresponding number and
	// places it in coeffecients[]
	public void getCoeffecients() {
		for (int i = 0; i < message.length(); i++)
			coeffecients[i] = alphabet.indexOf(message.charAt(i));
	}

	// This is the main function that converts each coeffecient using the
	// Berlekamp-Welch method
	public void encodeCoeffecients() {
		for (int j = 0; j < msgs; j++) {
			for (int t = 0; t < 29; t++) {

				int value = coeffecients[0 + start - (2 * j)];
				for (int i = 1; i < MY_ALPHABET_LENGTH; i++)
					value = (value * t + coeffecients[i + start - (2 * j)])
							% modulos;
				encoded[t + start] = value;
			}
			start = start + 29;
		}
	}

	// This converts all the encoded coeffecients into their corresponding
	// letters
	public void encodeMessage() {
		for (int i = 0; i < message.length() + 2 * msgs; i++)
			encodedMsg[i] = alphabet.charAt(encoded[i]);
	}

	// This prints all relevent information
	public void printEverything() {
		System.out.println("Opened file " + fileName);
		System.out.println("Original Message: " + message);
		System.out.print("Original Coeffecients: ");

		for (int i = 0; i < message.length(); i++)
			System.out.print(coeffecients[i] + " ");

		System.out.println();
		System.out.print("Encoded Coeffecients: ");

		for (int i = 0; i < message.length() + 2 * msgs; i++)
			System.out.print(encoded[i] + " ");
		System.out.print("\nEncoded Message: ");

		for (int i = 0; i < start; i++)
			System.out.print(encodedMsg[i]);

		System.out.println("\nLength of original msg: " + message.length()
				+ " Length of encoded msg: " + start);
	}

	// This writes encoded message to output.enc
	public void writeFile() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("output.enc"));
		for (int i = 0; i < message.length() + 2 * msgs; i++)
			writer.write(encodedMsg[i]);
		writer.close();
		System.out.print("Encoded Message outputed to file output.enc");
	}

	public static void main(String[] args) throws IOException {
		Encoder M = new Encoder();
		M.getFileInput();
		M.getCoeffecients();
		M.encodeCoeffecients();
		M.encodeMessage();
		M.printEverything();
		M.writeFile();
	}
}