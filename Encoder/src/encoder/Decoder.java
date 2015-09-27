package encoder;

//Micah Colvin
//Reed Solomon Decoder
//For Modern Algebra II
//5/1/2014

//NOTES: This reads in from file name "output.enc"
//     The decoded message is outputed to the console but also to a new text file named "decodedMessage.txt"
//     For each block I output what position the error is, and the corresponding Q(t), P(t) and what that block decodes too

import java.util.Scanner;
import java.io.*;

public class Decoder {
	
	private static Decoder M = new Decoder();    
	private static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ. $";
	private static String message = "";  // This is the message that is recieved 
	private static String decodedMessage = "";  // This is the decoded message
	private static int length = 0;
	private static int start = 0;
	private static int alpha = 1;
	private static int[] R = new int[29];
	private static int[] finalSumRi = new int[10000];
	private static int[] finalSumiRi = new int[10000];
	private static int[] errorBlock = new int[10000]; // This tells if there is an error in each block of message, 0 = no error 1 = error
	private static int[] Ei = new int[29];      
	private static int[] RiEi = new int[29];      
	private static int[] RiEiLeading = new int[29];
	private static int[] Q = new int[28];
	private static int[] P = new int[27];
	private static int[][] alteredCArrays = new int[29][29];
	
	// These are all the pre-computed inverses for each value in Z_29, starts at zero for allignment
	public static int[] inverseArray = {0, 1, 15, 10, 22, 6, 5, 25, 11, 13, 3, 8, 17, 9, 27, 2, 20, 12, 21, 26, 16, 18, 4, 24, 23, 7, 19, 14, 28};
	
