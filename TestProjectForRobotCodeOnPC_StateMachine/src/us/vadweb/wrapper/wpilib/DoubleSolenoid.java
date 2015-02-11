package us.vadweb.wrapper.wpilib;

public class DoubleSolenoid
{
	public static class Value {

        public final int value;
        public static final int kOff_val = 0;
        public static final int kForward_val = 1;
        public static final int kReverse_val = 2;
        public static final Value kOff = new Value(kOff_val);
        public static final Value kForward = new Value(kForward_val);
        public static final Value kReverse = new Value(kReverse_val);

        private Value(int value) {
            this.value = value;
        }
    }
	
	private int _a, _b;
	
	public DoubleSolenoid(int a, int b)
	{
		_a = a;
		_b = b;
	}
	
    public void set(final Value value)
    {

    }

    public Value get()
    {
		return null;
    }
}
