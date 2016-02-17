package HSProject.tileCoded.helicopterAgentTileCodedSARSARewardShapedPolicy;



import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentSARSA;

/**
 * An experiment implementing SARSA. The reward received from the
 * environment is shapped giving better rewards for producing rewards close to
 * {@link #agent_policy(Observation, Action)}
 * 
 * @author harrison
 *
 */
public class HelicopterAgentTileCodedSARSARewardShapedPolicy extends TileCodedAgentSARSA {
	
	public HelicopterAgentTileCodedSARSARewardShapedPolicy() {
		
		
		int numTiles = getConfig().getInt("stateTiles");
		int numVariables = 12;
		int numTilings = getConfig().getInt("stateTilings");
		double[] statesMin = { -5, -5, -5, -20, -20, -20, -12.566, -12.566, -12.566, -1, -1, -1 };
		double[] statesMax = { 5, 5, 5, 20, 20, 20, 12.566, 12.566, 12.566, 1, 1, 1 };

		initialiseStateTiling(numVariables, statesMin, statesMax, numTiles, numTilings);

		numTiles = getConfig().getInt("actionTiles");
		numVariables = 4;
		numTilings = getConfig().getInt("actionTilings");
		double[] actionsMin = {-1, -1, -1, -1};
		double[] actionsMax = {1, 1, 1, 1};

		initialiseActionTiling(numVariables, actionsMin, actionsMax, numTiles, numTilings);
	}
	
	@Override
	public void agent_end(double reward) {
		super.agent_end(getReward() - 1000);
	}
	
	/**
	 * Shape the reward before passing it to the learning process.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Action agent_step(double reward, Observation o) {
		return super.agent_step(getReward(), o);
	}
	
	private double getReward(){
		Observation lastState = getLastState();
		Action lastAction = getAction();
		
		Action bestAction = new Action(0 ,4);
		agent_policy(lastState, bestAction);
		
		double absDif = 0;
		for(int i = 0; i < 4; i++){
			absDif += Math.abs(lastAction.getDouble(i) - bestAction.getDouble(i));
		}
		return absDif;
		
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedSARSARewardShapedPolicy());
		L.run();
	}

}
