package HSProject.tileCoded.tilings;

/**
 * A structure combining a state and an action in the form if {@link Tile}s.
 * 
 * @author harrison
 *
 */
public class StateActionPair {
	Tile state;
	Tile action;
	int hash;

	/**
	 * New State-Action pair
	 * 
	 * @param s
	 *            the state
	 * @param a
	 *            the action
	 */
	public StateActionPair(Tile s, Tile a) {
		state = s;
		action = a;
		hash = state.hashCode() * action.hashCode();
	}

	@Override
	public int hashCode() {
		return hash;
	}

	/**
	 * 
	 * @return the state in this pair
	 */
	public Tile getState() {
		return state;
	}

	/**
	 * 
	 * @return the action in this pair
	 */
	public Tile getAction() {
		return action;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StateActionPair) {
			StateActionPair qk = (StateActionPair) obj;
			return this.state.equals(qk.state) && this.action.equals(qk.action);
		} else {
			return false;
		}
	}
}