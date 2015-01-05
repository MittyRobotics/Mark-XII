
public class Main
{

	public static void main(String[] args)
	{
		TKOLogger.start();
		for (int i = 0; i < 1000; i++)
		{
			TKOLogger.addMessage("Testing " + i);
		}
		TKOLogger.stop();
	}

}
