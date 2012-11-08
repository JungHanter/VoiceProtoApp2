package com.swm.vg.data;


public class ActionInfo {
	public int animalId;
	public int actionId;
	
	public ActionInfo() {
		this(-1, ActionList.ACTION_EXTRA_UNKNOWN);
	}
	
	public ActionInfo(int animalId, int actionId) {
		this.animalId = animalId;
		this.actionId = actionId;
	}
	
	public void init() {
		animalId = -1;
		actionId = ActionList.ACTION_EXTRA_UNKNOWN;
	}
	
	public ActionInfo set(int animalId, int actionId) {
		this.animalId = animalId;
		this.actionId = actionId;
		return this;
	}
	
	public ActionInfo set(int animalId) {
		this.animalId = animalId;
		this.actionId = ActionList.ACTION_EXTRA_UNKNOWN;
		return this;
	}
	
}
