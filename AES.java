import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class AES extends rijndael
{
	public static void main (String[] args)
	{
		assert args.length == 3 : "Invalid number of arguments.";

		// Prefer working with lists.
		List<String> argsList = Arrays.asList(args);
		List<String> encryptCmds = Arrays.asList("e", "encrypt");
		List<String> decryptCmds = Arrays.asList("d", "decrypt");
		
        //abstracting input method, encrypt and decrypt just taking in lists of strings (32 bytes each)
        //reading bytes was messing with new lines chars 
        File tmpfile=  new File(argsList.get(2));
        byte[] inputBytes = bytesFromLine("b51fdcd646acade9af7661ad66e0218d");
        
		for(String s : encryptCmds)
		{
			if (s.equalsIgnoreCase(argsList.get(0)))
			{
				// TODO: prepare encrypt here
				byte[] key = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };

				File file = new File(argsList.get(2));
				AES cypher = new AES();
				cypher.encrypt(key, inputBytes);
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
				cypher.decrypt(key, inputBytes);
			}
		}
	}

    private static byte[] bytesFromLine (String s)
    {        
        int len = s.length();
        byte[] lineBytes = new byte[16];
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
	
	public void encrypt(byte[] key, byte[] inputBytes)
	{
        
		System.out.println(">>> encrypting");
		assert false : "Not implemented";
		
        //for now just run this block every time, Keyexpansion and state recreation is getting call more than needed reduntant
        
		/*for (int chunkNum = 0 ; chunkNum < chunks.size() ; chunkNum++)
        {
            //lines have 32 bytes, we are looking for 16 bytes.
            byte[] chunk = chunks.get(chunkNum).getBytes();
            //System.out.println(chunk);
            byte[] subChunk1 = new byte[16];
            
            //first half
            for (int i = 0; i < 16; ++i)
            {
                subChunk1[i] = (byte)chunk[i];
            }
		
            //second half
            byte[] subChunk2 = new byte[16];
            for (int i = 0; i < 16; ++i)
            {
                subChunk2[i] = (byte)chunk[i+16];
            }
        
            State state1 = new State(subChunk1);
            state1.keyExpansion(key);
            
            State state2 = new State(subChunk2);
            state2.keyExpansion(key);
            
            encryptCore(state1);
            encryptCore(state2);
		
            System.out.println(bytesToHex(state1.data));
            System.out.println(bytesToHex(state2.data));
        }
*/
	}
    
    //
    private void encryptCore(State state)
    {
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
    }

    
	public void decrypt(byte[] key, byte[] inputBytes)
	{
		System.out.println("<<< decrpyting");
		assert false : "Not implemented";
        /*
        for (int chunkNum = 0 ; chunkNum < chunks.size() ; chunkNum++)       
        {
            byte[] chunk = chunks.get(chunkNum).getBytes();
            
            byte[] subChunk1 = new byte[16];
            for (int i = 0; i < 16; ++i)
            {
                subChunk1[i] = (byte)chunk[i];
            }
		
            byte[] subChunk2 = new byte[16];
            for (int i = 0; i < 16; ++i)
            {
                subChunk2[i] = (byte)chunk[i+16];
            }
            
            State state1 = new State(subChunk1);
            state1.keyExpansion(key);
            
            State state2 = new State(subChunk2);
            state2.keyExpansion(key);
		
            decryptCore(state1);
            decryptCore(state2);
            
            System.out.println(bytesToHex(state1.data));
            System.out.println(bytesToHex(state2.data));
		}
        */
	}
    
    private void decryptCore(State state)
    {
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
    }
}