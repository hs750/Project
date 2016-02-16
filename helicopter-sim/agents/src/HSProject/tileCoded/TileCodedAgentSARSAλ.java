package HSProject.tileCoded;

import org.rlcommunity.rlglue.codec.types.Action;

import HSProject.tileCoded.tilings.EligibilityQTable;
import HSProject.tileCoded.tilings.StateActionPair;
import HSProject.tileCoded.tilings.StateActionVisitQueue;
import HSProject.tileCoded.tilings.Tile;
import marl.agents.selection.Argmax;

/**
 * A {@link TileCodedAgent} with SARSA(λ) implemented.
 * 
 * @author harrison
 *
 */
public abstract class TileCodedAgentSARSAλ extends TileCodedAgent{
	private EligibilityQTable qTable;
	private StateActionVisitQueue savq;
	
	private double alpha;
	private double gamma;
	private double lambda;

	private int numStateTilings;
	private int numActionTilings;

	/**
	 * A new SARSA(λ) learning agent.
	 * 
	 * @param alpha
	 *            the learning rate
	 * @param gamma
	 *            the discount factor
	 * @param lambda
	 *            the eligibility trace parameter
	 */
	public TileCodedAgentSARSAλ() {
		this.alpha = getConfig().getDouble("alpha");
		this.gamma = getConfig().getDouble("gamma");
		this.lambda = getConfig().getDouble("lambda");
		
		qTable = new EligibilityQTable();
		setQTable(qTable);
		
		System.out.println("Alpha=" + alpha);
		System.out.println("Gamma=" + gamma);
		System.out.println("Lambda=" + lambda);
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
	 * Implementation of SARSA(λ) <br>
	 * <br>
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected void learn(double reward, Action lastAction, Tile[] tiledLastStates, Tile[] tiledLastActions,
			Tile[] tiledCurStates) {
		//Initialise savq here as the size of this is the only time tiling numbers are known
		if(savq == null){
			savq = new StateActionVisitQueue(Math.max(10 * numStateTilings * numActionTilings, 2000));
		}

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
				
				// Here for convenience as don't want to have to do this nested loop again just for this.
				savq.add(new StateActionPair(tiledLastStates[i], tiledLastActions[j]));
				savq.add(new StateActionPair(tiledCurStates[i], nextActions[j]));
			}
			nextQ[i] = Argmax.select(qForNextAction);

		}

		for (int i = 0; i < numStateTilings; i++) {
			for (int j = 0; j < numActionTilings; j++) {
				// Q(s,a)
				Tile lastS = tiledLastStates[i];
				Tile lastA = tiledLastActions[j];
				double curQ = qTable.getQValue(lastS, lastA);
				double eligibility = qTable.getEligibility(lastS, lastA) + 1;
				qTable.updateEligibility(lastS, lastA, eligibility);

				for (int k = 0; k < numStateTilings; k++) {
					double delta = (reward + (gamma * nextQ[k]) - curQ)
							/ (double) (numStateTilings * numActionTilings * numStateTilings);

					savq.forEach((s, a) -> {
						double e = qTable.getEligibility(s, a);
						double q = qTable.getQValue(s, a);

						Action putAction = null;
						if(s.equals(lastS) && a.equals(lastA)){
							putAction = lastAction;
						}
						qTable.put(s, a, q + alpha * delta * e, putAction);
						qTable.updateEligibility(s, a, gamma * lambda * e);
					});
				}

			}
		}
	}

	/**
	 * Implementation of SARSA(λ) <br>
	 * <br>
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected void learnEnd(double reward, Tile[] curStates, Tile[] actions) {
		for (int i = 0; i < numStateTilings; i++) {
			for (int j = 0; j < numActionTilings; j++) {
				Tile lastS = curStates[i];
				Tile lastA = actions[j];
				double curQ = qTable.getQValue(lastS, lastA);
				double delta = (reward - curQ) / (double) (numStateTilings * numActionTilings);

				double eligibility = qTable.getEligibility(lastS, lastA) + 1;
				qTable.updateEligibility(lastS, lastA, eligibility);

				savq.forEach((s, a) -> {
					double e = qTable.getEligibility(s, a);
					double q = qTable.getQValue(s, a);

					Action putAction = null;
					if(s.equals(lastS) && a.equals(lastA)){
						putAction = getNextAction();
					}
					qTable.put(s, a, q + alpha * delta * e, putAction);
					qTable.updateEligibility(s, a, gamma * lambda * e);
				});

			}

		}
	}
}
