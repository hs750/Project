package HSProject.tileCoded.tilings;

import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * Procides the ability to manipulate a state representation. Aka, add / remove
 * or modify a state variables in representation.
 * 
 * @author harrison
 *
 */
public interface StateManipulator {

	/**
	 * Manipulate a state
	 * 
	 * @param o
	 *            the state to be modified
	 * @return a new state based on the input
	 */
	public Observation manipulateState(Observation o);
}
