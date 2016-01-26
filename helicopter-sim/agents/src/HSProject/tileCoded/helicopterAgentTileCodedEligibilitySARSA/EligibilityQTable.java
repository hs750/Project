package HSProject.tileCoded.helicopterAgentTileCodedEligibilitySARSA;

import java.util.Map;
import java.util.Map.Entry;

import org.rlcommunity.rlglue.codec.types.Action;

import HSProject.tileCoded.tilings.Tile;
import HSProject.tileCoded.tilings.TileCodeQTable;
import net.openhft.koloboke.collect.map.hash.HashIntDoubleMaps;
import net.openhft.koloboke.collect.map.hash.HashIntObjMaps;

public class EligibilityQTable extends TileCodeQTable {
	Map<Integer, Map<Integer, Double>> eligibilityTable;
	
	double alpha;
	double gamma;
	double lambda;
	
	public EligibilityQTable(double alpha, double gamma, double lambda) {
		eligibilityTable = HashIntObjMaps.<Map<Integer, Double>>newUpdatableMap();
		
		this.alpha = alpha;
		this.gamma = gamma;
		this.lambda = lambda;
	}
	
	public void eligibilityUpdate(Tile state, Tile action, double delta, Action lastAction){
		double oldElig = getEligibilityValue(state.hashCode(), action.hashCode());
		putEligibilityValue(state.hashCode(), action.hashCode(), oldElig + 1);
		
		double q = getQValue(state, action);
		put(state, action, q + alpha * delta * getEligibilityValue(state.hashCode(), action.hashCode()), lastAction);
		
		for (Entry<Integer, Map<Integer, ActionValue>> states : table.entrySet()){
			for (Entry<Integer, ActionValue> actions : states.getValue().entrySet()){
				double curElig = getEligibilityValue(states.getKey(), actions.getKey());
				
				if(!state.equals(states.getKey()) && !action.equals(actions.getKey())){
					double curQValue = actions.getValue().getValue();
					actions.getValue().setValue(curQValue + alpha * delta * curElig);
				}
				putEligibilityValue(states.getKey(), actions.getKey(), gamma * lambda * curElig);
				
			}
		}
		
		
	}
	
	private double getEligibilityValue(Integer state, Integer action){
		Map<Integer, Double> eligActions = eligibilityTable.get(state);
		if(eligActions == null){
			return 0;
		}
		Double oldElig = eligActions.get(action);
		if(oldElig == null){
			oldElig = new Double(0);
		}
		return oldElig;
	}
	
	private void putEligibilityValue(Integer state, Integer action, double elig){
		Map<Integer, Double> eligActions = eligibilityTable.get(state);
		if(eligActions == null){
			eligActions = HashIntDoubleMaps.newUpdatableMap();
		}
		eligActions.put(action, elig);
		eligibilityTable.put(state, eligActions);
	}
	
	
}
