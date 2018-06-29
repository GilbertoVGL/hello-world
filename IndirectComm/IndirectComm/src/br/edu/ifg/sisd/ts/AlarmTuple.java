package br.edu.ifg.sisd.ts;

import net.jini.core.entry.Entry;

public class AlarmTuple implements Entry {
	public String alarmType;

	public AlarmTuple(String alarmType) {
		this.alarmType = alarmType;
	}
}