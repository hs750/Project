package HSProject.helicopterAgentTileCodedQ;



import java.util.HashMap;
import java.util.Random;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.TileCodeQTable;
import HSProject.TileCodeQTable.ActionValue;
import HSProject.TileCodedHelicopterAction;
import HSProject.TileCodedHelicopterState;
import HSProject.TileCoding;
import HSProject.Tile;

public class HelicopterAgentTileCodedQ implements AgentInterface {
	private TileCodeQTable qTable;
	private TileCoding stateTileCoding;
	private TileCoding actionTileCoding;
	
	int numStateTilings = 32;
	int numActionTilings = 4;
	int numActions;
	private HashMap<Integer, Action> savedActions;
	
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

	public HelicopterAgentTileCodedQ() {
		int numTiles = 8;
		int numFeatures = 12;
		numStateTilings = 8;
		double[] featureMin = {-5, -5, -5, -20, -20, -20, -12.566, -12.566, -12.566, -1, -1, -1};
		double[] featureMax = {5, 5, 5, 20, 20, 20, 12.566, 12.566, 12.566, 1, 1, 1};
		stateTileCoding = new TileCoding(numTiles, numFeatures, numStateTilings, featureMin, featureMax);
		
		int aNumTiles = 5;
		int aNumFeatures = 4;
		numActionTilings = 4;
		double[] aFeatureMin = {-1, -1, -1, -1};
		double[] aFeatureMax = {1, 1, 1, 1};
		actionTileCoding = new TileCoding(aNumTiles, aNumFeatures, numActionTilings, aFeatureMin, aFeatureMax);
		
		double numStates = Math.pow(numTiles, numFeatures) * numStateTilings;
		numActions = (int) Math.pow(aNumTiles, aNumFeatures) * numActionTilings;
		qTable = new TileCodeQTable();
		
		savedActions = new HashMap<Integer, Action>();
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
            double newQ[] = new double[numStateTilings];
            
    	    // Get all the tiles of the new states
    	    Tile[] newStates = new Tile[numStateTilings];
    	    stateTileCoding.getTiles(newStates, new TileCodedHelicopterState(o));
    	    
    	    for( int i=0; i<numStateTilings; i++ ) {
                // Get the new states' Q values
                         newQ[i]    = qTable.getMaxQValue(newStates[i]);
    	    }
    	    
    	    Tile[] actions = new Tile[numActionTilings];
            actionTileCoding.getTiles(actions, new TileCodedHelicopterAction(lastAction));
    	    
    	    for( int i=0; i<numStateTilings; i++ ) {
    	    	for(int j = 0; j < numActionTilings; j++){
    	    		double curQ  = qTable.getQValue(curStates[i], actions[j]);
    	    		double val   = curQ + (( alpha * (reward + (gamma*newQ[i]) - curQ)) / (double)numStateTilings);
    	    		
        	        qTable.put(curStates[i], actions[j], val, lastAction);   // commit the update to the Q table
    	    	}
    	        
    	    }
    	    
    	    
		}
		lastState = o;
		//System.out.println(qTable.size());
		return action;
	}

	private Action fixed_policy(Observation o) {
		double weights[] = { 0.0196, 0.7475, 0.0367, 0.0185, 0.7904, 0.0322, 0.1969, 0.0513, 0.1348, 0.02, 0, 0.23 };

		int y_w = 0;
		int roll_w = 1;
		int v_w = 2;
		int x_w = 3;
		int pitch_w = 4;
		int u_w = 5;
		int yaw_w = 6;
		int z_w = 7;
		int w_w = 8;
		int ail_trim = 9;
		int el_trim = 10;
		int coll_trim = 11;

		// x/y/z_error = body(x - x_target)
		// q_error = inverse(Q_target) * Q, where Q is the orientation of the
		// helicopter
		// roll/pitch/yaw_error = scaled_axis(q_error)

		// collective control
		double coll = weights[z_w] * o.doubleArray[z_err] + weights[w_w] * o.doubleArray[w_err] + weights[coll_trim];

		// forward-backward control
		double elevator = -weights[x_w] * o.doubleArray[x_err] + -weights[u_w] * o.doubleArray[u_err]
				+ weights[pitch_w] * o.doubleArray[qy_err] + weights[el_trim];

		// left-right control
		double aileron = -weights[y_w] * o.doubleArray[y_err] + -weights[v_w] * o.doubleArray[v_err]
				+ -weights[roll_w] * o.doubleArray[qx_err] + weights[ail_trim];

		double rudder = -weights[yaw_w] * o.doubleArray[qz_err];

		Action a  =new Action(0,  4);
		a.doubleArray[0] = aileron;
		a.doubleArray[1] = elevator;
		a.doubleArray[2] = rudder;
		a.doubleArray[3] = coll;
		
		return a;
	}
	
	protected Action randomAction(Observation o){
		Action a = fixed_policy(o);
		a = new Action(0, 4);
		a.doubleArray[0] = (randGenerator.nextDouble() * 2) - 1;
		a.doubleArray[1] = (randGenerator.nextDouble() * 2) - 1;
		a.doubleArray[2] = (randGenerator.nextDouble() * 2) - 1;
		a.doubleArray[3] = (randGenerator.nextDouble() * 2) - 1;
//		a.doubleArray[0] = a.doubleArray[0] * randGenerator.nextDouble();
//		a.doubleArray[1] = a.doubleArray[1] * randGenerator.nextDouble();
//		a.doubleArray[2] = a.doubleArray[2] * randGenerator.nextDouble();
//		a.doubleArray[3] = a.doubleArray[3] * randGenerator.nextDouble();
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
               return randomAction(theState);
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
		   return randomAction(theState);
	   }
	   return maxAction.getAction();
	   
//	// Get all the tiles of this state representation
//	    Tile[] tiles = new Tile[numStateTilings];
//	    stateTileCoding.getTiles(tiles, new TileCodedHelicopterState(theState));
//	    
//	    // initialise a container for the sum of the q values
//	    // and sum them
//	    double[] qValues     = new double[numActions],
//	    	     tileQValues;
//	    for( int k=0; k<numActions; k++ )
//	        qValues[k] = 0;
//
//	    for( int j=0; j<numStateTilings; j++ ) {
//	        tileQValues = qTable.get(tiles[j]);
//	        for( int k=0; k<numActions; k++ )
//	            qValues[k] += tileQValues[k];
//	    }
//		
//	    int maxQIndex = 0;
//	    double maxQ = -Double.MAX_VALUE;
//	    for(int i = 0; i<numActions; i++){
//	    	if(qValues[i] > maxQ){
//	    		maxQ = qValues[i];
//	    		maxQIndex = i;
//	    	}
//	    }
//	    
//	    Action maxAction = savedActions.get(maxQIndex);
//	    if(maxAction == null){
//	    	return randomAction(theState);
//	    }
//	    return maxAction;
   }

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedQ());
		L.run();
	}

}
