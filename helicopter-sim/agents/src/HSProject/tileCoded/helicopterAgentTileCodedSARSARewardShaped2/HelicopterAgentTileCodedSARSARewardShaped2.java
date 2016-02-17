package HSProject.tileCoded.helicopterAgentTileCodedSARSARewardShaped2;



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
public class HelicopterAgentTileCodedSARSARewardShaped2 extends TileCodedAgentSARSA {
	private int steps = 0;
	public HelicopterAgentTileCodedSARSARewardShaped2() {
		
		
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
		super.agent_end(getFinalReward());
	}
	
	/**
	 * Shape the reward before passing it to the learning process.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Action agent_step(double reward, Observation o) {
		return super.agent_step(getReward(o), o);
	}
	
	private double getReward(Observation o){
		double reward = 0;
		// reward only made up of distances from desired point, nothing else
		reward -= Math.pow(o.getDouble(3), 2);
		reward -= Math.pow(o.getDouble(4), 2);
		reward -= Math.pow(o.getDouble(5), 2);
		return reward;
	}
	
	private double getFinalReward(){
		// the maximum negative reward for the remainder of the episode
		double reward = Math.pow(20, 2)*3;
		return -1 * reward * (6000-steps);
	}
	
	@Override
	public Action agent_start(Observation o) {
		steps = 0;
		return super.agent_start(o);
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedSARSARewardShaped2());
		L.run();
	}

}
