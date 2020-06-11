package lab3;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Sorter implements SorterMBean {

	static private final int SEED_MIN = 0;
	static private final int SEED_MAX = 50000;
	//static private final int LIST_SIZE = 600;
	//static private final int NUMBER_OF_THREAD = 4;

	private int threadsNumber;
	private int memorySize;

	Map<Thread, SortThread> sortThreadTab;
	ReferenceCounter referenceCounter;
	Boolean endComputing;
	Map<Integer , Reference<int[]>> map;
	Semaphore mutex;
	Random rand;


	public Sorter() {
		endComputing = false;
		threadsNumber = 4;
		memorySize = 600;

		map = new HashMap<Integer,  Reference<int[]>>();

		sortThreadTab = new HashMap<Thread, SortThread>();

		mutex = new Semaphore(1);

		referenceCounter = new ReferenceCounter();

		//
		// Create and run threads.
		//

		rand = new Random(System.currentTimeMillis());

		for(int i = 0; i < threadsNumber; i++)
		{
			SortThread st = new SortThread(rand.nextInt(), map, mutex, SEED_MIN, SEED_MAX, memorySize, referenceCounter);
			Thread t = new Thread(st);
			sortThreadTab.put(t, st);
		}
	}

	public void start() {
		for (Thread t : sortThreadTab.keySet())
			t.start();
	}


//	private void reloadComputing() {
//
//		//
//		// Kill threads.
//		//
//
//		for (SortThread t : sortThreadTab.values())
//			t.setEndThread(true);
//
//		//
//		// Check if all threads stop working.
//		//
//
//		Boolean allThreadsAreDead = false;
//		while(allThreadsAreDead == false) {
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {}
//			for (Thread t : sortThreadTab.keySet())
//				if(t.isAlive() == true)
//					continue;
//			break;
//		}
//
//		//
//		// Clean map of threads
//		//
//
//		sortThreadTab.clear();
//
//		//
//		// Create new map of threads and start them.
//		//
//
//		for(int i = 0; i < threadsNumber; i++)
//		{
//			SortThread st = new SortThread(rand.nextInt(), map, mutex, SEED_MIN, SEED_MAX, memorySize, referenceCounter);
//			Thread t = new Thread(st);
//			t.start();
//			sortThreadTab.put(t, st);
//		}
//	}

	public Boolean sortingHasBeenFinished() {

		for (Thread t : sortThreadTab.keySet())
			if(t.isAlive() == true)
				return false;

		if (endComputing == false)
			return false;

		return true;
	}

	@Override
	public void setThreadsNumber(int threadsNumber) {
		//
		// Kill threads.
		//

		for (SortThread t : sortThreadTab.values())
			t.setEndThread(true);

		//
		// Check if all threads stop working.
		//

		Boolean allThreadsAreDead = false;
		while(allThreadsAreDead == false) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
			for (Thread t : sortThreadTab.keySet())
				if(t.isAlive() == true)
					continue;
			break;
		}

		//
		// Clean map of threads
		//

		sortThreadTab.clear();

		//
		// Create new map of threads and start them.
		//

		for(int i = 0; i < threadsNumber; i++)
		{
			SortThread st = new SortThread(rand.nextInt(), map, mutex, SEED_MIN, SEED_MAX, memorySize, referenceCounter);
			Thread t = new Thread(st);
			t.start();
			sortThreadTab.put(t, st);
		}

		//
		// Set number of thread
		//

		this.threadsNumber = threadsNumber;
	}

	@Override
	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
		//
		// Kill threads.
		//

		for (SortThread t : sortThreadTab.values())
			t.setEndThread(true);

		//
		// Check if all threads stop working.
		//

		Boolean allThreadsAreDead = false;
		while(allThreadsAreDead == false) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
			for (Thread t : sortThreadTab.keySet())
				if(t.isAlive() == true)
					continue;
			break;
		}

		//
		// Clean map of threads
		//

		sortThreadTab.clear();

		//
		// Create new map of values.
		//

		//
		// Create new map of threads and start them.
		//

		for(int i = 0; i < threadsNumber; i++)
		{
			SortThread st = new SortThread(rand.nextInt(), map, mutex, SEED_MIN, SEED_MAX, memorySize, referenceCounter);
			Thread t = new Thread(st);
			t.start();
			sortThreadTab.put(t, st);
		}
	}

	@Override
	public String getInfo() {
		String info = referenceCounter.getStat();
		referenceCounter.cleanSecondsCountern();

		info += "Threads " + threadsNumber + ", memory size: " + memorySize + "\n";
		return info;
	}
}
