package br.edu.ifg.sisd.ts;

import net.jini.space.JavaSpace;

public class FireAlarmProducer {
	public static void main(String[] args) {
		try {
			JavaSpace space = SpaceAccessor.findSpace("jini://localhost", "AlarmSpace");
			AlarmTuple tuple = new AlarmTuple("Fire!");
			space.write(tuple, null, 60 * 60 * 1000);
			System.out.println("Message writed");
		} catch (Exception e) {
		}
	}
}
