package HSProject;

import java.util.HashMap;
import java.util.HashSet;

import org.rlcommunity.rlglue.codec.types.Action;

import HSProject.Tile;

public class TileCodeQTableFlat implements TileCodeQTableInterface{
	private HashMap<Tile, HashSet<Tile>> actionsForStates;
	private HashMap<QKey, ActionValue> qTable;

	private static double DEFAULT_Q_VAL = 0;
	
	public TileCodeQTableFlat() {
		actionsForStates = new HashMap<Tile, HashSet<Tile>>(1000000);
		qTable = new HashMap<QKey, ActionValue>(10000000);
	}
	
	public double getQValue(Tile state, Tile action){
		QKey qk = new QKey(state, action);
		ActionValue av = qTable.get(qk);
		if(av != null){
			return av.getValue();
		}
		return DEFAULT_Q_VAL;
	}
	
	public double getMaxQValue(Tile state){
		ActionValue maxAV = getMaxAction(state);
		
		if(maxAV.getAction() == null){
			return DEFAULT_Q_VAL;
		}
		return maxAV.getValue();
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
				if(av.getValue() > maxAV.getValue()){
					maxAV = av;
				}
			}
		}
		
		return maxAV;
	}
	
	
	public void put(Tile state, Tile action, double value, Action actualAction){
		QKey qk = new QKey(state, action);
		ActionValue av = qTable.get(qk);
		if(av != null){
			av.update(value, actualAction);
		}else{
			av = new ActionValue(value, actualAction);
			qTable.put(qk, av);
		}
		
		HashSet<Tile> actions = actionsForStates.get(state);
		
		boolean newAction = false;
		if(actions == null){
			actions = new HashSet<Tile>();
			newAction = true;
		}
		actions.add(action);
		
		if(newAction){
			actionsForStates.put(state, actions);//only need to put the new actions if it is new, otherwise the byref nature means it is already in.
		}
		//System.out.println(actionsForStates.size() + " " + qTable.size() + " " + actions.size());
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
