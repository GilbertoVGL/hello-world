package br.edu.ifg.sisd.ts;

import net.jini.space.JavaSpace;

public class FireAlarmConsumer {
	public static void main(String[] args) {
		try {
			JavaSpace space = SpaceAccessor.findSpace("jini://localhost", "AlarmSpace");
			AlarmTuple template = new AlarmTuple("Fire!");
			AlarmTuple recvd = (AlarmTuple) space.read(template, null, Long.MAX_VALUE);
			String msg = recvd.alarmType;
			System.out.println("Read: " + msg);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
