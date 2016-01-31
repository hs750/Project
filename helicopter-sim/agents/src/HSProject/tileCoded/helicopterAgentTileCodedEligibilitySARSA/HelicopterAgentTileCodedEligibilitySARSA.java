package HSProject.tileCoded.helicopterAgentTileCodedEligibilitySARSA;

import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentSARSAλ;

public class HelicopterAgentTileCodedEligibilitySARSA extends TileCodedAgentSARSAλ {
	private static double alpha = 0.1;
	private static double gamma = 1;
	private static double lambda = 0;

	private int numStateTilings;
	private int numActionTilings;

	public HelicopterAgentTileCodedEligibilitySARSA() {
		super(alpha, gamma, lambda);

		int numTiles = 10;
		int numVariables = 12;
		numStateTilings = 16;
		double[] statesMin = { -5, -5, -5, -20, -20, -20, -12.566, -12.566, -12.566, -1, -1, -1 };
		double[] statesMax = { 5, 5, 5, 20, 20, 20, 12.566, 12.566, 12.566, 1, 1, 1 };

		initialiseStateTiling(numVariables, statesMin, statesMax, numTiles, numStateTilings);

		numTiles = 5;
		numVariables = 4;
		numActionTilings = 16;
		double[] actionsMin = { -1, -1, -1, -1 };
		double[] actionsMax = { 1, 1, 1, 1 };

		initialiseActionTiling(numVariables, actionsMin, actionsMax, numTiles, numActionTilings);
	}
	
	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedEligibilitySARSA());
		L.run();
	}

}
