package HSProject.tileCoded.helicopterAgentTileCodedEligibilitySARSA;

import java.util.HashMap;
import java.util.Map.Entry;

import org.rlcommunity.rlglue.codec.types.Action;

import com.sun.xml.internal.ws.assembler.dev.ServerTubelineAssemblyContext;

import HSProject.tileCoded.tilings.Tile;
import HSProject.tileCoded.tilings.TileCodeQTable;

public class EligibilityQTable extends TileCodeQTable {
	HashMap<Tile, HashMap<Tile, Double>> eligibilityTable;
	
	double alpha;
	double gamma;
	double lambda;
	
	public EligibilityQTable(double alpha, double gamma, double lambda) {
		eligibilityTable = new HashMap<Tile, HashMap<Tile,Double>>();
		
		this.alpha = alpha;
		this.gamma = gamma;
		this.lambda = lambda;
	}
	
	public void eligibilityUpdate(Tile state, Tile action, double delta, Action lastAction){
		double oldElig = getEligibilityValue(state, action);
		putEligibilityValue(state, action, oldElig + 1);
		
		double q = getQValue(state, action);
		put(state, action, q + alpha * delta * getEligibilityValue(state, action), lastAction);
		
		for (Entry<Tile, HashMap<Tile, ActionValue>> states : table.entrySet()){
			for (Entry<Tile, ActionValue> actions : states.getValue().entrySet()){
				double curElig = getEligibilityValue(states.getKey(), actions.getKey());
				
				if(!state.equals(states.getKey()) && !action.equals(actions.getKey())){
					double curQValue = actions.getValue().getValue();
					actions.getValue().setValue(curQValue + alpha * delta * curElig);
				}
				putEligibilityValue(states.getKey(), actions.getKey(), gamma * lambda * curElig);
				
			}
		}
		
		
	}
	
	private double getEligibilityValue(Tile state, Tile action){
		HashMap<Tile, Double> eligActions = eligibilityTable.get(state);
		if(eligActions == null){
			eligActions = new HashMap<Tile, Double>();
		}
		Double oldElig = eligActions.get(action);
		if(oldElig == null){
			oldElig = new Double(0);
		}
		return oldElig;
	}
	
	private void putEligibilityValue(Tile state, Tile action, double elig){
		HashMap<Tile, Double> eligActions = eligibilityTable.get(state);
		if(eligActions == null){
			eligActions = new HashMap<Tile, Double>();
		}
		eligActions.put(action, elig);
		eligibilityTable.put(state, eligActions);
	}
	
	
}
