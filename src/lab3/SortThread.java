package lab3;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
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
	ReferenceCounter referenceCounter;
	Boolean endThread;

	java.lang.reflect.Method methodSolve;
	Class<?> reflectIntSorter;

	public SortThread(long seed, Map<Integer , Reference<int[]>> map, Semaphore mapMutex,
					      int seedMin, int seedMax, int numberOfElementsToSort, ReferenceCounter referenceCounter, int memSize)
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

					seed = rand.nextInt(mapSize) + seedMin;

					referenceCounter.incrementMemoryCall();

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

				   // map.put(seed, array.clone());
				    map.put(seed, new WeakReference<int[]>(array.clone()));

				    mapMutex.release();

				    //System.out.println(threadName + "INFO > Createt array for seed: "+ seed);
				    break;
				}

				//System.out.println(threadName + "INFO > Start sort array for seed: "+ seed);

				Object obj = methodSolve.invoke(reflectIntSorter.newInstance(), array);
				array = (int[])obj;
				//System.out.println(threadName + "INFO > Array for seed: "+ seed + " has been sorted");

				mapMutex.acquire();
				map.put(seed, new WeakReference<int[]>(array));

				mapMutex.release();

				//System.out.println(threadName + "INFO > Array for seed: "+ seed + " has been added to map");

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
}
