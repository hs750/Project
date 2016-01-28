package HSProject.tileCoded.helicopterAgentTileCodedEligibilitySARSA;



import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentSARSA;
import HSProject.tileCoded.tilings.Tile;
import HSProject.tileCoded.tilings.TileCodedHelicopterAction;


public class HelicopterAgentTileCodedEligibilitySARSA extends TileCodedAgentSARSA {
	private EligibilityQTable qTable = new EligibilityQTable(alpha, gamma, lambda);
	
	private static double alpha = 0.1;
	private static double gamma = 1;
	private static double lambda = 0.9999;
	
	private int numStateTilings;
	private int numActionTilings;

	public HelicopterAgentTileCodedEligibilitySARSA() {
		super(alpha, gamma);
		
		int numTiles = 10;
		int numVariables = 12;
		numStateTilings = 16;
		double[] statesMin = {-5, -5, -5, -20, -20, -20, -12.566, -12.566, -12.566, -1, -1, -1};
		double[] statesMax = {5, 5, 5, 20, 20, 20, 12.566, 12.566, 12.566, 1, 1, 1};
		
		initialiseStateTiling(numVariables, statesMin, statesMax, numTiles, numStateTilings);
		
		numTiles = 5;
		numVariables = 4;
		numActionTilings = 16;
		double[] actionsMin = {-1, -1, -1, -1};
		double[] actionsMax = {1, 1, 1, 1};

		initialiseActionTiling(numVariables, actionsMin, actionsMax, numTiles, numActionTilings);
		
		qTable = new EligibilityQTable(alpha, gamma, lambda);
		
		setQTable(qTable);
	}
	
	@Override
	protected void learn(double reward, Action lastAction, Tile[] curStates, Tile[] actions, Tile[] newStates) {
		
		// get the current Q values
		double newQ[][] = new double[numStateTilings][numActionTilings];
		
		// Get all the tiles of the next actions
		Tile[] nextActions = new Tile[numActionTilings];
		getActionTileCoding().getTiles(nextActions, new TileCodedHelicopterAction(getNextAction()));
					
		for (int i = 0; i < numStateTilings; i++) {
			// Get the new states' Q values
			for (int j = 0; j < numActionTilings; j++) {
				newQ[i][j] = qTable.getQValue(newStates[i], nextActions[j]);
			}

		}
		
		for( int i=0; i<numStateTilings; i++ ) {
	    	for(int j = 0; j < numActionTilings; j++){
	    		
	    		double curQ  = qTable.getQValue(curStates[i], actions[j]);
	    		double delta  = ( reward + (gamma * newQ[i][j]) - curQ ) / (double) (numStateTilings * numActionTilings); 	    		
	    		
	    		qTable.eligibilityUpdate(curStates[i], actions[j], delta, getNextAction());
	    		
	    	}
	        
	    }
	}
	
	@Override
	protected void learnEnd(double reward, Tile[] curStates, Tile[] actions) {
		for( int i=0; i<numStateTilings; i++ ) {
	    	for(int j = 0; j < numActionTilings; j++){
	    		
	    		double curQ  = qTable.getQValue(curStates[i], actions[j]);
	    		double delta  = ( reward - curQ ) / (double) (numStateTilings * numActionTilings); 	    		
	    		
	    		qTable.eligibilityUpdate(curStates[i], actions[j], delta, getNextAction());
	    		
	    	}
	        
	    }
	}
	
	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedEligibilitySARSA());
		L.run();
	}

}
