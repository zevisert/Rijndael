import java.io.File;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public class AES extends rijndael
{
	public static void main (String[] args)
	{
		assert args.length == 3 : "Invalid number of arguments.";

		// Prefer wokring with lists.
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

				encrypt(key, file);
			}
		}

		for(String s : decryptCmds)
		{
			if (s.equalsIgnoreCase(argsList.get(0)))
			{
				// TODO: prepare decrypt here
				byte[] key = new byte[16];
				File file = null;

				decrypt(key, file);
			}
		}
	}

	public static void encrypt(byte[] key, File plaintext)
	{
		System.out.println(">>> encrypting");
		assert false : "Not implemented";
	}

	public static void decrypt(byte[] key, File encrpyted)
	{
		System.out.println("<<< decrpyting");
		assert false : "Not implemented";
	}
}