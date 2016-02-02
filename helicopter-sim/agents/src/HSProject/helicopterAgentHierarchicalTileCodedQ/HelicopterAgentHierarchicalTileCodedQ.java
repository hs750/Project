package HSProject.helicopterAgentHierarchicalTileCodedQ;



import java.util.ArrayList;
import java.util.Random;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.tilings.Tile;
import HSProject.tileCoded.tilings.TileCodeQTable;
import HSProject.tileCoded.tilings.TileCodeQTableInterface.ActionValue;
import HSProject.tileCoded.tilings.TileCoding;

public class HelicopterAgentHierarchicalTileCodedQ implements AgentInterface {
	private TileCodeQTable u_qTable;
	private TileCodeQTable v_qTable;
	private TileCodeQTable w_qTable;
	private TileCodeQTable x_qTable;
	private TileCodeQTable y_qTable;
	private TileCodeQTable z_qTable;
	private TileCodeQTable p_qTable;
	private TileCodeQTable q_qTable;
	private TileCodeQTable r_qTable;
	private TileCodeQTable qx_qTable;
	private TileCodeQTable qy_qTable;
	private TileCodeQTable qz_qTable;
	
	private TileCoding stateTileCoding_vel;
	private TileCoding stateTileCoding_pos;
	private TileCoding stateTileCoding_rate;
	private TileCoding stateTileCoding_quat;
	
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
	@SuppressWarnings("unused")
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

	public HelicopterAgentHierarchicalTileCodedQ() {
		int numTiles = 10;
		int numFeatures = 1;
		numStateTilings = 16;
		double[] featureMin_vel = {-5};
		double[] featureMax_vel = {5};
		
		double[] featureMin_pos = {-20};
		double[] featureMax_pos = {20};
		
		double[] featureMin_rate = {-12.566};
		double[] featureMax_rate = {12.566};
		
		double[] featureMin_quat = {-1};
		double[] featureMax_quat = {1};
		
		
		stateTileCoding_vel = new TileCoding(numTiles, numFeatures, numStateTilings, featureMin_vel, featureMax_vel);
		stateTileCoding_pos = new TileCoding(numTiles, numFeatures, numStateTilings, featureMin_pos, featureMax_pos);
		stateTileCoding_rate = new TileCoding(numTiles, numFeatures, numStateTilings, featureMin_rate, featureMax_rate);
		stateTileCoding_quat = new TileCoding(numTiles, numFeatures, numStateTilings, featureMin_quat, featureMax_quat  );
		
		int aNumTiles = 5;
		int aNumFeatures = 4;
		numActionTilings = 16;
		double[] aFeatureMin = {-1, -1, -1, -1};
		double[] aFeatureMax = {1, 1, 1, 1};
		actionTileCoding = new TileCoding(aNumTiles, aNumFeatures, numActionTilings, aFeatureMin, aFeatureMax);
		
		u_qTable = new TileCodeQTable();
		v_qTable = new TileCodeQTable();
		w_qTable = new TileCodeQTable();
		x_qTable = new TileCodeQTable();
		y_qTable = new TileCodeQTable();
		z_qTable = new TileCodeQTable();
		p_qTable = new TileCodeQTable();
		q_qTable = new TileCodeQTable();
		r_qTable = new TileCodeQTable();
		qx_qTable = new TileCodeQTable();
		qy_qTable = new TileCodeQTable();
		qz_qTable = new TileCodeQTable();
	}

	public void agent_cleanup() {
	}

