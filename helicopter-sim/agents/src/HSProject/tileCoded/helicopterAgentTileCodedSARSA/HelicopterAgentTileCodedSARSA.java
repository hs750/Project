package HSProject.tileCoded.helicopterAgentTileCodedSARSA;



import java.util.HashMap;
import java.util.Random;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentSARSA;
import HSProject.tileCoded.helicopterAgentTileCodedQ.HelicopterAgentTileCodedQ;
import HSProject.tileCoded.tilings.Tile;
import HSProject.tileCoded.tilings.TileCodeQTable;
import HSProject.tileCoded.tilings.TileCodeQTableFlat;
import HSProject.tileCoded.tilings.TileCodeQTableInterface;
import HSProject.tileCoded.tilings.TileCodedHelicopterAction;
import HSProject.tileCoded.tilings.TileCodedHelicopterState;
import HSProject.tileCoded.tilings.TileCoding;
import HSProject.tileCoded.tilings.TileCodeQTableInterface.ActionValue;

@SuppressWarnings("unused")
public class HelicopterAgentTileCodedSARSA extends TileCodedAgentSARSA {
	private static double alpha = 0.1;
	private static double gamma = 1;
	
	public HelicopterAgentTileCodedSARSA() {
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
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedSARSA());
		L.run();
	}
}