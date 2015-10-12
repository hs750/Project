package helicopterAgentQ.src;

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

	public double getQValue(Observation o, Action a) {
		QKey key = new QKey(o, a);
		Double value = qTable.get(key);
		if (value != null) {
			return value;
		}
		return DEFAULT_Q_VALUE;
	}

	public void putQValue(Observation o, Action a, double value) {
		QKey key = new QKey(o, a);
		qTable.put(key, value);
		System.out.println(value);
		HelicopterState state = new HelicopterState(o);
		HashSet<HelicopterAction> actions = actionsForStates.get(state);
		if (actions == null) {
			actions = new HashSet<HelicopterAction>();
		}
		HelicopterAction action = new HelicopterAction(a);
		actions.add(action);
		actionsForStates.put(state, actions);
		
	}

	public double getMaxQValue(Observation o) {
		HelicopterState state = new HelicopterState(o);

		HashSet<HelicopterAction> actions = actionsForStates.get(state);
		if (actions == null) {
			actions = new HashSet<HelicopterAction>();
		}

		double maxValue = -Double.MAX_VALUE;
		for (HelicopterAction action : actions) {
			QKey key = new QKey(o, action.action);
			Double value = qTable.get(key);
			if (value != null) {
				if (value > maxValue) {
					maxValue = value;
				}
			}
		}
		return maxValue == -Double.MAX_VALUE ? DEFAULT_Q_VALUE : maxValue;
	}

	public Action getActionForMaxQValue(Observation o) {
		HelicopterState state = new HelicopterState(o);

		HashSet<HelicopterAction> actions = actionsForStates.get(state);
		if (actions == null) {
			actions = new HashSet<HelicopterAction>();
		}

		double maxValue = -Double.MAX_VALUE;
		Action maxAction = null;
		for (HelicopterAction action : actions) {
			QKey key = new QKey(o, action.action);
			Double value = qTable.get(key);
			if (value != null) {
				if (value > maxValue) {
					maxValue = value;
					maxAction = action.action;
				}
			}
		}
		return maxAction;
	}

	public int size() {
		return qTable.size();
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

	private class QKey {
		Observation observation;
		Action action;
		
		public QKey(Observation o, Action a) {
			observation = new Observation(o);
			action = new Action(a);
		}

		@Override
		public int hashCode() {
			double hash = 1;
			for(int i = 0; i < observation.doubleArray.length; i++){
				hash *= observation.doubleArray[i];
			}
			for(int i = 0; i < action.doubleArray.length; i++){
				hash *= action.doubleArray[i];
			}
			
			return (int) hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof QKey) {
				QKey q = (QKey) obj;
				boolean equal = true;
				for(int i = 0; i < observation.doubleArray.length; i++){
					equal = equal && (observation.doubleArray[i] == q.observation.doubleArray[i]);
				}
				for(int i = 0; i < action.doubleArray.length; i++){
					equal = equal && (action.doubleArray[i] == q.action.doubleArray[i]);
				}
				return equal;
			} else {
				return false;
			}
		}
	}
}
