package HSProject.helicopterAgentTileCodedSARSAGaussian;



import java.util.HashMap;
import java.util.Random;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.TileCodeQTable;
import HSProject.TileCodeQTableFlat;
import HSProject.TileCodeQTableInterface;
import HSProject.TileCodeQTableInterface.ActionValue;
import HSProject.TileCodedHelicopterAction;
import HSProject.TileCodedHelicopterState;
import HSProject.TileCoding;
import HSProject.Tile;

@SuppressWarnings("unused")
public class HelicopterAgentTileCodedSARSAGaussian implements AgentInterface {
	private TileCodeQTableInterface qTable;
	private TileCoding stateTileCoding;
	private TileCoding actionTileCoding;
	
	int numStateTilings = 32;
	int numActionTilings = 4;
	
	private Action action;
	private Observation lastState;
	
	protected Random randGenerator = new Random();
	private double epsilon = 0.1;
	private boolean exploringFrozen = false;

	TaskSpec TSO = null;
	
	double alpha = 0.1;
	double gamma = 1;
	
	

	// Indices into observation_t.doubleArray...
	private static int u_err = 0, // forward velocity
			v_err = 1, // sideways velocity
			w_err = 2, // downward velocity
			x_err = 3, // forward error
			y_err = 4, // sideways error
			z_err = 5, // downward error
			p_err = 6, // angular rate around forward axis
			q_err = 7, // angular rate around sideways (to the right) axis
			r_err = 8, // angular rate around vertical (downward) axis
			qx_err = 9, // <-- quaternion entries, x,y,z,w q = [ sin(theta/2) *
						// axis; cos(theta/2)],
			qy_err = 10, // where axis = axis of rotation; theta is amount of
							// rotation around that axis
			qz_err = 11; // [recall: any rotation can be represented by a single
							// rotation around some axis]

	public HelicopterAgentTileCodedSARSAGaussian() {
		int numTiles = 10;
		int numFeatures = 12;
		numStateTilings = 16;
		double[] featureMin = {-5, -5, -5, -20, -20, -20, -12.566, -12.566, -12.566, -1, -1, -1};
		double[] featureMax = {5, 5, 5, 20, 20, 20, 12.566, 12.566, 12.566, 1, 1, 1};
		stateTileCoding = new TileCoding(numTiles, numFeatures, numStateTilings, featureMin, featureMax);
		
		int aNumTiles = 5;
		int aNumFeatures = 4;
		numActionTilings = 16;
		double[] aFeatureMin = {-1, -1, -1, -1};
		double[] aFeatureMax = {1, 1, 1, 1};
		actionTileCoding = new TileCoding(aNumTiles, aNumFeatures, numActionTilings, aFeatureMin, aFeatureMax);
		
		qTable = new TileCodeQTable();
	}

	public void agent_cleanup() {
	}

	//Learn from last reward
	public void agent_end(double reward) {
		if(!exploringFrozen){
			// Get all the tiles for the current state
            Tile[] curStates = new Tile[numStateTilings];
            stateTileCoding.getTiles(curStates, new TileCodedHelicopterState(lastState));
    	    
    	    Tile[] actions = new Tile[numActionTilings];
            actionTileCoding.getTiles(actions, new TileCodedHelicopterAction(action));
    	    
    	    for( int i=0; i<numStateTilings; i++ ) {
    	    	for(int j = 0; j < numActionTilings; j++){
    	    		double curQ  = qTable.getQValue(curStates[i], actions[j]);
        	        double val   = curQ + (( alpha * (reward - curQ)) / (double)numStateTilings);
        	        qTable.put(curStates[i], actions[j], val, action);   // commit the update to the Q table
    	    	}
    	        
    	    }
		}
	}

	public void agent_freeze() {

	}

	public void agent_init(String taskSpec) {
		System.out.println(taskSpec);
		TSO = new TaskSpec(taskSpec);
		action = new Action(TSO.getNumDiscreteActionDims(), TSO.getNumContinuousActionDims());
		
		lastState = new Observation(0, TSO.getNumContinuousActionDims());
	}

