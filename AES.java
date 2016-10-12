import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public class AES extends rijndael
{
	public static void main (String[] args)
	{
		assert args.length == 3 : "Invalid number of arguments.";

		// Prefer working with lists.
		List<String> argsList = Arrays.asList(args);
		List<String> encryptCmds = Arrays.asList("e", "encrypt");
		List<String> decryptCmds = Arrays.asList("d", "decrypt");

		for(String s : encryptCmds)
		{
			if (s.equalsIgnoreCase(argsList.get(0)))
			{
				// TODO: prepare encrypt here
				byte[] key = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };

				File file = new File(argsList.get(2));
				AES cypher = new AES();
				cypher.encrypt(key, file);
			}
		}

		for(String s : decryptCmds)
		{
			if (s.equalsIgnoreCase(argsList.get(0)))
			{
				// TODO: prepare decrypt here
				byte[] key = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };

				File file = new File(argsList.get(2));
				AES cypher = new AES();
				cypher.decrypt(key, file);
			}
		}
	}

	public static String bytesToHex(byte[] in) {
	    final StringBuilder builder = new StringBuilder();
	    for(byte b : in) {
	        builder.append(String.format("%02x", b));
	    }
	    return builder.toString();
	}
	
	public void encrypt(byte[] key, File plaintext)
	{
		System.out.println(">>> encrypting");
		assert false : "Not implemented";
		
		char[] buffer = new char[(int) plaintext.length()];
		
		try (FileReader reader = new FileReader(plaintext)){
			reader.read(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String contents = new String(buffer);
		
		String block = contents.substring(0, 16);
		
		State state = new State(block.getBytes());
		
		state.keyExpansion(key);
		
		state.addRoundKey(state.keys[state.round]);
		int numberOfRounds = 10;
		
		for(state.round = 1; state.round < numberOfRounds; ++state.round)
		{
			state.subBytes();
			state.shiftRows();
			state.mixColumns();
			state.addRoundKey(state.keys[state.round]);

		}
		
		// Final round
		state.subBytes();
		state.shiftRows();
		state.addRoundKey(state.keys[state.round]);
		
		System.out.println(bytesToHex(state.data));

	}

	public void decrypt(byte[] key, File encrpyted)
	{
		System.out.println("<<< decrpyting");
		assert false : "Not implemented";
		

		byte[] array = null;
		
		try {
			array = Files.readAllBytes(encrpyted.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		byte[] chunk = new byte[16];
		for (int i = 0; i < 16; ++i)
		{
			chunk[i] = (byte)array[i];
		}
		
		State state = new State(chunk);
		
		// Key Expansion
		state.keyExpansion(key);
		
		int numberOfRounds = 10;
					
		state.addRoundKey(state.keys[numberOfRounds]);
		
		for(state.round = numberOfRounds - 1; state.round > 0; --state.round)
		{
			state.InverseShiftRows();
			state.InverseSubBytes();
			state.addRoundKey(state.keys[state.round]);
			state.InverseMixColumns();
		}
		
		// Final round

		state.InverseShiftRows();
		state.InverseSubBytes();

		state.addRoundKey(state.keys[state.round]);
		
		System.out.println("output: " + new String(state.data));
	}
}