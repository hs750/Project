package helicopterAgentQDiscretised.src;

import java.util.HashMap;
import java.util.HashSet;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

public class QTable {
	private HashMap<HelicopterState, HashSet<HelicopterAction>> actionsForStates;
	private HashMap<QKey, Double> qTable;
	
	private static double DEFAULT_Q_VALUE = 0;
	
	public QTable() {
		actionsForStates = new HashMap<HelicopterState, HashSet<HelicopterAction>>();
		qTable = new HashMap<QKey, Double>();
	}
	
	public double getQValue(Observation o, Action a){
		QKey key = new QKey(o, a);
		Double value = qTable.get(key);
		if(value != null){
			return value;
		}
		return DEFAULT_Q_VALUE;
	}
	
	public void putQValue(Observation o, Action a, double value){
		QKey key = new QKey(o, a);
		qTable.put(key, value);
		
		HelicopterState state = new HelicopterState(o);
		HashSet<HelicopterAction> actions = actionsForStates.get(state);
		if(actions == null){
			actions = new HashSet<HelicopterAction>();
		}
		HelicopterAction action = new HelicopterAction(a);
		actions.add(action);
		actionsForStates.put(state, actions);
	}
	
	public double getMaxQValue(Observation o){
		HelicopterState state = new HelicopterState(o);
		
		HashSet<HelicopterAction> actions = actionsForStates.get(state);
		if(actions == null){
			actions = new HashSet<HelicopterAction>();
		}
		
		double maxValue = Double.MIN_VALUE;
		for(HelicopterAction action : actions){
			QKey key = new QKey(o, action.a);
			Double value = qTable.get(key);
			if(value != null){
				if(value > maxValue){
					maxValue = value;
				}
			}
		}
		
		return maxValue;
	}
	
	public Action getActionForMaxQValue(Observation o){
		HelicopterState state = new HelicopterState(o);
		
		HashSet<HelicopterAction> actions = actionsForStates.get(state);
		if(actions == null){
			actions = new HashSet<HelicopterAction>();
		}
		
		double maxValue = Double.MIN_VALUE;
		Action maxAction = null;
		for(HelicopterAction action : actions){
			QKey key = new QKey(o, action.a);
			Double value = qTable.get(key);
			if(value != null){
				if(value > maxValue){
					maxValue = value;
					maxAction = action.a;
				}
			}
		}
		
		return maxAction;
	}
	
	public int size(){
		return qTable.size();
	}
	
	private class HelicopterState{
		private Observation o;
		public HelicopterState(Observation o){
			this.o = o;
		}
		
		@Override
		public int hashCode() {
			return o.doubleArray.hashCode(); // Helicopter only has observations in the doubleArray
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof HelicopterState){
				HelicopterState hs = (HelicopterState) obj;
				return o.doubleArray.equals(hs.o.doubleArray);
			}else{
				return false;
			}
			
		}
	}
	
	private class HelicopterAction{
		private Action a;
		public HelicopterAction(Action a){
			this.a = a;
		}
		
		@Override
		public int hashCode() {
			return a.doubleArray.hashCode(); // Helicopter only has actions in the doubleArray
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof HelicopterAction){
				HelicopterAction ha = (HelicopterAction) obj;
				return a.doubleArray.equals(ha.a.doubleArray);
			}else{
				return false;
			}
			
		}
	}
	
	private class QKey{
		Observation state;
		Action action;
		
		public QKey(Observation state, Action action){
			this.state = state;
			this.action = action;
		}
		
		@Override
		public int hashCode() {
			return state.doubleArray.hashCode() * action.doubleArray.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof QKey){
				QKey q = (QKey) obj;
				return q.state.doubleArray.equals(state.doubleArray) & q.action.doubleArray.equals(action.doubleArray);
			}else{
				return false;
			}
		}
	}
}
