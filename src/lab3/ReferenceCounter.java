package lab3;

import java.util.concurrent.Semaphore;

public class ReferenceCounter {

	public ReferenceCounter()
	{
		g1= 0;
		g2= 0;
		m1= 0;
		m2= 0;
		mutex = new Semaphore(1);
	}

	public void incrementMemoryCall()
	{
		try
		{
		mutex.acquire();
		g1++;
		g2++;
		mutex.release();
		}
		catch (Exception e)
		{
			System.out.println("Exception ReferenceCounter> " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void incrementUnsuccessfulMemoryCall()
	{
		try
		{
		mutex.acquire();
		m1++;
		m2++;
		mutex.release();
		}
		catch (Exception e)
		{
			System.out.println("Exception ReferenceCounter> " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void cleanSecondsCountern()
	{
		try
		{
		mutex.acquire();
		g2 = 0;
		m2 = 0;
		mutex.release();
		}
		catch (Exception e)
		{
			System.out.println("Exception ReferenceCounter> " + e.getMessage());
			e.printStackTrace();
		}
	}

	public String getStat()
	{
		String info = new String();
		try
		{
		mutex.acquire();
		info = "g1:" + g1 + " , m1: " + m1+ ", g2: " + g2 + ", m2: " + m2+
				"\n" + "m1/g1 = " + (float)((float)m1/g1) + ", m2/g2 = " + (float)((float)m2/g2) + "\n";
		mutex.release();
		}
		catch (Exception e)
		{
			System.out.println("Exception ReferenceCounter> " + e.getMessage());
			e.printStackTrace();
		}

		return info;
	}

	int g1;
	int g2;
	int m1;
	int m2;
	Semaphore mutex;
}
