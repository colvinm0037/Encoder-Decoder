package encoder;

//Micah Colvin
//Reed Solomon Encoder
//For Modern Algebra II
//5/1/2014

//Notes: Asks user for name of file to open outputs encoded message to output.enc

import java.util.Scanner;
import java.io.*;

public class Encoder {
	
	private static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ. $";
	private String fileName;
	private static int modulos = 29;
	private static String message = "";
	private static int[] coeffecients = new int[100000];
	private static int[] encoded = new int[100000];
	private static char[] encodedMsg = new char[100000];
	private static int start = 0;
	private static int msgs = 0;
	private static final int ALPHABET_LENGTH = 27;

	// This asks user for a file to open, stores entire line from file into
	// message, then adds $'s so message is a factor of 27
	private void getFileInput() throws IOException {
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter name of file to encode[Ex: message.txt]: ");
		fileName = scanner.nextLine();
		
		scanner = new Scanner(new BufferedReader(new FileReader(fileName)));
		message = scanner.nextLine();
		scanner.close();

		int extraspace = ALPHABET_LENGTH - (message.length() % ALPHABET_LENGTH);
		for (int i = 0; i < extraspace; i++) {
			message = message + '$';
		}

		msgs = message.length() / ALPHABET_LENGTH;
	}

	// This converts each character of message to it's corresponding number and
	// places it in coeffecients[]
	private void getCoeffecients() {
		
		for (int i = 0; i < message.length(); i++) {
			coeffecients[i] = alphabet.indexOf(message.charAt(i));
		}
	}

	// This is the main function that converts each coeffecient using the
	// Berlekamp-Welch method
	private void encodeCoeffecients() {
		
		for (int j = 0; j < msgs; j++) {		
			for (int t = 0; t < 29; t++) {
				int value = coeffecients[0 + start - (2 * j)];
				for (int i = 1; i < ALPHABET_LENGTH; i++) {
					value = (value * t + coeffecients[i + start - (2 * j)])	% modulos;
				}
				encoded[t + start] = value;
			}
			start = start + 29;
		}
	}

	// This converts all the encoded coeffecients into their corresponding
	// letters
	private void encodeMessage() {
		
		for (int i = 0; i < message.length() + 2 * msgs; i++) {
			encodedMsg[i] = alphabet.charAt(encoded[i]);
		}
	}

	// This prints all relevant information
	private void printEverything() {
		
		System.out.println("Opened file " + fileName);
		System.out.println("Original Message: " + message);
		System.out.print("Original Coeffecients: ");

		for (int i = 0; i < message.length(); i++) {
			System.out.print(coeffecients[i] + " ");
		}
			
		System.out.println();
		System.out.print("Encoded Coeffecients: ");

		for (int i = 0; i < message.length() + 2 * msgs; i++) {
			System.out.print(encoded[i] + " ");
		}
		
		System.out.print("\nEncoded Message: ");

		for (int i = 0; i < start; i++)
			System.out.print(encodedMsg[i]);

		System.out.println("\nLength of original msg: " + message.length()
				+ " Length of encoded msg: " + start);
	}

	// This writes encoded message to output.enc
	private void writeFile() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("output.enc"));
		for (int i = 0; i < message.length() + 2 * msgs; i++)
			writer.write(encodedMsg[i]);
		writer.close();
		System.out.print("Encoded Message outputed to file output.enc");
	}

	private static void main(String[] args) throws IOException {
		Encoder M = new Encoder();
		M.getFileInput();
		M.getCoeffecients();
		M.encodeCoeffecients();
		M.encodeMessage();
		M.printEverything();
		M.writeFile();
	}
}