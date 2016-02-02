package HSProject.tileCoded.helicopterAgentTileCodedSARSARewardShaped;



import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentSARSA;

/**
 * An experiment implementing SARSA. The random actions guided by the hard
 * coded controller provided by the rl-competition with some added noise.
 * 
 * @author harrison
 *
 */
public class HelicopterAgentTileCodedSARSARewardShaped extends TileCodedAgentSARSA {
	private static double alpha = 0.1;
	private static double gamma = 1;
	
	public HelicopterAgentTileCodedSARSARewardShaped() {
		super(alpha, gamma);
		
		
		int numTiles = 10;
		int numVariables = 12;
		int numTilings = 16;
		double[] statesMin = {-5, -5, -5, -20, -20, -20, -12.566, -12.566, -12.566, -1, -1, -1};
		double[] statesMax = {5, 5, 5, 20, 20, 20, 12.566, 12.566, 12.566, 1, 1, 1};
		
		initialiseStateTiling(numVariables, statesMin, statesMax, numTiles, numTilings);

		
		numTiles = 5;
		numVariables = 4;
		numTilings = 16;
		double[] actionsMin = {-1, -1, -1, -1};
		double[] actionsMax = {1, 1, 1, 1};

		initialiseActionTiling(numVariables, actionsMin, actionsMax, numTiles, numTilings);
	}
	
	@Override
	public Action agent_step(double reward, Observation o) {
		Observation lastState = getLastState();
		Action lastAction = getAction();
		
		Action bestAction = new Action(0 ,4);
		agent_policy(lastState, bestAction);
		
		double absDif = 0;
		for(int i = 0; i < 4; i++){
			absDif += Math.abs(lastAction.getDouble(i) - bestAction.getDouble(i));
		}
		
		return super.agent_step(reward - absDif, o);
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedSARSARewardShaped());
		L.run();
	}

}
