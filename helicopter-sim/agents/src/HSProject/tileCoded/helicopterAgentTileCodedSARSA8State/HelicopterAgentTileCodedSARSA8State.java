package HSProject.tileCoded.helicopterAgentTileCodedSARSA8State;



import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentSARSA;

public class HelicopterAgentTileCodedSARSA8State extends TileCodedAgentSARSA {
	private static double alpha = 0.1;
	private static double gamma = 1;
	
	public HelicopterAgentTileCodedSARSA8State() {
		super(alpha, gamma);
		
		
		int numTiles = 10;
		int numVariables = 8;
		int numTilings = 16;
		double[] statesMin = {-5, -5, -5, -12.566, -12.566, -12.566, -1, -1};
		double[] statesMax = {5, 5, 5,12.566, 12.566, 12.566, 1, 1};
		
		initialiseStateTiling(numVariables, statesMin, statesMax, numTiles, numTilings);

		
		numTiles = 5;
		numVariables = 4;
		numTilings = 16;
		double[] actionsMin = {-1, -1, -1, -1};
		double[] actionsMax = {1, 1, 1, 1};

		initialiseActionTiling(numVariables, actionsMin, actionsMax, numTiles, numTilings);
	}
	
	@Override
	protected Observation manipulateState(Observation o){
		Observation newO = new Observation(0, 8);
		// Velocities
		newO.doubleArray[0] = o.doubleArray[0];
		newO.doubleArray[1] = o.doubleArray[1];
		newO.doubleArray[2] = o.doubleArray[2];
		
		// Angular Velocities 
		newO.doubleArray[3] = o.doubleArray[6];
		newO.doubleArray[4] = o.doubleArray[7];
		newO.doubleArray[5] = o.doubleArray[8];
		
		// Roll Pitch
		newO.doubleArray[6] = o.doubleArray[9];
		newO.doubleArray[7] = o.doubleArray[10];
		
		
		return newO;
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedSARSA8State());
		L.run();
	}

}
