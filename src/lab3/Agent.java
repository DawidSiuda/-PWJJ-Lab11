package lab3;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;


public class Agent {

	public static void main(String[] args) {
		try{

			// Pobieramy odniesienie do m-servera systemowego.
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

			// Tworzymy nazw dla m-ziarna dla którego zostanie zarejestrowany.
			ObjectName name = new ObjectName("software.JMX:example=standard");

			// Tworzymy m-ziarno Sorter.
			Sorter mbean = new Sorter();
			mbean.start();

			// Rejestrujemy m-ziarno w m-serwerze.
			mbs.registerMBean(mbean, name);

			//System.out.println("Czekam na zgloszenia.");

			while(true) {
				System.out.print("-----------------------------------------------------\n");
				System.out.print(mbean.getInfo());

				if(mbean.sortingHasBeenFinished() == true) {
					System.out.print("End program.");
					System.exit(0);
				}
				else
					Thread.sleep(1500);
			}


		}catch (Exception e) {
			System.out.println("ERROR AT AGENT MAIN");
		}
	}

}
