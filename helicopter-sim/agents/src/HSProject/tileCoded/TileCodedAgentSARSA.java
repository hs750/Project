package HSProject.tileCoded;

import org.rlcommunity.rlglue.codec.types.Action;

import HSProject.tileCoded.tilings.Tile;
import HSProject.tileCoded.tilings.TileCodeQTableInterface;
import HSProject.tileCoded.tilings.TileCodedHelicopterAction;

public abstract class TileCodedAgentSARSA extends TileCodedAgent{
	private double alpha;
	private double gamma;
	
	private int numStateTilings;
	private int numActionTilings;
	

	public TileCodedAgentSARSA(double alpha, double gamma) {
		super(alpha, gamma);
		this.alpha = alpha;
		this.gamma = gamma;
	}
	
	@Override
	protected void initialiseStateTiling(int numStateVariables, double[] statesMin, double[] statesMax, int numTiles,
			int numTilings) {
		numStateTilings = numTilings;
		super.initialiseStateTiling(numStateVariables, statesMin, statesMax, numTiles, numTilings);
	}

	@Override
	protected void initialiseActionTiling(int numActionVariables, double[] actionsMin, double[] actionsMax, int numTiles,
			int numTilings) {
		numActionTilings = numTilings;
		super.initialiseActionTiling(numActionVariables, actionsMin, actionsMax, numTiles, numTilings);
	}
	
	@Override
	protected void learn(double reward, Action lastAction, Tile[] tiledLastStates, Tile[] tiledLastActions, Tile[] tiledCurStates) {
		TileCodeQTableInterface qTable = getQTable();
		
		// get the current Q values
		double newQ[][] = new double[numStateTilings][numActionTilings];
		
		// Get all the tiles of the next actions
		Tile[] nextActions = new Tile[numActionTilings];
		getActionTileCoding().getTiles(nextActions, new TileCodedHelicopterAction(getNextAction()));
					
		for (int i = 0; i < numStateTilings; i++) {
			// Get the new states' Q values
			for (int j = 0; j < numActionTilings; j++) {
				newQ[i][j] = qTable.getQValue(tiledCurStates[i], nextActions[j]);
			}

		}
		
		for (int i = 0; i < numStateTilings; i++) {
			for (int j = 0; j < numActionTilings; j++) {
				
				double curQ = qTable.getQValue(tiledLastStates[i], tiledLastActions[j]);
				double val = curQ + ((alpha * (reward + (gamma * newQ[i][j]) - curQ))
						/ (double) (numStateTilings * numActionTilings));

				qTable.put(tiledLastStates[i], tiledLastActions[j], val, lastAction); // commit
																		// the
																		// update
																		// to
																		// the
																		// Q
																		// table

			}

		}
	}
	
	@Override
	protected void learnEnd(double reward, Tile[] tiledLastStates, Tile[] tiledLastActions) {
		TileCodeQTableInterface qTable = getQTable();
		for (int i = 0; i < numStateTilings; i++) {
			for (int j = 0; j < numActionTilings; j++) {
				double curQ = getQTable().getQValue(tiledLastStates[i], tiledLastActions[j]);
				double val = curQ + ((alpha * (reward - curQ)) / (double) (numStateTilings * numActionTilings));
				qTable.put(tiledLastStates[i], tiledLastActions[j], val, getNextAction()); // commit
																	// the
																	// update
																	// to
																	// the Q
																	// table
			}

		}
	}

}