	public String agent_message(String message) {
		if(message.equals("freeze-learning")){
			exploringFrozen = true;
		}else if(message.equals("unfreeze-learning")){
			exploringFrozen = false;
		}
		return null;
	}

	public Action agent_start(Observation o) {
		lastState = o;
		action = egreedy(o);
		return action;
	}

	public Action agent_step(double reward, Observation o) {
		Action lastAction = action;
		
		action = egreedy(o);
		
		if(!exploringFrozen){
			// Get all the tiles for the current state
            Tile[] curStates = new Tile[numStateTilings];
            stateTileCoding.getTiles(curStates, new TileCodedHelicopterState(lastState));
            
            // get the current Q values
            double newQ[][] = new double[numStateTilings][numActionTilings];
            
    	    // Get all the tiles of the new states
    	    Tile[] newStates = new Tile[numStateTilings];
    	    stateTileCoding.getTiles(newStates, new TileCodedHelicopterState(o));
    	    
    	    // Get all the tiles of the next actions
    	    Tile[] nextActions = new Tile[numActionTilings];
    	    actionTileCoding.getTiles(nextActions, new TileCodedHelicopterAction(action));
    	    
    	    for( int i=0; i<numStateTilings; i++ ) {
                // Get the new states' Q values
    	    	for(int j=0; j<numActionTilings; j++){
    	    		newQ[i][j]    = qTable.getQValue(newStates[i], nextActions[j]);
    	    	}
                         
    	    }
    	    
    	    Tile[] actions = new Tile[numActionTilings];
            actionTileCoding.getTiles(actions, new TileCodedHelicopterAction(lastAction));
    	    
    	    for( int i=0; i<numStateTilings; i++ ) {
    	    	for(int j = 0; j < numActionTilings; j++){
    	    		double curQ  = qTable.getQValue(curStates[i], actions[j]);
    	    		double val   = curQ + (( alpha * (reward + (gamma*newQ[i][j]) - curQ)) / (double)numStateTilings);   	    		
    	    		
        	        qTable.put(curStates[i], actions[j], val, lastAction);   // commit the update to the Q table
        	        
    	    	}
    	        
    	    }
		}
		lastState = o;
		//System.out.println(qTable.size());
		return action;
	}
	
	protected Action randomAction(){
		Action a = new Action(0, 4);
		a.doubleArray[0] = randGenerator.nextGaussian()/3.0;
		a.doubleArray[1] = randGenerator.nextGaussian()/3.0;
		a.doubleArray[2] = randGenerator.nextGaussian()/3.0;
		a.doubleArray[3] = randGenerator.nextGaussian()/3.0;
		
		for(int i = 0; i < 4; i++){
			if(a.doubleArray[i] > 1){
				a.doubleArray[i] = 1;
			}else if(a.doubleArray[i] < -1){
				a.doubleArray[i] = -1;
			}
		}
		return a;
	}
	
	/**
    *
    * Selects a random action with probability 1-sarsa_epsilon,
    * and the action with the highest value otherwise.  This is a
    * quick'n'dirty implementation, it does not do tie-breaking.

    * @param theState
    * @return
    */
   private Action egreedy(Observation theState) {
	   if (!exploringFrozen) {
           if (randGenerator.nextDouble() <= epsilon) {
               return randomAction();
           }
       }
	   
	   Tile[] tiles = new Tile[numStateTilings];
	   stateTileCoding.getTiles(tiles, new TileCodedHelicopterState(theState));
	   ActionValue maxAction = null;
	   for(int i = 0; i < numStateTilings; i++){
		   ActionValue av = qTable.getMaxAction(tiles[i]);
		   if(maxAction == null){
			   maxAction = av;
		   }else if(av.getValue() > maxAction.getValue()){
			   maxAction = av;
		   }
	   }
	   
	   if(maxAction.getAction() == null){
		   return new Action(0,4);
	   }
	   return maxAction.getAction();
   }

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedSARSAGaussian());
		L.run();
	}

}
