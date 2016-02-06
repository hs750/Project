package HSProject.tileCoded;

import org.rlcommunity.rlglue.codec.types.Action;

import HSProject.tileCoded.tilings.Tile;
import HSProject.tileCoded.tilings.TileCodeQTableInterface;

/**
 * A {@link TileCodedAgent} with Q-Learning implemented
 * 
 * @author harrison
 *
 */
public abstract class TileCodedAgentQ extends TileCodedAgent {
	private double alpha;
	private double gamma;

	private int numStateTilings;
	private int numActionTilings;

	/**
	 * A new Q learning tile agent
	 * 
	 * @param alpha
	 *            the learning rate
	 * @param gamma
	 *            the discount factor
	 */
	public TileCodedAgentQ() {
		this.alpha = getConfig().getDouble("alpha");
		this.gamma = getConfig().getDouble("gamma");
		System.out.println("Alpha=" + alpha);
		System.out.println("Gamma=" + gamma);
	}

	@Override
	protected void initialiseStateTiling(int numStateVariables, double[] statesMin, double[] statesMax, int numTiles,
			int numTilings) {
		numStateTilings = numTilings;
		super.initialiseStateTiling(numStateVariables, statesMin, statesMax, numTiles, numTilings);
	}

	@Override
	protected void initialiseActionTiling(int numActionVariables, double[] actionsMin, double[] actionsMax,
			int numTiles, int numTilings) {
		numActionTilings = numTilings;
		super.initialiseActionTiling(numActionVariables, actionsMin, actionsMax, numTiles, numTilings);
	}

	/**
	 * Implementation of Q-Learning. <br>
	 * <br>
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected void learn(double reward, Action lastAction, Tile[] tiledLastStates, Tile[] tiledLastActions,
			Tile[] tiledCurStates) {
		TileCodeQTableInterface qTable = getQTable();

		// get the new Q values
		// newQ for state tile i
		double newQ[] = new double[numStateTilings];

		for (int i = 0; i < numStateTilings; i++) {
			// Get the new states' Q values
			// max_a Q(s',a')
			newQ[i] = qTable.getMaxQValue(tiledCurStates[i]);
		}

		for (int i = 0; i < numStateTilings; i++) {
			for (int j = 0; j < numActionTilings; j++) {
				// Q(s,a)
				double curQ = qTable.getQValue(tiledLastStates[i], tiledLastActions[j]);
				for (int k = 0; k < numStateTilings; k++) {
					double val = curQ + ((alpha * (reward + (gamma * newQ[k]) - curQ))
							/ (double) (numStateTilings * numActionTilings * numStateTilings));

					qTable.put(tiledLastStates[i], tiledLastActions[j], val, lastAction);
				}
			}

		}
	}

	/**
	 * Implementation of Q-Learning. <br>
	 * <br>
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected void learnEnd(double reward, Tile[] tiledLastStates, Tile[] tiledLastActions) {
		TileCodeQTableInterface qTable = getQTable();
		for (int i = 0; i < numStateTilings; i++) {
			for (int j = 0; j < numActionTilings; j++) {
				// Q(s,a)
				double curQ = qTable.getQValue(tiledLastStates[i], tiledLastActions[j]);
				double val = curQ + ((alpha * (reward - curQ)) / (double) (numStateTilings * numActionTilings));
				qTable.put(tiledLastStates[i], tiledLastActions[j], val, getNextAction()); // commit
																							// the
																							// update
																							// to
																							// the
																							// Q
																							// table
			}

		}
	}

}
