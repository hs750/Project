package HSProject.tileCoded.tilings;

import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * An implementation of {@link StateManipulator} reducing the number of states
 * from 12 to 5. These 5 are the states shared between the state reductions
 * described in: <br>
 * The states used by both <br>
 * A. Y. Ng, H. J. Kim, M. I. Jordan, and S. Sastry, “Autonomous helicopter
 * flight via Reinforcement Learning,” Adv. Neural Inf. Process. Syst. 16, vol.
 * 16, pp. 363–372, 2004. <br>
 * and <br>
 * R. Koppejan and S. Whiteson, “Neuroevolutionary reinforcement learning for
 * generalized control of simulated helicopters.,” Evol. Intell., vol. 4, no. 4,
 * pp. 219–241, 2011.
 * 
 * @author harrison
 *
 */
public class StateManipulator5 implements StateManipulator {

	@Override
	public Observation manipulateState(Observation o) {
		Observation newO = new Observation(0, 5);
		// Velocities
		newO.doubleArray[0] = o.doubleArray[0];
		newO.doubleArray[1] = o.doubleArray[1];
		newO.doubleArray[2] = o.doubleArray[2];
		// Angles
		newO.doubleArray[3] = o.doubleArray[9];
		newO.doubleArray[4] = o.doubleArray[10];
		return newO;
	}

}
