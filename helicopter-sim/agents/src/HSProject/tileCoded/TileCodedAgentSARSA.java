package HSProject.tileCoded;

import org.rlcommunity.rlglue.codec.types.Action;

import HSProject.tileCoded.tilings.Tile;
import HSProject.tileCoded.tilings.TileCodeQTableInterface;
import marl.agents.selection.Argmax;

/**
 * A {@link TileCodedAgent} with SARSA implemented
 * 
 * @author harrison
 *
 */
public abstract class TileCodedAgentSARSA extends TileCodedAgent {
	private double alpha;
	private double gamma;

	private int numStateTilings;
	private int numActionTilings;

	/**
	 * A new SARSA tile agent
	 * 
	 * @param alpha
	 *            the learning rate
	 * @param gamma
	 *            the discount factor
	 */
	public TileCodedAgentSARSA() {
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
	 * Implementation of SARSA. <br>
	 * <br>
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected void learn(double reward, Action lastAction, Tile[] tiledLastStates, Tile[] tiledLastActions,
			Tile[] tiledCurStates) {
		TileCodeQTableInterface qTable = getQTable();

		// get the next Q values
		// Q(s',a')
		double nextQ[] = new double[numStateTilings];

		// Get all the tiles of the next actions
		// a' given s'
		Tile[] nextActions = new Tile[numActionTilings];
		getActionTileCoding().getTiles(nextActions, getNextAction().doubleArray);

		for (int i = 0; i < numStateTilings; i++) {
			// Get the next states' Q values
			double qForNextAction[] = new double[numActionTilings];
			for (int j = 0; j < numActionTilings; j++) {
				qForNextAction[j] = qTable.getQValue(tiledCurStates[i], nextActions[j]);
			}
			nextQ[i] = Argmax.select(qForNextAction);

		}

		for (int i = 0; i < numStateTilings; i++) {
			for (int j = 0; j < numActionTilings; j++) {
				// Q(s,a)
				double curQ = qTable.getQValue(tiledLastStates[i], tiledLastActions[j]);
				for (int k = 0; k < numStateTilings; k++) {
					double val = curQ + ((alpha * (reward + (gamma * nextQ[k]) - curQ))
							/ (double) (numStateTilings * numActionTilings * numStateTilings));
					qTable.put(tiledLastStates[i], tiledLastActions[j], val, lastAction);
				}

			}
		}
	}

	/**
	 * Implementation of SARSA. <br>
	 * <br>
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected void learnEnd(double reward, Tile[] tiledLastStates, Tile[] tiledLastActions) {
		TileCodeQTableInterface qTable = getQTable();
		for (int i = 0; i < numStateTilings; i++) {
			for (int j = 0; j < numActionTilings; j++) {
				double curQ = getQTable().getQValue(tiledLastStates[i], tiledLastActions[j]);
				double val = curQ + ((alpha * (reward - curQ)) / (double) (numStateTilings * numActionTilings));
				qTable.put(tiledLastStates[i], tiledLastActions[j], val, getNextAction());
			}

		}
	}
	
	protected double getGamma(){
		return gamma;
	}

	protected int getStateTilings(){
		return numStateTilings;
	}
	
	protected int getActionTilings(){
		return numActionTilings;
	}
}
