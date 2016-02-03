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
	private StateManipulator sm;
	
	public HelicopterAgentTileCodedQ8State() {
		int numTiles = getConfig().getInt("stateTiles");
		int numVariables = 8;
		int numTilings = getConfig().getInt("stateTilings");
		double[] statesMin = {-5, -5, -5, -12.566, -12.566, -12.566, -1, -1};
		double[] statesMax = {5, 5, 5,12.566, 12.566, 12.566, 1, 1};
		
		initialiseStateTiling(numVariables, statesMin, statesMax, numTiles, numTilings);

		
		numTiles = getConfig().getInt("actionTiles");
		numVariables = 4;
		numTilings = getConfig().getInt("actionTilings");
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
