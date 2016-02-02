package HSProject.tileCoded.helicopterAgentTileCodedQRewardShaped;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentQ;

/**
 * An experiment implementing Q-Learning. The reward received from the
 * environment is shapped giving better rewards for producing rewards close to
 * {@link #agent_policy(Observation, Action)}
 * 
 * @author harrison
 *
 */
public class HelicopterAgentTileCodedQRewardShaped extends TileCodedAgentQ {
	private static double alpha = 0.1;
	private static double gamma = 1;

	public HelicopterAgentTileCodedQRewardShaped() {
		super(alpha, gamma);

		int numTiles = 10;
		int numVariables = 12;
		int numTilings = 16;
		double[] statesMin = { -5, -5, -5, -20, -20, -20, -12.566, -12.566, -12.566, -1, -1, -1 };
		double[] statesMax = { 5, 5, 5, 20, 20, 20, 12.566, 12.566, 12.566, 1, 1, 1 };

		initialiseStateTiling(numVariables, statesMin, statesMax, numTiles, numTilings);

		numTiles = 5;
		numVariables = 4;
		numTilings = 16;
		double[] actionsMin = { -1, -1, -1, -1 };
		double[] actionsMax = { 1, 1, 1, 1 };

		initialiseActionTiling(numVariables, actionsMin, actionsMax, numTiles, numTilings);
	}

	/**
	 * Shape the reward before passing it to the learning process.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Action agent_step(double reward, Observation o) {
		Observation lastState = getLastState();
		Action lastAction = getAction();

		Action bestAction = new Action(0, 4);
		agent_policy(lastState, bestAction);

		double absDif = 0;
		for (int i = 0; i < 4; i++) {
			absDif += Math.abs(lastAction.getDouble(i) - bestAction.getDouble(i));
		}

		return super.agent_step(reward - absDif, o);
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedQRewardShaped());
		L.run();
	}

}
