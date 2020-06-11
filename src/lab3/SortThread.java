package lab3;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class SortThread implements Runnable{


	private static long threadID = 0;
	private long seed;
	private Map<Integer , Reference<int[]>> map;
	private Semaphore mapMutex;
	private Boolean loadedSortingFunction;
	private String threadName;
	private int seedMin;
	private int seedMax;
	private int numberOfElementsToSort;
	private int mapSize;
	private int memSize;
	private Random rand;
	Boolean endThread;
	ReferenceCounter referenceCounter;

	java.lang.reflect.Method methodSolve;
	Class<?> reflectIntSorter;

	public SortThread(long seed, Map<Integer , Reference<int[]>> map, Semaphore mapMutex,
					      int seedMin, int seedMax, int numberOfElementsToSort, ReferenceCounter referenceCounter,
					      int memSize)
	{
		endThread = false;

		threadID++;

		this.seed = seed;
		this.map = map;
		this.mapMutex = mapMutex;
		this.seedMin = seedMin;
		this.seedMax = seedMax;
		this.numberOfElementsToSort = numberOfElementsToSort;
		this.referenceCounter = referenceCounter;
		this.memSize = memSize;

		mapSize = seedMax- seedMin;
		rand = new Random(seed);
		loadedSortingFunction = false;
		threadName = "SortThread " + threadID + " > ";

		System.out.println("SortThread> Created new thread. ID: " + threadID);

		try{
			reflectIntSorter = Class.forName("package1.IntSorter");

			System.out.println(threadName + "Found IntSorter");

			methodSolve = reflectIntSorter.getMethod("solve", int[].class);

			System.out.println(threadName + "Found method package1.IntSorter.solve");

			if(methodSolve != null)
				loadedSortingFunction = true;

		}
		catch(Exception e){
			System.out.println("Exception> " + threadName + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		if(loadedSortingFunction == false)
		{
			System.out.println(threadName + "ERROR > No loaded sorting function");
			return;
		}

		if(map.size() >= mapSize)
		{
			System.out.println(threadName + "ERROR > Whole map contains only sorted elements");
			return;
		}

		try
		{
			while(true)
			{
				int seed;

				//
				// check if element with rand seed exist.
				//

				int[] array;

				while(true)
				{
					mapMutex.acquire();

					referenceCounter.incrementMemoryCall();

					if(memSize == 0) {
						map.clear();
						mapMutex.release();
						Thread.sleep(100);
						continue;
					}

					if(memSize == 1) {
						map.clear();
					}

					if(map.size() >= memSize) {
						// Remove 1/3 of elements.
						Iterator<Integer> it = map.keySet().iterator();

						try {
							while(map.size() > (memSize/4)*3) {
								if(it.hasNext() == true) {
									it.next();
									it.remove();
								}
							}
						} catch (Exception e) {
							//System.out.println("WARNING: Iterator null.");
						}
					}

					seed = rand.nextInt(mapSize) + seedMin;

					if(map.containsKey(seed) == true )
					{
						Reference<int[]> ref = (Reference<int[]>) map.get(seed);

						if(ref != null && ref.get() != null)
						{
							mapMutex.release();
							continue;
						}

						referenceCounter.incrementUnsuccessfulMemoryCall();
					}


					//
					// Fill data for rand seed
					//
					Random randTable = new Random(seed);
					array = new int[numberOfElementsToSort];
					for (int i = 0; i< numberOfElementsToSort; i++)
					{
						array[i] = randTable.nextInt(10000);
					}

				    map.put(seed, new WeakReference<int[]>(array.clone()));
				    mapMutex.release();

				    break;
				}

				Object obj = methodSolve.invoke(reflectIntSorter.newInstance(), array);
				array = (int[])obj;

				mapMutex.acquire();
				map.put(seed, new WeakReference<int[]>(array));

				mapMutex.release();


				Thread.sleep(1);

				if(endThread == true)
					return;
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception Thread" + threadID + "> " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			mapMutex.release();
		}
	}

	public void setEndThread(Boolean value) {
		endThread = value;
	}

	public void setMemSize(int memSize) {
		this.memSize = memSize;
	}

	public int getMemSize() {
		return memSize;
	}

	public int getCurrentMemSize() {
		int size = -1;
		try {
			mapMutex.acquire();
			size = map.size();
			mapMutex.release();
		} catch (InterruptedException e) {
			System.out.print("ERROR: SortThreadError.");
		}

		return size;
	}
}