	// Here are all the pre-computed c-values
	int[] c0 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1};   
	int[] c1 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0};                    // x 
	int[] c2 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,15,14,0};                  // 14*x+15*x^2
	int[] c3 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5,14,10,0};                  // 5*x^3+14*x^2+10*x 
	int[] c4 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,23,7,21,7,0};                  // 23*x^4+7*x^3+21*x^2+7*x
	int[] c5 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,22,12,16,2,6,0};                 // 22*x^5+12*x^4+16*x^3+2*x^2+6*x
	int[] c6 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,23,3,12,16,9,24,0};                // 23*x^6+3*x^5+12*x^4+16*x^3+9*x^2+24*x
	int[] c7 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,24,18,24,21,0,4,25,0};               // 24*x^7+18*x^6+24*x^5+21*x^4+4*x^2+25*x
	int[] c8 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,9,7,7,15,25,18,0};                 // 3*x^8+3*x^7+9*x^6+7*x^5+7*x^4+15*x^3+25*x^2+18*x
	int[] c9 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,10,17,8,25,1,18,12,12,13,0};             // 10*x^9+17*x^8+8*x^7+25*x^6+x^5+18*x^4+12*x^3+12*x^2+13*x
	int[] c10= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,13,0,4,24,27,14,2,5,26,0};               // x^10+13*x^9+4*x^7-5*x^6-2*x^5+14*x^4+2*x^3+5*x^2-3*x
	int[] c11= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,24,4,3,17,7,11,27,25,11,8,0};              // 8*x^11-5*x^10+4*x^9+3*x^8-12*x^7+7*x^6+11*x^5-2*x^4-4*x^3+11*x^2+8*x
	int[] c12= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,20,14,17,28,18,14,9,26,16,7,22,12,0};          //-9*x^12+14*x^11-12*x^10-x^9-11*x^8+14*x^7+9*x^6-3*x^5-13*x^4+7*x^3-7*x^2+12*x
	int[] c13= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,25,4,11,9,9,19,16,4,17,22,23,9,0};             // 6*x^13-4*x^12+4*x^11+11*x^10+9*x^9+9*x^8-10*x^7-13*x^6+4*x^5-12*x^4-7*x^3-6*x^2+9*x
	int[] c14= {0,0,0,0,0,0,0,0,0,0,0,0,0,17,19,4,24,7,13,22,27,2,12,21,4,0,2,0};            //-12*x^14-10*x^13+4*x^12-5*x^11+7*x^10+13*x^9-7*x^8-2*x^7+2*x^6+12*x^5-8*x^4+4*x^3+2*x
	int[] c15= {0,0,0,0,0,0,0,0,0,0,0,0,5,26,27,23,9,4,28,18,2,26,25,0,4,4,2,0};             // 5*x^15-3*x^14-2*x^13-6*x^12+9*x^11+4*x^10-x^9-11*x^8+2*x^7-3*x^6-4*x^5+4*x^3+4*x^2+2*x
	int[] c16= {0,0,0,0,0,0,0,0,0,0,0,13,6,19,16,8,19,27,22,5,7,8,11,22,11,0,9,0};           // 13*x^16+6*x^15-10*x^14-13*x^13+8*x^12-10*x^11-2*x^10-7*x^9+5*x^8+7*x^7+8*x^6+11*x^5-7*x^4+11*x^3+9*x
	int[] c17= {0,0,0,0,0,0,0,0,0,0,11,12,4,24,11,26,11,10,12,23,28,17,8,26,5,21,12,0};      // 11*x^17+12*x^16+4*x^15-5*x^14+11*x^13-3*x^12+11*x^11+10*x^10+12*x^9-6*x^8-x^7-12*x^6+8*x^5-3*x^4+5*x^3-8*x^2+12*x
	int[] c18= {0,0,0,0,0,0,0,0,0,28,8,5,4,15,12,26,24,17,27,4,18,15,10,16,19,5,8,0};        //-x^18+8*x^17+5*x^16+4*x^15-14*x^14+12*x^13-3*x^12-5*x^11-12*x^10-2*x^9+4*x^8-11*x^7-14*x^6+10*x^5-13*x^4-10*x^3+5*x^2+8*x
	int[] c19= {0,0,0,0,0,0,0,0,3,9,11,26,26,20,19,27,27,25,25,17,28,26,28,24,25,14,26,0};   // 3*x^19+9*x^18+11*x^17-3*x^16-3*x^15-9*x^14-10*x^13-2*x^12-2*x^11-4*x^10-4*x^9-12*x^8-x^7-3*x^6-x^5-5*x^4-4*x^3+14*x^2-3*x
	int[] c20= {0,0,0,0,0,0,0,19,15,21,1,23,14,24,21,25,22,21,9,7,24,26,21,6,19,17,13,0};    //-10*x^20-14*x^19-8*x^18+x^17-6*x^16+14*x^15-5*x^14-8*x^13-4*x^12-7*x^11-8*x^10+9*x^9+7*x^8-5*x^7-3*x^6-8*x^5+6*x^4-10*x^3-12*x^2+13*x
	int[] c21= {0,0,0,0,0,0,23,13,24,27,25,5,3,3,24,9,27,26,18,0,6,8,1,9,20,1,18,0};         //-6*x^21+13*x^20-5*x^19-2*x^18-4*x^17+5*x^16+3*x^15+3*x^14-5*x^13+9*x^12-2*x^11-3*x^10-11*x^9+6*x^7+8*x^6+x^5+9*x^4-9*x^3+x^2-11*x
	int[] c22= {0,0,0,0,0,5,5,19,6,7,8,27,21,18,21,19,11,5,25,24,21,28,10,20,6,17,25,0};     // 5*x^22+5*x^21-10*x^20+6*x^19+7*x^18+8*x^17-2*x^16-8*x^15-11*x^14-8*x^13-10*x^12+11*x^11+5*x^10-4*x^9-5*x^8-8*x^7-x^6+10*x^5-9*x^4+6*x^3-12*x^2-4*x
	int[] c23= {0,0,0,0,4,3,20,1,16,5,20,23,16,19,11,5,25,19,20,12,24,14,14,24,24,5,24,0};   // 4*x^23+3*x^22-9*x^21+x^20-13*x^19+5*x^18-9*x^17-6*x^16-13*x^15-10*x^14+11*x^13+5*x^12-4*x^11-10*x^10-9*x^9+12*x^8-5*x^7+14*x^6+14*x^5-5*x^4-5*x^3+5*x^2-5*x
	int[] c24= {0,0,0,5,12,4,28,13,3,19,12,4,6,4,9,18,1,8,20,4,9,21,19,7,5,24,6,0};          // 5*x^24+12*x^23+4*x^22-x^21+13*x^20+3*x^19-10*x^18+12*x^17+4*x^16+6*x^15+4*x^14+9*x^13-11*x^12+x^11+8*x^10-9*x^9+4*x^8+9*x^7-8*x^6-10*x^5+7*x^4+5*x^3-5*x^2+6*x
	int[] c25= {0,0,6,27,13,17,27,12,6,24,13,8,6,0,6,28,4,14,3,0,27,27,18,19,24,12,7,0};     // 6*x^25-2*x^24+13*x^23-12*x^22-2*x^21+12*x^20+6*x^19-5*x^18+13*x^17+8*x^16+6*x^15+6*x^13-x^12+4*x^11+14*x^10+3*x^9-2*x^7-2*x^6-11*x^5-10*x^4-5*x^3+12*x^2+7*x
	int[] c26= {0,27,12,8,6,7,18,11,13,12,9,26,21,27,2,0,19,19,25,20,13,16,18,15,22,1,10,0}; //-2*x^26+12*x^25+8*x^24+6*x^23+7*x^22-11*x^21+11*x^20+13*x^19+12*x^18+9*x^17-3*x^16-8*x^15-2*x^14+2*x^13-10*x^11-10*x^10-4*x^9-9*x^8+13*x^7-13*x^6-11*x^5-14*x^4-7*x^3+x^2+10*x
	int[] c27= {1,26,7,14,2,24,11,6,18,21,17,23,13,2,26,5,20,17,25,7,16,25,9,10,10,8,14,0};  // x^27-3*x^26+7*x^25+14*x^24+2*x^23-5*x^22+11*x^21+6*x^20-11*x^19-8*x^18-12*x^17-6*x^16+13*x^15+2*x^14-3*x^13+5*x^12-9*x^11-12*x^10-4*x^9+7*x^8-13*x^7-4*x^6+9*x^5+10*x^4+10*x^3+8*x^2+14*x
	
	// They are held in this array of arrays
	int[][] cArrays = {c0, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20, c21, c22, c23, c24, c25, c26, c27};      
	//**************************************************************************************

	// Open file and read in entire line and store in String message
	// Automatically set to open file "output.enc"
	private void getFileInput() throws IOException {
		Scanner in = new Scanner(new BufferedReader(
				new FileReader("output.enc")));
		message = in.nextLine();
		in.close();
	}

	// Convert the letters in message into corresponding numbers, stored in R
	private void convert() // We do this in steps instead of all at once
	{
		for (int i = 0; i < 29; i++) {
			R[i] = alphabet.indexOf(message.charAt(i + start));
			// System.out.print(R[i] + " ");
		}
	}

	// This is a custom method that multiplies every element in an array by a
	// given value and stores it in a new array
	private int[] multiplyArray(int value, int[] array) {
		int[] temp = new int[28];
		for (int i = 0; i < 28; i++)
			temp[i] = (array[i] * value) % 29;

		return temp;
	}

	// This finds the final sums of R[i]
	private int findSumRi() {
		int[] sum = new int[29];
		int length = 28;

		for (int i = 0; i < 29; i++)
			sum[i] = R[i];

		// System.out.print("Ri: ");
		// for(int i = 0; i < 29; i++)
		// System.out.print(R[i] + " "); // WAS System.out.print(R[i + start] +
		// " ");

		System.out.println();

		for (int j = 0; j < 28; j++) {
			for (int i = 0; i < length; i++) {
				sum[i] = sum[i + 1] - sum[i];
				sum[i] = M.myMod(sum[i], 29);
				// System.out.print(sum[i] + " ");
			}
			length = length - 1;
			// System.out.println();
		}
		return sum[0];
	}

	// This finds the final sums of iR[i]
	private int findSumiRi() {
		int[] sum = new int[29];
		int length = 28;

		// Find iRi
		int[] iR = new int[29];

		for (int i = 0; i < 29; i++)
			iR[i] = M.myMod((i * R[i]), 29);

		// System.out.print("iRi: ");
		// for(int i = 0; i < 29; i++)
		// System.out.print(iR[i] + " ");
		// System.out.println();

		for (int i = 0; i < 29; i++)
			sum[i] = iR[i];

		for (int j = 0; j < 28; j++) {
			for (int i = 0; i < length; i++) {
				sum[i] = sum[i + 1] - sum[i];
				sum[i] = M.myMod(sum[i], 29);
				// System.out.print(sum[i] + " ");
			}
			length = length - 1;
			// System.out.println();
		}
		return sum[0];
	}

	// This finds the final sums of EiRi[] and stores it in RiEiLeading[]
	private void findSums(int[] myArray) // This is for RiEi
	{
		int[] sum = myArray;
		int length = 28;

		/*
		 * System.out.print("Ri: "); // Delete Later for(int i = 0; i < 29; i++)
		 * System.out.print(R[i] + ", "); System.out.println();
		 * 
		 * System.out.print("Ei: "); // Delete Later for(int i = 0; i < 29; i++)
		 * System.out.print(Ei[i] + ", "); System.out.println("\n");
		 * 
		 * System.out.print("RiEi: "); for(int i = 0; i < 29; i++)
		 * System.out.print(myArray[i] + ", "); System.out.println();
		 */

		RiEiLeading[0] = myArray[0];

		for (int j = 0; j < 28; j++) {
			for (int i = 0; i < length; i++) {
				sum[i] = sum[i + 1] - sum[i];
				sum[i] = M.myMod(sum[i], 29);
				// System.out.print(sum[i] + " ");
			}
			RiEiLeading[j + 1] = sum[0];
			length--;
		}
	}

	// This calls the other methods to get the finalSums of Ri and iRi
	private void getRiAndiRi(int i) {
		finalSumRi[i] = M.findSumRi();
		finalSumiRi[i] = M.findSumiRi();
	}

	// This prints out a list that shows what blocks have an error
	private void checkForError() {
		System.out
				.println("\nThese are what blocks have errors[No Error = 0, Has Error = 1]: ");
		for (int i = 0; i < message.length() / 29; i++) {
			System.out.print(errorBlock[i] + ",");
		}
		System.out.println();
	}

	// This is a custom modulous method that returns the smallest positive,
	// instead of a negative
	private int myMod(int x, int modulo) {
		return ((x % modulo) + modulo) % modulo;
	}

	// For each block, this finds E(t), Q(t), P(t)
	private void processAllBlocks() {
		for (int i = 0; i < message.length() / 29; i++) {
			System.out.print("\nFor block #" + i);
			M.convert();
			M.getRiAndiRi(i);
			M.findAlpha(i);
			M.findRiandEiandEiRi();
			M.alterCArray();

			M.buildQ();
			M.buildP();
			M.printP();

			start = start + 29;

			// RESET EVERYTHING
			Q = new int[28];
			P = new int[27];
			R = new int[29];
		}
	}

	private void findRiandEiandEiRi() {
		// FIND EI AND RIEI
		for (int j = 0; j < 29; j++) {
			Ei[j] = (j + (alpha + 29)) % 29;
			RiEi[j] = (Ei[j] * R[j]) % 29;
		}

		// FIND LEADING COEFFECIENTs FOR ALL SUMS OF RIEI
		M.findSums(RiEi);
	}

	// This multiplies each leading sum of RiEi with each of the corresponding
	// C_K value and stores it in alteredCArrays
	private void alterCArray() {
		for (int k = 0; k < 28; k++)
			alteredCArrays[k] = M.multiplyArray(RiEiLeading[k], cArrays[k]);
	}

	// FIND ALPHA AND PRINT
	private void findAlpha(int i) {
		alpha = (inverseArray[finalSumRi[i]] * finalSumiRi[i] * (-1)) % 29;
		// System.out.println("Alpha is " + alpha);
		if (alpha != 0) {
			errorBlock[i] = 1;
			System.out.println("Error at position " + alpha * (-1)
					+ " of this block of the message.");
		} else
			System.out
					.println("There is no error in this block of the message");
	}

	// This builds and prints Q
	private void buildQ() {
		// BUILD Q
		for (int m = 0; m < 28; m++) {
			for (int n = 0; n < 28; n++)
				Q[m] = (Q[m] + alteredCArrays[n][m]) % 29;
		}

		// PRINT Q
		System.out.print("For this block Q(t) = ");
		for (int z = 0; z < 28; z++)
			System.out.print(Q[z] + "*t^" + (28 - z) + " + ");
	}

	// This builds P and prints the p-values to the console
	private void buildP() {
		P[0] = Q[0];
		System.out.print("\nThese are the P values: " + P[0] + ", ");
		for (int b = 1; b < 27; b++) {
			P[b] = (P[b - 1] * (-1 * alpha) + Q[b]) % 29;
			System.out.print(P[b] + ", ");
		}
	}

	// This prints this section of P and stores this section in decodedMessage
	private void printP() {
		System.out.print("\nThis block decodes to: ");
		for (int d = 0; d < 27; d++) {
			System.out.print(alphabet.charAt(P[d]));
			if (alphabet.charAt(P[d]) != '$')
				decodedMessage = decodedMessage + alphabet.charAt(P[d]);
		}
		System.out.println();
	}

	// This just writes the decodedMessage to a text file
	private void writeFile() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(
				"decodedMessage.txt"));
		writer.write(decodedMessage);
		writer.close();
	}

	// Main method just calls various functions and prints messages
	public static void main(String[] args) throws IOException {
		M.getFileInput();
		M.processAllBlocks();
		M.writeFile();
		M.checkForError();
		System.out.println("\nRecieved Message: " + message);
		System.out.println("Decoded Message: " + decodedMessage);
	}
}