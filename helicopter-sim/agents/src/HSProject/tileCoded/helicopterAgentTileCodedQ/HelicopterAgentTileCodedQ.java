package HSProject.tileCoded.helicopterAgentTileCodedQ;



import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentQ;

public class HelicopterAgentTileCodedQ extends TileCodedAgentQ {
	private static double alpha = 0.1;
	private static double gamma = 1;
	
	public HelicopterAgentTileCodedQ() {
		super(alpha, gamma);
		
		
		int numTiles = 10;
		int numVariables = 12;
		int numTilings = 16;
		double[] statesMin = {-5, -5, -5, -20, -20, -20, -12.566, -12.566, -12.566, -1, -1, -1};
		double[] statesMax = {5, 5, 5, 20, 20, 20, 12.566, 12.566, 12.566, 1, 1, 1};
		
		initialiseStateTiling(numVariables, statesMin, statesMax, numTiles, numTilings);

		
		numTiles = 5;
		numVariables = 4;
		numTilings = 16;
		double[] actionsMin = {-1, -1, -1, -1};
		double[] actionsMax = {1, 1, 1, 1};

		initialiseActionTiling(numVariables, actionsMin, actionsMax, numTiles, numTilings);
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedQ());
		L.run();
	}

}
