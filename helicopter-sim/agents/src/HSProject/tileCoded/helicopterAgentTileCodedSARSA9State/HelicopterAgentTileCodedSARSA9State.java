package HSProject.tileCoded.helicopterAgentTileCodedSARSA9State;

import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentSARSA;
import HSProject.tileCoded.tilings.StateManipulator;
import HSProject.tileCoded.tilings.StateManipulator9;

/**
 * An experiment applying SARSA to a reduced state space of 9 states. These nine
 * states are these defined in: <br>
 * A. Y. Ng, H. J. Kim, M. I. Jordan, and S. Sastry, “Autonomous helicopter
 * flight via Reinforcement Learning,” Adv. Neural Inf. Process. Syst. 16, vol.
 * 16, pp. 363–372, 2004.
 * 
 * @see StateManipulator9
 * 
 * @author harrison
 *
 */
public class HelicopterAgentTileCodedSARSA9State extends TileCodedAgentSARSA {
	private StateManipulator sm;

	public HelicopterAgentTileCodedSARSA9State() {

		int numTiles = getConfig().getInt("stateTiles");
		int numVariables = 9;
		int numTilings = getConfig().getInt("stateTilings");
		double[] statesMin = { -5, -5, -5, -20, -20, -20, -1, -1, -1 };
		double[] statesMax = { 5, 5, 5, 20, 20, 20, 1, 1, 1 };

		initialiseStateTiling(numVariables, statesMin, statesMax, numTiles, numTilings);

		numTiles = getConfig().getInt("actionTiles");
		numVariables = 4;
		numTilings = getConfig().getInt("actionTilings");
		double[] actionsMin = { -1, -1, -1, -1 };
		double[] actionsMax = { 1, 1, 1, 1 };

		initialiseActionTiling(numVariables, actionsMin, actionsMax, numTiles, numTilings);

		sm = new StateManipulator9();
	}

	/**
	 * Modify the input state to only contain nine state variables.
	 * 
	 * @see StateManipulator9
	 */
	@Override
	protected Observation manipulateState(Observation o) {
		return sm.manipulateState(o);
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedSARSA9State());
		L.run();
	}

}
