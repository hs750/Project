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
	protected void learn(double reward, Action lastAction, Tile[] tiledLastStates, Tile[] tiledLastActions, Tile[] tiledCurStates) {
		TileCodeQTableInterface qTable = getQTable();
		
		// get the current Q values
        double newQ[] = new double[numStateTilings];
        
		for( int i=0; i<numStateTilings; i++ ) {
            // Get the new states' Q values
			// max_a Q(s',a')
            newQ[i]    = qTable.getMaxQValue(tiledCurStates[i]);
	    }
		
		for( int i=0; i<numStateTilings; i++ ) {
	    	for(int j = 0; j < numActionTilings; j++){
	    		//Q(s,a)
	    		double curQ  = qTable.getQValue(tiledLastStates[i], tiledLastActions[j]);
	    		double val   = curQ + (( alpha * (reward + (gamma*newQ[i]) - curQ)) / (double)(numStateTilings*numActionTilings));
	    		
    	        qTable.put(tiledLastStates[i], tiledLastActions[j], val, lastAction);   // commit the update to the Q table
	    	}
	        
	    }
	}
	
	@Override
	protected void learnEnd(double reward, Tile[] tiledLastStates, Tile[] tiledLastActions) {
		TileCodeQTableInterface qTable = getQTable();
		for( int i=0; i<numStateTilings; i++ ) {
	    	for(int j = 0; j < numActionTilings; j++){
	    		//Q(s,a)
	    		double curQ  = qTable.getQValue(tiledLastStates[i], tiledLastActions[j]);
    	        double val   = curQ + (( alpha * (reward - curQ)) / (double)(numStateTilings*numActionTilings));
    	        qTable.put(tiledLastStates[i], tiledLastActions[j], val, getNextAction());   // commit the update to the Q table
	    	}
	        
	    }
	}

}
