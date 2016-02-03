package HSProject.tileCoded.helicopterAgentTileCodedSARSAGuided;



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
public class HelicopterAgentTileCodedSARSAGuided extends TileCodedAgentSARSA {
	
	private double noiseWidth;

	public HelicopterAgentTileCodedSARSAGuided() {
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
		
		noiseWidth = getConfig().getDouble("noise");
	}
	
	/**
	 * An action generated by applying noise to the policy generated by
	 * {@link #agent_policy(Observation, Action)}
	 */
	@Override
	protected Action randomAction(Observation o){
		Action a = new Action(0, 4);
		agent_policy(o, a);
		
		double divisor = 1.0 / noiseWidth;
		double adjustor = noiseWidth / 2.0;
		a.doubleArray[0] += (randGenerator.nextDouble() / divisor) - adjustor;
		a.doubleArray[1] += (randGenerator.nextDouble() / divisor) - adjustor;
		a.doubleArray[2] += (randGenerator.nextDouble() / divisor) - adjustor;
		a.doubleArray[3] += (randGenerator.nextDouble() / divisor) - adjustor;
		
		for(int i = 0; i < 4; i++){
			if(a.doubleArray[i] > 1){
				a.doubleArray[i] = 1;
			}else if(a.doubleArray[i] < -1){
				a.doubleArray[i] = -1;
			}
		}
		return a;
		
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedSARSAGuided());
		L.run();
	}

}
