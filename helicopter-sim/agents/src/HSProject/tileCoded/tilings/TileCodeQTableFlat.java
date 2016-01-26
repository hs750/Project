package HSProject.tileCoded.tilings;

import java.util.Map;
import java.util.Set;

import org.rlcommunity.rlglue.codec.types.Action;

import net.openhft.koloboke.collect.map.hash.HashIntObjMaps;
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import net.openhft.koloboke.collect.set.hash.HashIntSets;

public class TileCodeQTableFlat implements TileCodeQTableInterface{
	private Map<Integer, Set<Integer>> actionsForStates;
	private Map<QKey, ActionValue> qTable;

	private static double DEFAULT_Q_VAL = 0;
	
	public TileCodeQTableFlat() {
		actionsForStates = HashIntObjMaps.<Set<Integer>>newUpdatableMap();
		qTable = HashObjObjMaps.<QKey, ActionValue>newUpdatableMap();
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
		Set<Integer> actions = actionsForStates.get(state.value_);
		if(actions == null){
			actions = HashIntSets.newUpdatableSet();
		}
		for(Integer action : actions){
			QKey qk = new QKey(state, new Tile(action));
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
		
		Set<Integer> actions = actionsForStates.get(state.value_);
		
		boolean newAction = false;
		if(actions == null){
			actions = HashIntSets.newUpdatableSet();
			newAction = true;
		}
		actions.add(action.value_);
		
		if(newAction){
			actionsForStates.put(state.value_, actions);//only need to put the new actions if it is new, otherwise the byref nature means it is already in.
		}
		//System.out.println(actionsForStates.size() + " " + qTable.size() + " " + actions.size());
	}
	
	private class QKey {
		Tile observation;
		Tile action;
		int hash;
		public QKey(Tile o, Tile a) {
			observation = o;
			action = a;
			hash = observation.hashCode() * action.hashCode();
		}

		@Override
		public int hashCode() {
			return hash;
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
