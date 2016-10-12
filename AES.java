import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.ValidationException;

public class AES extends rijndael
{
	
	public static void main (String[] args) throws ValidationException
	{
		assert args.length == 3 : "Invalid number of arguments.";

		// Prefer working with lists.
		List<String> argsList = Arrays.asList(args);
		List<String> encryptCmds = Arrays.asList("e", "encrypt");
		List<String> decryptCmds = Arrays.asList("d", "decrypt");
		
        //abstracting input method, encrypt and decrypt just taking in lists of strings (32 bytes each)
        //reading bytes was messing with new lines chars
        File inputfile=  new File(argsList.get(2));
        File keyfile=  new File(argsList.get(1));
        List<String> lines = null;
        List<byte[]> data = new ArrayList<byte[]>();
        byte[] inputKey = null; 
        
        //read Key from inputfile
        try
        {
            lines = Files.readAllLines(keyfile.toPath());
            //should be 1 line.
            for(String line : lines)
            {
            	line = line.replaceAll("\\s+", "");
            	if (line.length() != 64)
            	{
            		throw new ValidationException("String not 64 bytes");
            	}
            	inputKey = bytesFromLine(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        
        
        // Get all lines and check their length
        try
        {
            lines = Files.readAllLines(inputfile.toPath());
            
            for(String line : lines)
            {
            	line = line.replaceAll("\\s+", "");
            	if (line.length() != 32)
            	{
            		throw new ValidationException("String not 32 bytes");
            	}
            	data.add(bytesFromLine(line));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        
        
		for(String s : encryptCmds)
		{
			if (s.equalsIgnoreCase(argsList.get(0)))
			{

				AES cypher = new AES();
				List<String> output = cypher.encrypt(inputKey, data);
				
				File outputFile = new File(new String(argsList.get(2) + ".enc"));
				
				try (FileWriter writer = new FileWriter(outputFile))
				{
					for (String line : output)
					{
						writer.write(line);
						writer.write('\n');
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		for(String s : decryptCmds)
		{
			if (s.equalsIgnoreCase(argsList.get(0)))
			{

				AES cypher = new AES();
				List<String> output = cypher.decrypt(inputKey, data);
				
				File outputFile = new File(new String(argsList.get(2) + ".dec"));
				
				try (FileWriter writer = new FileWriter(outputFile))
				{
					for (String line : output)
					{
						writer.write(line);
						writer.write('\n');
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

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
    
	public static String bytesToHex(byte[] in) {
	    final StringBuilder builder = new StringBuilder();
	    for(byte b : in) {
	        builder.append(String.format("%02x", b));
	    }
	    return builder.toString();
	}
	
	public List<String> encrypt(byte[] key, List<byte[]> data)
	{ 
		System.out.println(">>> encrypting");
		
        //for now just run this block every time, Keyexpansion and state recreation is getting call more than needed reduntant
        
		KeyExpander ke = new KeyExpander(key);
		List<String> output = new ArrayList<String>();
		
		for (byte[] block : data)
        {
            State state = new State(block);
            state.keys = ke.keys;
            encryptCore(state);
		
            System.out.println(bytesToHex(state.data));
            output.add(bytesToHex(state.data));
        }
		
		return output;
	}
    
    private void encryptCore(State state)
    {
    	state.round = 0;
        state.addRoundKey(state.keys[state.round]);
		int numberOfRounds = 14;
		
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
    }

    
	public List<String> decrypt(byte[] key, List<byte[]> data)
	{
		System.out.println(">>> decrypting");
		
        //for now just run this block every time, Keyexpansion and state recreation is getting call more than needed reduntant
        
		KeyExpander ke = new KeyExpander(key);
		List<String> output = new ArrayList<String>();
		
		for (byte[] block : data)
        {
            State state = new State(block);
            state.keys = ke.keys;
            decryptCore(state);
		
            System.out.println(bytesToHex(state.data));
            output.add(bytesToHex(state.data));
        }
		
		return output;
	}
    
    private void decryptCore(State state)
    {
         int numberOfRounds = 14;
					
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
    }
}