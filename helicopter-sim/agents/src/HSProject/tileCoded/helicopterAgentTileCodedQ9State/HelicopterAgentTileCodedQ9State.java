package HSProject.tileCoded.helicopterAgentTileCodedQ9State;



import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentQ;
import HSProject.tileCoded.tilings.StateManipulator;
import HSProject.tileCoded.tilings.StateManipulator9;

/**
 * An experiment applying Q-Learning to a reduced state space of 9 states. These
 * nine states are these defined in: <br>
 * A. Y. Ng, H. J. Kim, M. I. Jordan, and S. Sastry, “Autonomous helicopter
 * flight via Reinforcement Learning,” Adv. Neural Inf. Process. Syst. 16, vol.
 * 16, pp. 363–372, 2004.
 * 
 * @see StateManipulator9
 * 
 * @author harrison
 *
 */
public class HelicopterAgentTileCodedQ9State extends TileCodedAgentQ {
	private static double alpha = 0.1;
	private static double gamma = 1;
	private StateManipulator sm;
	
	public HelicopterAgentTileCodedQ9State() {
		super(alpha, gamma);
		
		
		int numTiles = 10;
		int numVariables = 9;
		int numTilings = 16;
		double[] statesMin = {-5, -5, -5, -20, -20, -20, -1, -1, -1};
		double[] statesMax = {5, 5, 5, 20, 20, 20, 1, 1, 1};
		
		initialiseStateTiling(numVariables, statesMin, statesMax, numTiles, numTilings);

		
		numTiles = 5;
		numVariables = 4;
		numTilings = 16;
		double[] actionsMin = {-1, -1, -1, -1};
		double[] actionsMax = {1, 1, 1, 1};

		initialiseActionTiling(numVariables, actionsMin, actionsMax, numTiles, numTilings);
		
		sm = new StateManipulator9();
	}
	
	/**
	 * Modify the input state to only contain nine state variables.
	 * 
	 * @see StateManipulator9
	 */
	@Override
	protected Observation manipulateState(Observation o){
		return sm.manipulateState(o);
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedQ9State());
		L.run();
	}

}
