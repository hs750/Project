package HSProject.tileCoded.helicopterAgentTileCodedEligibilityQ;

import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentQλ;

/**
 * An experiment applying Q-Learning with eligibility traces.
 * 
 * @author harrison
 *
 */
public class HelicopterAgentTileCodedEligibilityQ extends TileCodedAgentQλ {

	private int numStateTilings;
	private int numActionTilings;

	public HelicopterAgentTileCodedEligibilityQ() {

		int numTiles = getConfig().getInt("stateTiles");
		int numVariables = 12;
		numStateTilings = getConfig().getInt("stateTilings");
		double[] statesMin = { -5, -5, -5, -20, -20, -20, -12.566, -12.566, -12.566, -1, -1, -1 };
		double[] statesMax = { 5, 5, 5, 20, 20, 20, 12.566, 12.566, 12.566, 1, 1, 1 };

		initialiseStateTiling(numVariables, statesMin, statesMax, numTiles, numStateTilings);

		numTiles = getConfig().getInt("actionTiles");
		numVariables = 4;
		numActionTilings = getConfig().getInt("actionTilings");
		double[] actionsMin = { -1, -1, -1, -1 };
		double[] actionsMax = { 1, 1, 1, 1 };

		initialiseActionTiling(numVariables, actionsMin, actionsMax, numTiles, numActionTilings);
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedEligibilityQ());
		L.run();
	}

}
