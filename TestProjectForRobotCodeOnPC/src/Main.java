import java.util.Random;


public class Main
{

	public static void main(String[] args)
	{
		Random r = new Random();
		TKOLogger.getInstance().start();
		for (int i = 0; i < 1000; i++)
		{
			TKOLogger.getInstance().addMessage("Testing " + i);
			if (r.nextBoolean())
				TKOLogger.getInstance().addData("Voltage", r.nextDouble(), "num " + i);
			if (r.nextBoolean())
				TKOLogger.getInstance().addData("Current", r.nextDouble(), "num " + i);
		}
		TKOLogger.getInstance().stop();
	}

}
