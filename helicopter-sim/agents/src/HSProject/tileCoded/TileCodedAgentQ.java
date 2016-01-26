package HSProject.tileCoded;

import org.rlcommunity.rlglue.codec.types.Action;

import HSProject.tileCoded.tilings.Tile;
import HSProject.tileCoded.tilings.TileCodeQTableInterface;

public abstract class TileCodedAgentQ extends TileCodedAgent{
	private double alpha;
	private double gamma;
	
	private int numStateTilings;
	private int numActionTilings;
	

	public TileCodedAgentQ(double alpha, double gamma) {
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
	protected void learn(double reward, Action lastAction, Tile[] curStates, Tile[] actions, Tile[] newStates) {
		TileCodeQTableInterface qTable = getQTable();
		
		// get the current Q values
        double newQ[] = new double[numStateTilings];
        
		for( int i=0; i<numStateTilings; i++ ) {
            // Get the new states' Q values
            newQ[i]    = qTable.getMaxQValue(newStates[i]);
	    }
		
		for( int i=0; i<numStateTilings; i++ ) {
	    	for(int j = 0; j < numActionTilings; j++){
	    		double curQ  = qTable.getQValue(curStates[i], actions[j]);
	    		double val   = curQ + (( alpha * (reward + (gamma*newQ[i]) - curQ)) / (double)numStateTilings);
	    		
    	        qTable.put(curStates[i], actions[j], val, lastAction);   // commit the update to the Q table
	    	}
	        
	    }
	}
	
	@Override
	protected void learnEnd(double reward, Tile[] curStates, Tile[] actions) {
		TileCodeQTableInterface qTable = getQTable();
		for( int i=0; i<numStateTilings; i++ ) {
	    	for(int j = 0; j < numActionTilings; j++){
	    		double curQ  = qTable.getQValue(curStates[i], actions[j]);
    	        double val   = curQ + (( alpha * (reward - curQ)) / (double)(numStateTilings*numActionTilings));
    	        qTable.put(curStates[i], actions[j], val, getCurrentAction());   // commit the update to the Q table
	    	}
	        
	    }
	}

}
