package HSProject.tileCoded.helicopterAgentTileCodedSARSARewardShapedPotential;



import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentSARSA;
import HSProject.tileCoded.tilings.Tile;

/**
 * An experiment implementing SARSA. The reward received from the
 * environment is shapped giving better rewards for producing rewards close to
 * {@link #agent_policy(Observation, Action)}
 * 
 * @author harrison
 *
 */
public class HelicopterAgentTileCodedSARSARewardShapedPotential extends TileCodedAgentSARSA {
	private boolean usePotential;
	private int potentialFunction;
	
	public HelicopterAgentTileCodedSARSARewardShapedPotential() {
		
		
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
		
		usePotential = getConfig().getBoolean("usePotential");
		System.out.println("Potential Reward Shaping=" + usePotential);
		potentialFunction = getConfig().getInt("potentialFunction");
		System.out.println("Shaping Function=" + potentialFunction);
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
	
	/**
	 * Potential based reward shaping
	 * @param o
	 * @param reward
	 * @return
	 */
	private double getReward(Observation o, double reward){
		if(usePotential){
			Observation lastState = getLastState();
			double r = getR(lastState);
			double r2 = getR(o);
			
			double F = getGamma() * r2 - r;
			return reward + F;
		}else{
			return reward + getR(o);
		}
		
		
	}
	
	/**
	 * The real-valued funciton of state
	 * @param s
	 * @return
	 */
	private double getR(Observation s){
		double r = 0;
		switch(potentialFunction){
		case 0:
		default:
			r -= s.getDouble(3) * s.getDouble(0);
			r -= s.getDouble(4) * s.getDouble(1);
			r -= s.getDouble(5) * s.getDouble(2);
			break;
		case 1:
			r -= s.getDouble(3) * s.getDouble(0);
			r -= s.getDouble(4) * s.getDouble(1);
			r -= s.getDouble(5) * s.getDouble(2);
			r += s.getDouble(6) * s.getDouble(9);
			r += s.getDouble(7) * s.getDouble(10);
			r += s.getDouble(8) * s.getDouble(11);
			break;
		case 2:
			r -= s.getDouble(3) * s.getDouble(0) * 2;
			r -= s.getDouble(4) * s.getDouble(1) * 2;
			r -= s.getDouble(5) * s.getDouble(2) * 2;
			break;
		case 3:
			r -= s.getDouble(3) * s.getDouble(0) * 3;
			r -= s.getDouble(4) * s.getDouble(1) * 3;
			r -= s.getDouble(5) * s.getDouble(2) * 3;
			break;
		case 4:
			Tile[] stateTiles = new Tile[getStateTilings()];
			getStateTileCoding().getTiles(stateTiles, s.doubleArray);
			double val = 0;
			for(Tile t : stateTiles){
				val += getQTable().getMaxQValue(t);
			}
			r = Math.abs(val) > 50 ? Math.signum(val) * 50 : val;
		}
		
		
		return r;
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedSARSARewardShapedPotential());
		L.run();
	}

}
