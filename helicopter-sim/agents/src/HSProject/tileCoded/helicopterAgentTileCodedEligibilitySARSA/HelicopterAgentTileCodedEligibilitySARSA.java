package HSProject.tileCoded.helicopterAgentTileCodedEligibilitySARSA;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentSARSA;
import HSProject.tileCoded.tilings.StateActionPair;
import HSProject.tileCoded.tilings.Tile;
import HSProject.tileCoded.tilings.TileCodedHelicopterAction;
import marl.agents.selection.Argmax;

public class HelicopterAgentTileCodedEligibilitySARSA extends TileCodedAgentSARSA {
	private EligibilityQTable qTable = new EligibilityQTable(alpha, gamma, lambda);
	private StateActionVisitQueue savq;

	private static double alpha = 0.1;
	private static double gamma = 1;
	private static double lambda = 0;

	private int numStateTilings;
	private int numActionTilings;

	public HelicopterAgentTileCodedEligibilitySARSA() {
		super(alpha, gamma);

		int numTiles = 10;
		int numVariables = 12;
		numStateTilings = 16;
		double[] statesMin = { -5, -5, -5, -20, -20, -20, -12.566, -12.566, -12.566, -1, -1, -1 };
		double[] statesMax = { 5, 5, 5, 20, 20, 20, 12.566, 12.566, 12.566, 1, 1, 1 };

		initialiseStateTiling(numVariables, statesMin, statesMax, numTiles, numStateTilings);

		numTiles = 5;
		numVariables = 4;
		numActionTilings = 16;
		double[] actionsMin = { -1, -1, -1, -1 };
		double[] actionsMax = { 1, 1, 1, 1 };

		initialiseActionTiling(numVariables, actionsMin, actionsMax, numTiles, numActionTilings);

		qTable = new EligibilityQTable(alpha, gamma, lambda);
		savq = new StateActionVisitQueue(10 * numStateTilings * numActionTilings);

		setQTable(qTable);
	}

	@Override
	protected void learn(double reward, Action lastAction, Tile[] tiledLastStates, Tile[] tiledLastActions,
			Tile[] tiledCurStates) {

		// get the next Q values
		// Q(s',a')
		double nextQ[] = new double[numStateTilings];

		// Get all the tiles of the next actions
		// a' given s'
		Tile[] nextActions = new Tile[numActionTilings];
		getActionTileCoding().getTiles(nextActions, new TileCodedHelicopterAction(getNextAction()));

		for (int i = 0; i < numStateTilings; i++) {
			// Get the next states' Q values
			double qForNextAction[] = new double[numActionTilings];
			for (int j = 0; j < numActionTilings; j++) {
				qForNextAction[j] = qTable.getQValue(tiledCurStates[i], nextActions[j]);
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

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedEligibilitySARSA());
		L.run();
	}

}
