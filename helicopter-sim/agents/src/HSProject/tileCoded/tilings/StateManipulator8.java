package HSProject.tileCoded.tilings;

import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * A. Y. Ng, H. J. Kim, M. I. Jordan, and S. Sastry, “Autonomous helicopter flight via Reinforcement Learning,” Adv. Neural Inf. Process. Syst. 16, vol. 16, pp. 363–372, 2004.
 * @author harrison
 *
 */
public class StateManipulator8 implements StateManipulator {

	@Override
	public Observation manipulateState(Observation o) {
		Observation newO = new Observation(0, 8);
		// Velocities
		newO.doubleArray[0] = o.doubleArray[0];
		newO.doubleArray[1] = o.doubleArray[1];
		newO.doubleArray[2] = o.doubleArray[2];
		
		// Angular Velocities 
		newO.doubleArray[3] = o.doubleArray[6];
		newO.doubleArray[4] = o.doubleArray[7];
		newO.doubleArray[5] = o.doubleArray[8];
		
		// Roll Pitch
		newO.doubleArray[6] = o.doubleArray[9];
		newO.doubleArray[7] = o.doubleArray[10];
		
		
		return newO;
	}

}
