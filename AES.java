import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AES extends rijndael
{

	public static void main (String[] args)
	{
		// Needed option keyFile inputFile
		if (args.length != 3) {
			throw new IllegalArgumentException("Invalid number of arguments\nExpected $> java AES option keyFile inputFile\nUse quotations to enclose paths with spaces.");
		}

		// Prefer working with lists.
		List<String> argsList = Arrays.asList(args);
		List<String> encryptCmds = Arrays.asList("e", "encrypt", "-e");
		List<String> decryptCmds = Arrays.asList("d", "decrypt", "-d");

		// Create the file objects, exceptions will be handled later if they don't exist
		File keyFile = new File(argsList.get(1));
		File inputfile = new File(argsList.get(2));

		// Get all lines and check their length
		try
		{
			List<String> lines = Files.readAllLines(inputfile.toPath());
			List<byte[]> data = new ArrayList<byte[]>();
			AES cypher = new AES();

			for(String line : lines)
			{
				line = line.replaceAll("\\s+", "").toUpperCase();
				if (line.length() != 32)
				{
					throw new NumberFormatException("Input line not 32 bytes");
				}
				data.add(bytesFromLine(line));
			}
			
			// Require some data to work with!
			if (data.size() <= 0) {
				throw new IllegalStateException("Input file was empty");
			}

			// Check each possible encrypt command
			for(String s : encryptCmds)
			{
				if (s.equalsIgnoreCase(argsList.get(0))) {

					List<String> output = cypher.encrypt(getKeyData(keyFile), data);

					File outputFile = new File(new String(argsList.get(2) + ".enc"));

					FileWriter writer = new FileWriter(outputFile);
					for (String outLine : output){
						writer.write(outLine.toUpperCase() + "\n");
					}
					writer.close();
					
					System.out.println(">>> done");
				}
			}

			// Not encrypting, see if a decrypt option was specified
			for(String s : decryptCmds)
			{
				if (s.equalsIgnoreCase(argsList.get(0))) {

					List<String> output = cypher.decrypt(getKeyData(keyFile), data);

					File outputFile = new File(new String(argsList.get(2) + ".dec"));

					FileWriter writer = new FileWriter(outputFile);
					for (String outLine : output) {
						writer.write(outLine.toUpperCase() + "\n");
					}
					writer.close();
					
					System.out.println(">>> done");
				}
			}

		}
		catch (NoSuchFileException e)
		{
			System.out.println("Cannot find file: '" + e.getMessage() + "'");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}

	// Encryption routine /////////////////////////////////////////////////////
	public List<String> encrypt(byte[] key, List<byte[]> data)
	{ 
		System.out.println(">>> encrypting");

		// Key expansion needs to happen only once, since it's the same for every block
		KeyExpander ke = new KeyExpander(key);
		List<String> output = new ArrayList<String>();

		for (byte[] block : data)
		{
			State state = new State(block, ke.keys);
			encryptCore(state);
			
			// Save the output encrypted text for this transaction to write to the output file later.
			output.add(bytesToHexString(state.getData()));
		}

		return output;
	}

	private void encryptCore(State state)
	{
		// Set to round 0
		state.round = 0;
		final int numberOfRounds = 14;
		
		// Initial round, key expansion already happened
		state.addRoundKey(state.getKey(state.round));
		
		// The remaining rounds 
		for(state.round = 1; state.round < numberOfRounds; ++state.round)
		{
			state.subBytes();
			state.shiftRows();
			state.mixColumns();
			state.addRoundKey(state.getKey(state.round));
		}

		// Final round
		state.subBytes();
		state.shiftRows();
		state.addRoundKey(state.getKey(state.round));
	}

	// decryption routine /////////////////////////////////////////////////////
	public List<String> decrypt(byte[] key, List<byte[]> data)
	{
		System.out.println(">>> decrypting");

		// Key expansion needs to happen only once, since it's the same for every block
		KeyExpander ke = new KeyExpander(key);
		List<String> output = new ArrayList<String>();

		for (byte[] block : data)
		{
			State state = new State(block, ke.keys);
			decryptCore(state);

			// Save the output plaintext for this transaction to write to the output file later.
			output.add(bytesToHexString(state.getData()));
		}

		return output;
	}

	private void decryptCore(State state)
	{
		// Reverse of encryptCore
		final int numberOfRounds = state.round = 14;
		
		state.addRoundKey(state.getKey(state.round));

		for(state.round = numberOfRounds - 1; state.round > 0; --state.round)
		{
			state.InverseShiftRows();
			state.InverseSubBytes();
			state.addRoundKey(state.getKey(state.round));
			state.InverseMixColumns();
		}

		// Final round
		state.InverseShiftRows();
		state.InverseSubBytes();

		state.addRoundKey(state.getKey(state.round));
	}
	
	// Helpers ////////////////////////////////////////////////////////////////
	
	// Get the byte representation of the input key string
	private static byte[] getKeyData(File keyfile) throws IOException{
		List<String> lines;
		try
		{
			lines = Files.readAllLines(keyfile.toPath());
			if (lines.size() != 1) {
				throw new InvalidKeyException("Expect key file to contain one line of 64 ascii hex characters.");
			}
			for(String line : lines) {
				line = line.replaceAll("\\s+", "");
				if (line.length() != 64){
					throw new InvalidKeyException("Expect key file to contain one line of 64 ascii hex characters.");
				}
				else return bytesFromLine(line);
			}
		}
		catch (InvalidKeyException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		return null;
	}

	// Get the byte representation of a line of the input text
	private static byte[] bytesFromLine (String s)
	{        
		int len = s.length();
		byte[] lineBytes = new byte[len/2];
		for (int i = 0 ; i < len; i += 2 )    
		{
			lineBytes[i/2] = (byte)((Character.digit(s.charAt(i),16) << 4) + Character.digit(s.charAt(i+1), 16));
		}

		return lineBytes;
	}

	// Returns a string like Arrays.toString would, but without separating elements
	private static String bytesToHexString(byte[] in) {
		final StringBuilder builder = new StringBuilder();
		for(byte b : in) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}

}