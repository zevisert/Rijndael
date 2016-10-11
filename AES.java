import java.io.File;

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
				byte[] key = new byte[16];
				File file = null;
				AES cypher = new AES();
				cypher.encrypt(key, file);
			}
		}

		for(String s : decryptCmds)
		{
			if (s.equalsIgnoreCase(argsList.get(0)))
			{
				// TODO: prepare decrypt here
				byte[] key = new byte[16];
				File file = null;
				
				AES cypher = new AES();
				cypher.decrypt(key, file);
			}
		}
	}

	public void encrypt(byte[] key, File plaintext)
	{
		System.out.println(">>> encrypting");
		assert false : "Not implemented";
		
		String s = "Stri"+
				   "ng o"+
				   "f 16"+
				   "char";

		byte[] test_data = s.getBytes();
		
		State state = new State(test_data);
		// Key Expansion
		// keyExpansion();
		
		state.addRoundKey(key);
		
		int numberOfRounds = 1;
		
		for(int round = 1; round <= numberOfRounds; ++round)
		{
			state.subBytes();
			state.shiftRows();
			state.mixColumns();
			state.addRoundKey(key);
		}
		
		// Final round
		state.subBytes();
		state.shiftRows();
		state.addRoundKey(key);
		
		System.out.print("[");
		for(byte b : state.data)
		{
			System.out.print(Integer.toHexString(Byte.toUnsignedInt(b)).toUpperCase() + ", ");
		}
		System.out.println("]");
	}

	public void decrypt(byte[] key, File encrpyted)
	{
		System.out.println("<<< decrpyting");
		assert false : "Not implemented";
		
		byte[] test_data = {(byte)0xB5, 0x12, 0x59, 0x12, (byte)0xA1, 0x3E, 0x7D, 0x28, (byte)0x83, 0x7B, 0x27, 0x2B, (byte)0x89, 0x28, 0x54, (byte)0xAF};
		
		State state = new State(test_data);
		// Key Expansion
		// keyExpansion();
		
		state.addRoundKey(key);
		
		int numberOfRounds = 1;
		
		for(int round = 1; round <= numberOfRounds; ++round)
		{
			state.InverseShiftRows();
			state.InverseSubBytes();
			state.addRoundKey(key);
			state.InverseMixColumns();
		}
		
		// Final round

		state.InverseShiftRows();
		state.InverseSubBytes();

		state.addRoundKey(key);
		
		String s = new String(state.data);
		System.out.println(s);
	}
}