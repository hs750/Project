package HSProject.tileCoded.helicopterAgentTileCodedSARSAGreedyGuided;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentSARSA;
import HSProject.tileCoded.tilings.Tile;
import HSProject.tileCoded.tilings.TileCodeQTableInterface.ActionValue;

/**
 * An experiment implementing SARSA. The random actions guided by the hard coded
 * controller provided by the rl-competition with some added noise. This is
 * greedy because during learning it will generate an action from the hard coded
 * controller and use that if that action has a better value than the one chosen
 * from the q table. This will be picked often as the default q value is 0 and
 * the q values will only decrease.
 * 
 * @author harrison
 *
 */
public class HelicopterAgentTileCodedSARSAGreedyGuided extends TileCodedAgentSARSA {
	private static double alpha = 0.1;
	private static double gamma = 1;
	private int numStateTilings;
	private int numActionTilings;

	public HelicopterAgentTileCodedSARSAGreedyGuided() {
		super(alpha, gamma);

		int numTiles = 10;
		int numVariables = 12;
		int numTilings = 16;
		numStateTilings = numTilings;
		double[] statesMin = { -5, -5, -5, -20, -20, -20, -12.566, -12.566, -12.566, -1, -1, -1 };
		double[] statesMax = { 5, 5, 5, 20, 20, 20, 12.566, 12.566, 12.566, 1, 1, 1 };

		initialiseStateTiling(numVariables, statesMin, statesMax, numTiles, numTilings);

		numTiles = 5;
		numVariables = 4;
		numTilings = 16;
		numActionTilings = numTilings;
		double[] actionsMin = { -1, -1, -1, -1 };
		double[] actionsMax = { 1, 1, 1, 1 };

		initialiseActionTiling(numVariables, actionsMin, actionsMax, numTiles, numTilings);
	}

	/**
	 * An action generated by applying noise to the policy generated by
	 * {@link #agent_policy(Observation, Action)}
	 */
	@Override
	protected Action randomAction(Observation o) {
		Action a = new Action(0, 4);
		agent_policy(o, a);

		double widthAdjust = 5;
		a.doubleArray[0] += randGenerator.nextGaussian() / widthAdjust;
		a.doubleArray[1] += randGenerator.nextGaussian() / widthAdjust;
		a.doubleArray[2] += randGenerator.nextGaussian() / widthAdjust;
		a.doubleArray[3] += randGenerator.nextGaussian() / widthAdjust;

		for (int i = 0; i < 4; i++) {
			if (a.doubleArray[i] > 1) {
				a.doubleArray[i] = 1;
			} else if (a.doubleArray[i] < -1) {
				a.doubleArray[i] = -1;
			}
		}
		return a;

	}

	/**
	 *
	 * Selects a random action with probability 1-epsilon, and the action with
	 * the highest value otherwise. A hard coded policy action will be taken if
	 * the value for that action is higher than the one chosen from the q table.
	 * 
	 * @param theState
	 *            the state to base the action selection off of.
	 * @return the action selected
	 */
	@Override
	protected Action egreedy(Observation theState) {
		if (!isExploringFrozen()) {
			setExplorationAction(randGenerator.nextDouble() <= epsilon);
			if (isExplorationAction()) {
				return randomAction(theState);
			}
		}

		Tile[] tiles = new Tile[numStateTilings];
		getStateTileCoding().getTiles(tiles, theState.doubleArray);

		ActionValue maxAction = null;
		for (int i = 0; i < numStateTilings; i++) {
			ActionValue av = getQTable().getMaxAction(tiles[i]);
			if (maxAction == null) {
				maxAction = av;
			} else if (av.getValue() > maxAction.getValue()) {
				maxAction = av;
			}
		}

		Action greedyAction = new Action(0, 4);
		agent_policy(theState, greedyAction);

		Tile[] greedyTiles = new Tile[numActionTilings];
		getActionTileCoding().getTiles(greedyTiles, theState.doubleArray);

		if (maxAction.getAction() == null) {
			return greedyAction;
		}

		for (Tile st : tiles) {
			for (Tile gt : greedyTiles) {
				double gv = getQTable().getQValue(st, gt);
				if (gv > maxAction.getValue()) {
					return greedyAction;
				}
			}
		}

		return maxAction.getAction();
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedSARSAGreedyGuided());
		L.run();
	}

}
