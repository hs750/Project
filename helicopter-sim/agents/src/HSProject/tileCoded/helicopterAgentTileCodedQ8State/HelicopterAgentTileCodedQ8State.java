package HSProject.tileCoded.helicopterAgentTileCodedQ8State;



import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentQ;
import HSProject.tileCoded.tilings.StateManipulator;
import HSProject.tileCoded.tilings.StateManipulator8;

/**
 * An experiment applying Q-Learning to a reduced state space of 8 states. These
 * eight states are these defined in: <br>
 * R. Koppejan and S. Whiteson, “Neuroevolutionary reinforcement learning for
 * generalized control of simulated helicopters.,” Evol. Intell., vol. 4, no. 4,
 * pp. 219–241, 2011.
 * 
 * @see StateManipulator8
 * 
 * @author harrison
 *
 */
public class HelicopterAgentTileCodedQ8State extends TileCodedAgentQ {
	private static double alpha = 0.1;
	private static double gamma = 1;
	private StateManipulator sm;
	
	public HelicopterAgentTileCodedQ8State() {
		super(alpha, gamma);
		
		
		int numTiles = 10;
		int numVariables = 8;
		int numTilings = 16;
		double[] statesMin = {-5, -5, -5, -12.566, -12.566, -12.566, -1, -1};
		double[] statesMax = {5, 5, 5,12.566, 12.566, 12.566, 1, 1};
		
		initialiseStateTiling(numVariables, statesMin, statesMax, numTiles, numTilings);

		
		numTiles = 5;
		numVariables = 4;
		numTilings = 16;
		double[] actionsMin = {-1, -1, -1, -1};
		double[] actionsMax = {1, 1, 1, 1};

		initialiseActionTiling(numVariables, actionsMin, actionsMax, numTiles, numTilings);
		
		sm = new StateManipulator8();
	}
	
	/**
	 * Modify the input state to only contain eight state variables.
	 * 
	 * @see StateManipulator8
	 */
	@Override
	protected Observation manipulateState(Observation o){
		return sm.manipulateState(o);
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedQ8State());
		L.run();
	}

}
