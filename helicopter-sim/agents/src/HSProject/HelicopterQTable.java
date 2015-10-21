package HSProject;

import java.util.HashMap;
import java.util.Map.Entry;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

public class HelicopterQTable {
	
	private HashMap<HelicopterState, HashMap<HelicopterAction, Double>> table;

	private static double DEFAULT_Q_VALUE = 0;

	public HelicopterQTable() {
		table = new HashMap<HelicopterState, HashMap<HelicopterAction, Double>>();
	}

	public double getQValue(Observation o, Action a) {
		HelicopterState state = new HelicopterState(o);
		HashMap<HelicopterAction, Double> actions = table.get(state);
		if(actions == null){
			actions = new HashMap<HelicopterAction, Double>();
		}
		
		HelicopterAction action = new HelicopterAction(a);
		Double value = actions.get(action);
		
		if (value != null) {
			return value;
		}
		return DEFAULT_Q_VALUE;
	}

	public void putQValue(Observation o, Action a, double value) {
		HelicopterState state = new HelicopterState(o);
		HashMap<HelicopterAction, Double> actions = table.get(state);
		if(actions == null){
			actions = new HashMap<HelicopterAction, Double>();
		}
		HelicopterAction action = new HelicopterAction(a);
		actions.put(action, value);
		table.put(state, actions);
	}

	public double getMaxQValue(Observation o) {
		HelicopterState state = new HelicopterState(o);
		HashMap<HelicopterAction, Double> actions = table.get(state);
		if(actions == null){
			actions = new HashMap<HelicopterAction, Double>();
		}
		Double maxVal = -Double.MAX_VALUE;
		for(Double val : actions.values()){
			if(val > maxVal){
				maxVal = val;
			}
		}
		return maxVal == -Double.MAX_VALUE ? DEFAULT_Q_VALUE : maxVal;
	}

	public Action getActionForMaxQValue(Observation o) {
		HelicopterState state = new HelicopterState(o);
		HashMap<HelicopterAction, Double> actions = table.get(state);
		if(actions == null){
			actions = new HashMap<HelicopterAction, Double>();
		}
		Double maxVal = -Double.MAX_VALUE;
		Action maxAction = null;
		for(Entry<HelicopterAction, Double> action : actions.entrySet()){
			if(action.getValue() > maxVal){
				maxVal = action.getValue();
				maxAction = action.getKey().action;
			}
		}
		return maxAction;
	}

	public int size() {
		return table.size();
	}

	private class HelicopterState {
		Observation observation;

		public HelicopterState(Observation o) {
			observation = new Observation(o);
		}

		@Override
		public int hashCode() {
			double hash = 1;
			for(int i = 0; i < observation.doubleArray.length; i++){
				hash *= observation.doubleArray[i];
			}
			return (int) hash; // Helicopter only has observations in the doubleArray
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof HelicopterState) {
				HelicopterState hs = (HelicopterState) obj;
				boolean equal = true;
				for(int i = 0; i < observation.doubleArray.length; i++){
					equal = equal && (observation.doubleArray[i] == hs.observation.doubleArray[i]);
				}
				return equal;
			} else {
				return false;
			}

		}
	}

	private class HelicopterAction {
		Action action;

		public HelicopterAction(Action a) {
			action = new Action(a);
		}

		@Override
		public int hashCode() {
			double hash = 1;
			for(int i = 0; i < action.doubleArray.length; i++){
				hash *= action.doubleArray[i];
			}
			return (int) hash;
			 // Helicopter only has actions in the doubleArray
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof HelicopterAction) {
				HelicopterAction ha = (HelicopterAction) obj;
				boolean equal = true;
				for(int i = 0; i < action.doubleArray.length; i++){
					equal = equal && (action.doubleArray[i] == ha.action.doubleArray[i]);
				}
				return equal;
			} else {
				return false;
			}

		}
	}
}
