package HSProject;

import java.util.HashMap;
import java.util.HashSet;

import org.rlcommunity.rlglue.codec.types.Action;

import HSProject.Tile;

public class TileCodeQTableFlat {
	private HashMap<Tile, HashSet<Tile>> actionsForStates;
	private HashMap<QKey, ActionValue> qTable;

	private static double DEFAULT_Q_VAL = 0;
	
	public TileCodeQTableFlat() {
		actionsForStates = new HashMap<Tile, HashSet<Tile>>();
		qTable = new HashMap<QKey, ActionValue>();
	}
	
	public double getQValue(Tile state, Tile action){
		QKey qk = new QKey(state, action);
		ActionValue av = qTable.get(qk);
		if(av != null){
			return av.value;
		}
		return DEFAULT_Q_VAL;
	}
	
	public double getMaxQValue(Tile state){
		ActionValue maxAV = getMaxAction(state);
		
		if(maxAV.actualAction == null){
			return DEFAULT_Q_VAL;
		}
		return maxAV.value;
	}
	
	public ActionValue getMaxAction(Tile state){
		ActionValue maxAV = new ActionValue(-Double.MAX_VALUE, null);
		HashSet<Tile> actions = actionsForStates.get(state);
		if(actions == null){
			actions = new HashSet<Tile>();
		}
		for(Tile action : actions){
			QKey qk = new QKey(state, action);
			ActionValue av = qTable.get(qk);
			if(av != null){
				if(av.value > maxAV.value){
					maxAV = av;
				}
			}
		}
		
		return maxAV;
	}
	
	
	public void put(Tile state, Tile action, double value, Action actualAction){
		QKey qk = new QKey(state, action);
		ActionValue av = new ActionValue(value, actualAction);
		qTable.put(qk, av);
		HashSet<Tile> actions = actionsForStates.get(state);
		if(actions == null){
			actions = new HashSet<Tile>();
		}
		actions.add(action);
		actionsForStates.put(state, actions);
	}
	
	public class ActionValue{
		private double value;
		private Action actualAction;
		public ActionValue(double value, Action actualAction) {
			this.value = value;
			if(actualAction != null){
				this.actualAction = new Action(actualAction);
			}else{
				this.actualAction = null;
			}
		}
		
		public double getValue() {
			return value;
		}
		
		public Action getAction() {
			return actualAction;
		}
		
	}
	
	private class QKey {
		Tile observation;
		Tile action;
		
		public QKey(Tile o, Tile a) {
			observation = o;
			action = a;
		}

		@Override
		public int hashCode() {
			return observation.hashCode() * action.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof QKey) {
				QKey qk = (QKey) obj;
				return this.observation.equals(qk.observation) && this.action.equals(qk.action);
			} else {
				return false;
			}
		}
	}
	
}
