package HSProject.tileCoded.tilings;

import org.rlcommunity.rlglue.codec.types.Action;

/**
 * A Q-Table <br>
 * A structure for storing the values for state action pairs <code>Q(s,a)</code>
 * when tile coding is being used.
 * 
 * @author harrison
 *
 */
public interface TileCodeQTableInterface {

	/**
	 * Get the value for the state action pair.
	 * 
	 * @param state
	 *            the state
	 * @param action
	 *            the action
	 * @return the value
	 */
	public double getQValue(Tile state, Tile action);

	/**
	 * Get the maximum value for all the actions available from the given state
	 * 
	 * @param state
	 *            the state
	 * @return the value
	 */
	public double getMaxQValue(Tile state);

	/**
	 * Get the action for the given state that has the highest value
	 * 
	 * @param state
	 *            the state
	 * @return the action and its value
	 */
	public ActionValue getMaxAction(Tile state);

	/**
	 * Insert the value for a state action pair
	 * 
	 * @param state
	 *            the state
	 * @param action
	 *            the action
	 * @param value
	 *            the value
	 * @param actualAction
	 *            the RL-Glue action that the tiled action was generated from
	 */
	public void put(Tile state, Tile action, double value, Action actualAction);

	/**
	 * @return The number of states being stored in this Q-Table
	 */
	public int getNumStates();

	/**
	 * A structure containing an RL-Glue action and its value
	 * 
	 * @author harrison
	 *
	 */
	public class ActionValue {
		private double value;
		private Action actualAction;

		/**
		 * 
		 * @param value
		 *            the value
		 * @param actualAction
		 *            the RL-Glue action
		 */
		public ActionValue(double value, Action actualAction) {
			this.value = value;
			if (actualAction != null) {
				this.actualAction = new Action(actualAction);
			} else {
				this.actualAction = null;
			}
		}

		/**
		 * Update the value and action stored in this structure
		 * 
		 * @param value
		 *            the new value
		 * @param action
		 *            the new action
		 */
		public void update(double value, Action action) {
			this.value = value;
			this.actualAction = action;
		}

		/**
		 * Update only the value stored in this structure
		 * 
		 * @param value
		 *            the new value
		 */
		public void setValue(double value) {
			this.value = value;
		}

		/**
		 * 
		 * @return the value for this action
		 */
		public double getValue() {
			return value;
		}

		/**
		 * 
		 * @return the RL-Glue action
		 */
		public Action getAction() {
			return actualAction;
		}

	}

}
