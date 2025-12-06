package cn.Power.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;

import cn.Power.mod.Mod;

public class EventModStateUpdate extends EventCancellable {
	public Mod mod;

	// true: enable false:disactive
	public boolean status;

	public EventModStateUpdate(Mod m, boolean sta) {
		this.mod = m;
		this.status = sta;
	}

}
