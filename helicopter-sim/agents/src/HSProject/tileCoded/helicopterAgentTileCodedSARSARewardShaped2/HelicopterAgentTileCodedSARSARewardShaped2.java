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
		super.agent_end(reward);
	}
	
	/**
	 * Shape the reward before passing it to the learning process.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Action agent_step(double reward, Observation o) {
		return super.agent_step(getReward(o, reward), o);
	}
	
	private double getReward(Observation o, double reward){
		// reward only made up of distances from desired point, nothing else
		//reward -= Math.pow(o.getDouble(3), 2);
		//reward -= Math.pow(o.getDouble(4), 2);
		//reward += Math.pow(o.getDouble(5), 3); // downward is most crucial being above the target actually is good
		
		// want low velocity
		//reward -= Math.pow(o.getDouble(0), 2);
		//reward -= Math.pow(o.getDouble(1), 2);
//		if(o.getDouble(5) < 0){
//			if(o.getDouble(2) < 0){
//				reward -= Math.pow(o.getDouble(2), 4); // want negative downward velocity (aka upward velocity
//			}else{
//				reward += Math.pow(o.getDouble(2), 4); // want negative downward velocity (aka upward velocity
//			}
//			
//		}
		
//		reward += (o.getDouble(3) > 0 ? -o.getDouble(0) : o.getDouble(0));
//		reward += (o.getDouble(4) > 0 ? -o.getDouble(1) : o.getDouble(1));
//		reward += (o.getDouble(5) > 0 ? -o.getDouble(2) : o.getDouble(2));
		
		reward -= o.getDouble(3) * o.getDouble(0);
		reward -= o.getDouble(4) * o.getDouble(1);
		reward -= o.getDouble(5) * o.getDouble(2);
		
//		reward += o.getDouble(6) * o.getDouble(9);
//		reward += o.getDouble(7) * o.getDouble(10);
//		reward += o.getDouble(8) * o.getDouble(11);
		
		
		//reward -= Math.pow(o.getDouble(2), 7);
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
