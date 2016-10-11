public class rijndael {

	public class State
	{
		int round;
		byte[] data;

		public State(byte[] data)
		{
			this.round = 0;
			this.data = new byte[16];
			for(int i = 0; i < 16; ++i)
			{
				this.data[i] = data[i];
			}
		}

		public void apply(byte[] data)
		{
			for(int i = 0; i < 16; ++i)
			{
				this.data[i] = data[i];
			}
		}
	}

	void subBytes(){}
	void shiftRows(){}
	void mixColumns(){}
	void addRoundKey(){}

}