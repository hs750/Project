package HSProject.tileCoded.tilings;

import java.util.Map;

import net.openhft.koloboke.collect.map.hash.HashIntDoubleMaps;
import net.openhft.koloboke.collect.map.hash.HashIntObjMaps;

/**
 * Stores eligibility values for state-action pairs along with Q values.
 * 
 * @author harrison
 *
 */
public class EligibilityQTable extends TileCodeQTable {
	Map<Integer, Map<Integer, Double>> eligibilityTable;

	/**
	 * A new eligibility table
	 */
	public EligibilityQTable() {
		eligibilityTable = HashIntObjMaps.<Map<Integer, Double>> newUpdatableMap();
	}

	/**
	 * Get the eligibility value for a state action pair
	 * 
	 * @param state
	 *            the state
	 * @param action
	 *            the action
	 * @return the value
	 */
	private double getEligibilityValue(Integer state, Integer action) {
		Map<Integer, Double> eligActions = eligibilityTable.get(state);
		if (eligActions == null) {
			return 0;
		}
		Double oldElig = eligActions.get(action);
		if (oldElig == null) {
			return 0;
		}
		return oldElig;
	}

	/**
	 * Update the eligibility value of a state action pair
	 * 
	 * @param state
	 *            the state
	 * @param action
	 *            the action
	 * @param elig
	 *            the new eligibility value
	 */
	private void putEligibilityValue(Integer state, Integer action, double elig) {
		Map<Integer, Double> eligActions = eligibilityTable.get(state);
		boolean nullEligibility = eligActions == null;
		if (nullEligibility) {
			eligActions = HashIntDoubleMaps.newUpdatableMap();
		}
		eligActions.put(action, elig);
		if (nullEligibility) {
			eligibilityTable.put(state, eligActions);
		}
	}

	/**
	 * Update the eligibility value of a state action pair
	 * 
	 * @param state
	 *            the state
	 * @param action
	 *            the action
	 * @param elig
	 *            the new eligibility value
	 */
	public void updateEligibility(Tile state, Tile action, double eligibility) {
		putEligibilityValue(state.hashCode(), action.hashCode(), eligibility);
	}

	/**
	 * Get the eligibility value for a state action pair
	 * 
	 * @param state
	 *            the state
	 * @param action
	 *            the action
	 * @return the value
	 */
	public double getEligibility(Tile state, Tile action) {
		return getEligibilityValue(state.hashCode(), action.hashCode());
	}

}
