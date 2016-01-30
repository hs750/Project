package HSProject.tileCoded.tilings;

import java.util.Map;

import org.rlcommunity.rlglue.codec.types.Action;

import net.openhft.koloboke.collect.map.hash.HashIntObjMaps;

public class TileCodeQTable implements TileCodeQTableInterface {
	protected Map<Integer, Map<Integer, ActionValue>> table;

	private static double DEFAULT_Q_VAL = 0;

	public TileCodeQTable() {
		// TODO Auto-generated constructor stub
		table = HashIntObjMaps.<Map<Integer, ActionValue>> newUpdatableMap();
	}

	public double getQValue(Tile state, Tile action) {
		Map<Integer, ActionValue> actionValues = table.get(state.value_);
		if (actionValues == null) {
			return DEFAULT_Q_VAL;
		}
		ActionValue av = actionValues.get(action.value_);

		if (av != null) {
			return av.getValue();
		}
		return DEFAULT_Q_VAL;

	}

	public double getMaxQValue(Tile state) {
		ActionValue maxAV = getMaxAction(state);
		if (maxAV.getAction() == null) {
			return DEFAULT_Q_VAL;
		}
		return maxAV.getValue();
	}

	public ActionValue getMaxAction(Tile state) {
		Map<Integer, ActionValue> actionValues = table.get(state.value_);
		ActionValue maxAV = new ActionValue(-Double.MAX_VALUE, null);
		if (actionValues == null) {
			return maxAV;
		}

		for (ActionValue av : actionValues.values()) {
			if (av.getValue() > maxAV.getValue()) {
				maxAV = av;
			}
		}
		return maxAV;
	}

	public void put(Tile state, Tile action, double value, Action actualAction) {
		Map<Integer, ActionValue> av = table.get(state.value_);
		if (av == null) {
			av = HashIntObjMaps.<ActionValue> newUpdatableMap();
		}
		if (actualAction == null) {
			ActionValue actionValue = av.get(action.value_);
			if (actionValue != null) {
				actionValue.setValue(value);
			}
		} else {
			av.put(action.value_, new ActionValue(value, actualAction));
			table.put(state.value_, av);
		}
	}

	@Override
	public int getNumStates() {
		int size = table.size();
		return size;
	}

}
