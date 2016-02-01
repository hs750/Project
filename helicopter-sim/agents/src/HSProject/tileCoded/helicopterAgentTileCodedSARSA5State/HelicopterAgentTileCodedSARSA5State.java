package HSProject.tileCoded.helicopterAgentTileCodedSARSA5State;

import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentSARSA;
import HSProject.tileCoded.tilings.StateManipulator;
import HSProject.tileCoded.tilings.StateManipulator5;

/**
 * An experiment applying SARSA to a reduced state space of 5 states. These five
 * state the intersection between the reduced states used in: <br>
 * A. Y. Ng, H. J. Kim, M. I. Jordan, and S. Sastry, “Autonomous helicopter
 * flight via Reinforcement Learning,” Adv. Neural Inf. Process. Syst. 16, vol.
 * 16, pp. 363–372, 2004. <br>
 * and <br>
 * R. Koppejan and S. Whiteson, “Neuroevolutionary reinforcement learning for
 * generalized control of simulated helicopters.,” Evol. Intell., vol. 4, no. 4,
 * pp. 219–241, 2011.
 * 
 * @see StateManipulator5
 * 
 * @author harrison
 *
 */
public class HelicopterAgentTileCodedSARSA5State extends TileCodedAgentSARSA {
	private static double alpha = 0.1;
	private static double gamma = 1;
	private StateManipulator sm;

	public HelicopterAgentTileCodedSARSA5State() {
		super(alpha, gamma);

		int numTiles = 10;
		int numVariables = 5;
		int numTilings = 16;
		double[] statesMin = { -5, -5, -5, -1, -1 };
		double[] statesMax = { 5, 5, 5, 1, 1 };

		initialiseStateTiling(numVariables, statesMin, statesMax, numTiles, numTilings);

		numTiles = 5;
		numVariables = 4;
		numTilings = 16;
		double[] actionsMin = { -1, -1, -1, -1 };
		double[] actionsMax = { 1, 1, 1, 1 };

		initialiseActionTiling(numVariables, actionsMin, actionsMax, numTiles, numTilings);

		sm = new StateManipulator5();
	}

	/**
	 * Modify the input state to only contain five state variables.
	 * 
	 * @see StateManipulator5
	 */
	@Override
	protected Observation manipulateState(Observation o) {
		return sm.manipulateState(o);
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedSARSA5State());
		L.run();
	}

}
