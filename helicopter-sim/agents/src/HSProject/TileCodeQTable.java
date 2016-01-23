package HSProject;

import java.util.HashMap;

import org.rlcommunity.rlglue.codec.types.Action;

public class TileCodeQTable implements TileCodeQTableInterface{
	protected HashMap<Tile, HashMap<Tile, ActionValue>> table = new HashMap<Tile, HashMap<Tile, ActionValue>>();

	private static double DEFAULT_Q_VAL = 0;
	
	public TileCodeQTable() {
		// TODO Auto-generated constructor stub
	}
	
	public double getQValue(Tile state, Tile action){
		HashMap<Tile, ActionValue> actionValues = table.get(state);
		if(actionValues == null){
			actionValues = new HashMap<Tile, ActionValue>();
		}
		ActionValue av = actionValues.get(action);
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
		HashMap<Tile, ActionValue> actionValues = table.get(state);
		if(actionValues == null){
			actionValues = new HashMap<Tile, ActionValue>();
		}
		ActionValue maxAV = new ActionValue(-Double.MAX_VALUE, null);;
		for(ActionValue av : actionValues.values()){
			if(av.getValue() > maxAV.getValue()){
				maxAV = av;
			}
		}
		return maxAV;
	}
	
	
	public void put(Tile state, Tile action, double value, Action actualAction){
		HashMap<Tile, ActionValue> av = table.get(state);
		if(av == null){
			av = new HashMap<Tile, ActionValue>();
		}
		av.put(action, new ActionValue(value, actualAction));
		table.put(state, av);
	}
	
}
