package HSProject.tileCoded.tilings;

import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * R. Koppejan and S. Whiteson, “Neuroevolutionary reinforcement learning for
 * generalized control of simulated helicopters.,” Evol. Intell., vol. 4, no. 4,
 * pp. 219–241, 2011.
 * 
 * @author harrison
 *
 */
public class StateManipulator9 implements StateManipulator {

	@Override
	public Observation manipulateState(Observation o) {
		Observation newO = new Observation(0, 9);
		// Velocities
		newO.doubleArray[0] = o.doubleArray[0];
		newO.doubleArray[1] = o.doubleArray[1];
		newO.doubleArray[2] = o.doubleArray[2];
		// Positions
		newO.doubleArray[3] = o.doubleArray[3];
		newO.doubleArray[4] = o.doubleArray[4];
		newO.doubleArray[5] = o.doubleArray[5];
		// Angles
		newO.doubleArray[6] = o.doubleArray[9];
		newO.doubleArray[7] = o.doubleArray[10];
		newO.doubleArray[8] = o.doubleArray[11];

		return newO;
	}

}
