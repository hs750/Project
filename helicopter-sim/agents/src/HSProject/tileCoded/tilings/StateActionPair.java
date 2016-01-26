package HSProject.tileCoded.tilings;

public class StateActionPair{
	Tile observation;
	Tile action;
	int hash;
	public StateActionPair(Tile o, Tile a) {
		observation = o;
		action = a;
		hash = observation.hashCode() * action.hashCode();
	}

	@Override
	public int hashCode() {
		return hash;
	}
	
	public Tile getState(){
		return observation;
	}
	
	public Tile getAction(){
		return action;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StateActionPair) {
			StateActionPair qk = (StateActionPair) obj;
			return this.observation.equals(qk.observation) && this.action.equals(qk.action);
		} else {
			return false;
		}
	}
}