	//Learn from last reward
	public void agent_end(double reward) {
		if(!exploringFrozen){
			// Get all the tiles for the current state
			ArrayList<Tile[]> currentStates = new ArrayList<Tile[]>(12);
			
			for(int s = 0; s < 12; s++){
				Tile[] curStates = new Tile[numStateTilings];
				Observation ls = new Observation(0, 1);
				ls.doubleArray[0] = lastState.doubleArray[s];
				if(s < 3){
					stateTileCoding_vel.getTiles(curStates, ls.doubleArray);
				}else if(s<6){
					stateTileCoding_pos.getTiles(curStates, ls.doubleArray);
				}else if(s<9){
					stateTileCoding_rate.getTiles(curStates, ls.doubleArray);
				}else{
					stateTileCoding_quat.getTiles(curStates, ls.doubleArray);
				}
				currentStates.add(curStates);
				
			}
    	    
    	    Tile[] actions = new Tile[numActionTilings];
            actionTileCoding.getTiles(actions, action.doubleArray);
    	    
            for(int s = 0; s < 12; s++){
	    	    for( int i=0; i<numStateTilings; i++ ) {
	    	    	for(int j = 0; j < numActionTilings; j++){
	    	    		TileCodeQTable qTable = null;
	    	    		switch(s){
	    	    		case 0:
	    	    			qTable = u_qTable;
	    	    			break;
	    	    		case 1:
	    	    			qTable = v_qTable;
	    	    			break;
	    	    		case 2:
	    	    			qTable = w_qTable;
	    	    			break;
	    	    		case 3:
	    	    			qTable = x_qTable;
	    	    			break;
	    	    		case 4:
	    	    			qTable = y_qTable;
	    	    			break;
	    	    		case 5:
	    	    			qTable = z_qTable;
	    	    			break;
	    	    		case 6:
	    	    			qTable = p_qTable;
	    	    			break;
	    	    		case 7:
	    	    			qTable = q_qTable;
	    	    			break;
	    	    		case 8:
	    	    			qTable = r_qTable;
	    	    			break;
	    	    		case 9:
	    	    			qTable = qx_qTable;
	    	    			break;
	    	    		case 10:
	    	    			qTable = qy_qTable;
	    	    			break;
	    	    		case 11:
	    	    			qTable = qz_qTable;
	    	    			break;
	    	    		}
	    	    		Tile[] curStates = currentStates.get(s);
	    	    		double curQ  = qTable.getQValue(curStates[i], actions[j]);
	        	        double val   = curQ + (( alpha * (reward - curQ)) / (double)numStateTilings);
	        	        qTable.put(curStates[i], actions[j], val, action);   // commit the update to the Q table
	    	    	}
	    	        
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
			ArrayList<Tile[]> currentStates = new ArrayList<Tile[]>(12);
			
			for(int s = 0; s < 12; s++){
				Tile[] curStates = new Tile[numStateTilings];
				Observation ls = new Observation(0, 1);
				ls.doubleArray[0] = lastState.doubleArray[s];
				if(s < 3){
					stateTileCoding_vel.getTiles(curStates, ls.doubleArray);
				}else if(s<6){
					stateTileCoding_pos.getTiles(curStates, ls.doubleArray);
				}else if(s<9){
					stateTileCoding_rate.getTiles(curStates, ls.doubleArray);
				}else{
					stateTileCoding_quat.getTiles(curStates, ls.doubleArray);
				}
				currentStates.add(curStates);
				
			}
            
            // get the current Q values
            double newQ[] = new double[numStateTilings];
            
    	    // Get all the tiles of the new states
            ArrayList<Tile[]> newStates = new ArrayList<Tile[]>(12);
			
			for(int s = 0; s < 12; s++){
				Tile[] newStates_ = new Tile[numStateTilings];
				Observation ls = new Observation(0, 1);
				ls.doubleArray[0] = o.doubleArray[s];
				if(s < 3){
					stateTileCoding_vel.getTiles(newStates_, ls.doubleArray);
				}else if(s<6){
					stateTileCoding_pos.getTiles(newStates_, ls.doubleArray);
				}else if(s<9){
					stateTileCoding_rate.getTiles(newStates_, ls.doubleArray);
				}else{
					stateTileCoding_quat.getTiles(newStates_, ls.doubleArray);
				}
				newStates.add(newStates_);
				
			}

    	    
    	    Tile[] actions = new Tile[numActionTilings];
            actionTileCoding.getTiles(actions, lastAction.doubleArray);
    	    
            for(int s = 0; s < 12; s++){
	    	    for( int i=0; i<numStateTilings; i++ ) {
	    	    	for(int j = 0; j < numActionTilings; j++){
	    	    		TileCodeQTable qTable = null;
	    	    		switch(s){
	    	    		case 0:
	    	    			qTable = u_qTable;
	    	    			break;
	    	    		case 1:
	    	    			qTable = v_qTable;
	    	    			break;
	    	    		case 2:
	    	    			qTable = w_qTable;
	    	    			break;
	    	    		case 3:
	    	    			qTable = x_qTable;
	    	    			break;
	    	    		case 4:
	    	    			qTable = y_qTable;
	    	    			break;
	    	    		case 5:
	    	    			qTable = z_qTable;
	    	    			break;
	    	    		case 6:
	    	    			qTable = p_qTable;
	    	    			break;
	    	    		case 7:
	    	    			qTable = q_qTable;
	    	    			break;
	    	    		case 8:
	    	    			qTable = r_qTable;
	    	    			break;
	    	    		case 9:
	    	    			qTable = qx_qTable;
	    	    			break;
	    	    		case 10:
	    	    			qTable = qy_qTable;
	    	    			break;
	    	    		case 11:
	    	    			qTable = qz_qTable;
	    	    			break;
	    	    		}
	    	    		Tile[] curStates = currentStates.get(s);
	    	    		double curQ  = qTable.getQValue(curStates[i], actions[j]);
	    	    		double val   = curQ + (( alpha * (reward + (gamma*qTable.getMaxQValue(newStates.get(s)[i])) - curQ)) / (double)numStateTilings);
	    	    		
	        	        qTable.put(curStates[i], actions[j], val, lastAction);   // commit the update to the Q table
	    	    	}
	    	        
	    	    }
            }
    	    
		}
		lastState = o;
		//System.out.println(qTable.size());
		return action;
	}
	
	protected Action randomAction(){
		Action a = new Action(0, 4);
		a.doubleArray[0] = (randGenerator.nextDouble() * 2) - 1;
		a.doubleArray[1] = (randGenerator.nextDouble() * 2) - 1;
		a.doubleArray[2] = (randGenerator.nextDouble() * 2) - 1;
		a.doubleArray[3] = (randGenerator.nextDouble() * 2) - 1;
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
	   ArrayList<Tile[]> states = new ArrayList<Tile[]>(12);
		
		for(int s = 0; s < 12; s++){
			Tile[] tiles = new Tile[numStateTilings];
			Observation ls = new Observation(0, 1);
			ls.doubleArray[0] = theState.doubleArray[s];
			if(s < 3){
				stateTileCoding_vel.getTiles(tiles, ls.doubleArray);
			}else if(s<6){
				stateTileCoding_pos.getTiles(tiles, ls.doubleArray);
			}else if(s<9){
				stateTileCoding_rate.getTiles(tiles, ls.doubleArray);
			}else{
				stateTileCoding_quat.getTiles(tiles, ls.doubleArray);
			}
			states.add(tiles);
			
		}
		
		ArrayList<ActionValue> maxActions = new ArrayList<ActionValue>(12);
		for(int s = 0; s < 12; s++){
		   ActionValue maxAction = null;
		   for(int i = 0; i < numStateTilings; i++){
			   TileCodeQTable qTable = null;
	    		switch(s){
	    		case 0:
	    			qTable = u_qTable;
	    			break;
	    		case 1:
	    			qTable = v_qTable;
	    			break;
	    		case 2:
	    			qTable = w_qTable;
	    			break;
	    		case 3:
	    			qTable = x_qTable;
	    			break;
	    		case 4:
	    			qTable = y_qTable;
	    			break;
	    		case 5:
	    			qTable = z_qTable;
	    			break;
	    		case 6:
	    			qTable = p_qTable;
	    			break;
	    		case 7:
	    			qTable = q_qTable;
	    			break;
	    		case 8:
	    			qTable = r_qTable;
	    			break;
	    		case 9:
	    			qTable = qx_qTable;
	    			break;
	    		case 10:
	    			qTable = qy_qTable;
	    			break;
	    		case 11:
	    			qTable = qz_qTable;
	    			break;
	    		}
			   ActionValue av = qTable.getMaxAction(states.get(s)[i]);
			   if(maxAction == null){
				   maxAction = av;
			   }else if(av.getValue() > maxAction.getValue()){
				   maxAction = av;
			   }
		   }
		   maxActions.add(maxAction);
		}
		
		Action theAction = new Action(0, 4);
		int numActionsUsed = 0;
		for(int s = 0; s < 12; s++){
			ActionValue a = maxActions.get(s);
			if(a.getAction() != null){
				theAction.doubleArray[0] += a.getAction().doubleArray[0];
				theAction.doubleArray[1] += a.getAction().doubleArray[1];
				theAction.doubleArray[2] += a.getAction().doubleArray[2];
				theAction.doubleArray[3] += a.getAction().doubleArray[3];
				numActionsUsed++;
				
			}
		}
		if(numActionsUsed > 0){
			theAction.doubleArray[0] /= numActionsUsed;
			theAction.doubleArray[1] /= numActionsUsed;
			theAction.doubleArray[2] /= numActionsUsed;
			theAction.doubleArray[3] /= numActionsUsed;
			
			return theAction;
		}else{
			return randomAction();
		}
   }

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentHierarchicalTileCodedQ());
		L.run();
	}

}
