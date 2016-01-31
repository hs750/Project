package HSProject.tileCoded.tilings;

import java.util.Map;

import net.openhft.koloboke.collect.map.hash.HashIntDoubleMaps;
import net.openhft.koloboke.collect.map.hash.HashIntObjMaps;

public class EligibilityQTable extends TileCodeQTable {
	Map<Integer, Map<Integer, Double>> eligibilityTable;

	double alpha;
	double gamma;
	double lambda;

	public EligibilityQTable(double alpha, double gamma, double lambda) {
		this.alpha = alpha;
		this.gamma = gamma;
		this.lambda = lambda;

		eligibilityTable = HashIntObjMaps.<Map<Integer, Double>> newUpdatableMap();
	}

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

	public void updateEligibility(Tile state, Tile action, double eligibility) {
		putEligibilityValue(state.hashCode(), action.hashCode(), eligibility);
	}

	public double getEligibility(Tile state, Tile action) {
		return getEligibilityValue(state.hashCode(), action.hashCode());
	}

}
