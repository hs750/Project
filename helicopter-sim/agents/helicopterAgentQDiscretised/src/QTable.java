package helicopterAgentQDiscretised.src;

import java.util.ArrayList;
import java.util.Arrays;
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

		HelicopterState state = new HelicopterState(o);
		HashSet<HelicopterAction> actions = actionsForStates.get(state);
		if (actions == null) {
			System.out.println("make new");
			actions = new HashSet<HelicopterAction>();
		}
		HelicopterAction action = new HelicopterAction(a);
		actions.add(action);
		HashSet<HelicopterAction> test = actionsForStates.put(state, actions);
		if(test != null){
			System.out.println("overriten " + actions.size() + " " + actionsForStates.get(state).size() + " " + (actions == actionsForStates.get(state)));
		}
	}

	public double getMaxQValue(Observation o) {
		HelicopterState state = new HelicopterState(o);

		HashSet<HelicopterAction> actions = actionsForStates.get(state);
		if (actions == null) {
			System.out.println("null actions");
			actions = new HashSet<HelicopterAction>();
		}

		double maxValue = Double.NEGATIVE_INFINITY;
		for (HelicopterAction action : actions) {
			QKey key = new QKey(o, action.actionArray);
			Double value = qTable.get(key);
			if (value != null) {
				if (value > maxValue) {
					System.out.println("foudn");
					maxValue = value;
				}
			}
		}

		return maxValue == Double.NEGATIVE_INFINITY ? DEFAULT_Q_VALUE : maxValue;
	}

	public Action getActionForMaxQValue(Observation o) {
		HelicopterState state = new HelicopterState(o);

		HashSet<HelicopterAction> actions = actionsForStates.get(state);
		if (actions == null) {
			System.out.println("new action");
			actions = new HashSet<HelicopterAction>();
		}

		double maxValue = Double.NEGATIVE_INFINITY;
		Action maxAction = new Action(0, 4);
		for (HelicopterAction action : actions) {
			QKey key = new QKey(o, action.actionArray);
			Double value = qTable.get(key);
			System.out.println(value);
			if (value != null) {
				if (value > maxValue) {
					
					maxValue = value;
					maxAction.doubleArray = action.actionArray;
				}
			}
		}
		System.out.println("wtf " + qTable.values());
		return maxValue > Double.NEGATIVE_INFINITY ? maxAction : null;
	}

	public int size() {
		return qTable.size();
	}

	private class HelicopterState {
		double observationArray[] = new double[12];

		public HelicopterState(Observation o) {
			for(int i = 0; i < 12; i++){
				observationArray[i] = o.doubleArray[i];
			}
		}

		@Override
		public int hashCode() {
			double hash = observationArray[0] * observationArray[1] * observationArray[2] * observationArray[3] * observationArray[4]
					* observationArray[5] * observationArray[6] * observationArray[7] * observationArray[8] * observationArray[9]
					* observationArray[10] * observationArray[11];
			return (int) hash;
			// return o.doubleArray.hashCode(); // Helicopter only has
			// observations in the doubleArray
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof HelicopterState) {
				HelicopterState hs = (HelicopterState) obj;
				boolean equals = observationArray[0] == hs.observationArray[0] && observationArray[1] == hs.observationArray[1]
						&& observationArray[2] == hs.observationArray[2] && observationArray[3] == hs.observationArray[3]
						&& observationArray[4] == hs.observationArray[4] && observationArray[5] == hs.observationArray[5]
						&& observationArray[6] == hs.observationArray[6] && observationArray[7] == hs.observationArray[7]
						&& observationArray[8] == hs.observationArray[8] && observationArray[9] == hs.observationArray[9]
						&& observationArray[10] == hs.observationArray[10] && observationArray[11] == hs.observationArray[11];
				return equals;
			} else {
				return false;
			}

		}
	}

	private class HelicopterAction {
		private double actionArray[] = new double[4];

		public HelicopterAction(Action a) {
			for(int i = 0; i< 4; i++){
				actionArray[i] = a.doubleArray[i];
			}
			System.out.println(actionArray);
		}

		@Override
		public int hashCode() {
			double hash = actionArray[0] * actionArray[1] * actionArray[2] * actionArray[3];
			return (int) hash;
			// return actionArray.hashCode(); // Helicopter only has actions
			// in the doubleArray
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof HelicopterAction) {
				HelicopterAction ha = (HelicopterAction) obj;
				boolean equals = actionArray[0] == ha.actionArray[0] && actionArray[1] == ha.actionArray[1]
						&& actionArray[2] == ha.actionArray[2] && actionArray[3] == ha.actionArray[3];
				return equals;
			} else {
				return false;
			}

		}
	}

	private class QKey {
		double observationArray[] = new double[12];
		double actionArray[] = new double[4];
		
		public QKey(Observation state, Action action) {
			for(int i = 0; i< 12; i++){
				observationArray[i] = state.doubleArray[i];
			}
			for(int i = 0; i< 4; i++){
				actionArray[i] = action.doubleArray[i];
			}
		}
		
		public QKey(Observation state, double[] actions) {
			for(int i = 0; i< 12; i++){
				observationArray[i] = state.doubleArray[i];
			}
			for(int i = 0; i< 4; i++){
				actionArray[i] = actions[i];
			}
		}

		@Override
		public int hashCode() {
			int hash = (int) (observationArray[0] * observationArray[1] * observationArray[2] * observationArray[3] * observationArray[4]
					* observationArray[5] * observationArray[6] * observationArray[7] * observationArray[8] * observationArray[9]
					* observationArray[10] * observationArray[11]);
			
			hash *= (int) (actionArray[0] * actionArray[1] * actionArray[2] * actionArray[3]);
			
			return (int) hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof QKey) {
				QKey q = (QKey) obj;
				boolean equals = observationArray[0] == q.observationArray[0]
						&& observationArray[1] == q.observationArray[1] && observationArray[2] == q.observationArray[2]
						&& observationArray[3] == q.observationArray[3] && observationArray[4] == q.observationArray[4]
						&& observationArray[5] == q.observationArray[5] && observationArray[6] == q.observationArray[6]
						&& observationArray[7] == q.observationArray[7] && observationArray[8] == q.observationArray[8]
						&& observationArray[9] == q.observationArray[9] && observationArray[10] == q.observationArray[10]
						&& observationArray[11] == q.observationArray[11];
				
				equals = equals && actionArray[0] == q.actionArray[0] && actionArray[1] == q.actionArray[1]
						&& actionArray[2] == q.actionArray[2] && actionArray[3] == q.actionArray[3];

				return equals;
			} else {
				return false;
			}
		}
	}
}
