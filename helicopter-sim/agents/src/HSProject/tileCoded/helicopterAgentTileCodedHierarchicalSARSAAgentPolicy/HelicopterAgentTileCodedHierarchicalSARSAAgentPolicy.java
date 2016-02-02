package HSProject.tileCoded.helicopterAgentTileCodedHierarchicalSARSAAgentPolicy;

import java.util.ArrayList;
import java.util.Random;

import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentSARSA;
import HSProject.tileCoded.tilings.Tile;
import HSProject.tileCoded.tilings.TileCodeQTable;
import HSProject.tileCoded.tilings.TileCodeQTableInterface;
import HSProject.tileCoded.tilings.TileCodeQTableInterface.ActionValue;
import HSProject.tileCoded.tilings.TileCoding;

/**
 * A learning agent that independently learns <b>aileron</b>, <b>elevator</b>,
 * <b>rudder</b> and <b>collective</b> controls. How these controls are
 * separated is based of the implementation in
 * {@link #agent_policy(Observation, Action)}
 * 
 * @author harrison
 *
 */
public class HelicopterAgentTileCodedHierarchicalSARSAAgentPolicy extends TileCodedAgentSARSA {
	private enum StateType {
		AILERON, ELEVATION, RUDDER, COLLECTIVE;
	}

	// Q-Tables for Aileron, Elevator, Rudder and Collective
	private TileCodeQTable[] qTables = { new TileCodeQTable(), new TileCodeQTable(), new TileCodeQTable(),
			new TileCodeQTable() };

	// Different State tilings needed for the 4 types of states
	private TileCoding[] stateTileCodings;

	// can all use the same action tiling though
	private TileCoding actionTileCoding;

	private static final int NUM_CODINGS = 4;

	int numStateTilings = 32;
	int numActionTilings = 4;

	protected Random randGenerator = new Random();
	private double epsilon = 0.1;
	private boolean exploringFrozen = false;

	TaskSpec TSO = null;

	static double alpha = 0.1;
	static double gamma = 1;

	private StateType currentStateType = StateType.AILERON;

	public HelicopterAgentTileCodedHierarchicalSARSAAgentPolicy() {
		super(alpha, gamma);
		int numTiles = 20;
		numStateTilings = 128;

		// {Sideways velocity, Sideways Pos, y axis rotation}
		double[] featureMin_aileron = { -5, -20, -1 };
		double[] featureMax_aileron = { 5, 20, 1 };

		// {Forwards velocity, Forwards Pos, x axis rotation}
		double[] featureMin_elevation = { -5, -20, -1 };
		double[] featureMax_elevation = { 5, 20, 1 };

		// {z axis rotation}
		double[] featureMin_rudder = { -1 };
		double[] featureMax_rudder = { 1 };

		// {Downward velocity, Downward Pos}
		double[] featureMin_collective = { -5, -20 };
		double[] featureMax_collective = { 5, 20 };

		int numFeatures_aileron = 3;
		int numFeatures_elevation = 3;
		int numFeatures_rudder = 1;
		int numFeatures_collective = 2;

		TileCoding stateTileCoding_aileron = new TileCoding(numTiles, numFeatures_aileron, numStateTilings,
				featureMin_aileron, featureMax_aileron);
		TileCoding stateTileCoding_elevation = new TileCoding(numTiles, numFeatures_elevation, numStateTilings,
				featureMin_elevation, featureMax_elevation);
		TileCoding stateTileCoding_rudder = new TileCoding(numTiles, numFeatures_rudder, numStateTilings,
				featureMin_rudder, featureMax_rudder);
		TileCoding stateTileCoding_collective = new TileCoding(numTiles, numFeatures_collective, numStateTilings,
				featureMin_collective, featureMax_collective);
		stateTileCodings = new TileCoding[] { stateTileCoding_aileron, stateTileCoding_elevation,
				stateTileCoding_rudder, stateTileCoding_collective };

		int aNumTiles = 40;
		int aNumFeatures = 1;
		numActionTilings = 32;
		double[] aFeatureMin = { -1 };
		double[] aFeatureMax = { 1 };
		actionTileCoding = new TileCoding(aNumTiles, aNumFeatures, numActionTilings, aFeatureMin, aFeatureMax);

		// To trick the super into thinking we are using all its
		initialiseStateTiling(1, new double[1], new double[1], 1, numStateTilings);
		initialiseActionTiling(1, new double[1], new double[1], 1, numActionTilings);
	}

	private double[] generateState(Observation o, StateType st) {
		double[] no = null;
		switch (st) {
		case AILERON:
			no = new double[] { o.doubleArray[v_err], o.doubleArray[y_err], o.doubleArray[qx_err] };
			break;
		case ELEVATION:
			no = new double[] { o.doubleArray[u_err], o.doubleArray[x_err], o.doubleArray[qy_err] };
			break;
		case RUDDER:
			no = new double[] { o.doubleArray[qz_err] };
			break;
		case COLLECTIVE:
		default:
			no = new double[] { o.doubleArray[w_err], o.doubleArray[z_err] };
			break;
		}

		return no;
	}

	private double[] generateAction(Action a, StateType st) {
		double[] na = new double[] { a.doubleArray[st.ordinal()] };
		return na;
	}

	// Learn from last reward
	public void agent_end(double reward) {

		if (!exploringFrozen) {
			// Get all the tiles for the current state
			ArrayList<Tile[]> currentStates = new ArrayList<Tile[]>(NUM_CODINGS);

			for (int s = 0; s < NUM_CODINGS; s++) {
				Tile[] curStates = new Tile[numStateTilings];
				double[] tchs = generateState(getLastState(), StateType.values()[s]);
				stateTileCodings[s].getTiles(curStates, tchs);
				currentStates.add(curStates);

			}

			ArrayList<Tile[]> currentActions = new ArrayList<Tile[]>(NUM_CODINGS);
			for (int a = 0; a < NUM_CODINGS; a++) {
				Tile[] actions = new Tile[numActionTilings];
				double[] tcha = generateAction(getAction(), StateType.values()[a]);
				actionTileCoding.getTiles(actions, tcha);
				currentActions.add(actions);
			}

			for (int sa = 0; sa < NUM_CODINGS; sa++) {
				currentStateType = StateType.values()[sa];
				learnEnd(reward, currentStates.get(sa), currentActions.get(sa));
			}
		}
	}

	public Action agent_step(double reward, Observation o) {
		Action lastAction = getAction();

		setAction(egreedy(o));

		if (!exploringFrozen) {
			// Get all the tiles for the current state
			ArrayList<Tile[]> currentStates = new ArrayList<Tile[]>(NUM_CODINGS);

			for (int s = 0; s < NUM_CODINGS; s++) {
				Tile[] curStates = new Tile[numStateTilings];
				double[] tchs = generateState(getLastState(), StateType.values()[s]);
				stateTileCodings[s].getTiles(curStates, tchs);
				currentStates.add(curStates);

			}

			ArrayList<Tile[]> currentActions = new ArrayList<Tile[]>(NUM_CODINGS);
			for (int a = 0; a < NUM_CODINGS; a++) {
				Tile[] actions = new Tile[numActionTilings];
				double[] tcha = generateAction(lastAction, StateType.values()[a]);
				actionTileCoding.getTiles(actions, tcha);
				currentActions.add(actions);
			}

			// Get all the tiles of the new states
			ArrayList<Tile[]> newStates = new ArrayList<Tile[]>(12);
			for (int s = 0; s < NUM_CODINGS; s++) {
				Tile[] newStates_ = new Tile[numStateTilings];
				double[] tchs = generateState(o, StateType.values()[s]);
				stateTileCodings[s].getTiles(newStates_, tchs);
				newStates.add(newStates_);

			}

			for (int sa = 0; sa < NUM_CODINGS; sa++) {
				currentStateType = StateType.values()[sa];
				learn(reward, lastAction, currentStates.get(sa), currentActions.get(sa), newStates.get(sa));
			}
		}
		setLastState(o);
		// System.out.println(qTable.size());
		return getAction();
	}

	@Override
	protected TileCodeQTableInterface getQTable() {
		return qTables[currentStateType.ordinal()];
	}

	@Override
	protected Action egreedy(Observation theState) {
		if (!exploringFrozen) {
			if (randGenerator.nextDouble() <= epsilon) {
				return randomAction();
			}
		}
		ArrayList<Tile[]> states = new ArrayList<Tile[]>(NUM_CODINGS);
		for (int s = 0; s < NUM_CODINGS; s++) {
			Tile[] tile = new Tile[numStateTilings];
			double[] tchs = generateState(theState, StateType.values()[s]);
			stateTileCodings[s].getTiles(tile, tchs);
			states.add(tile);
		}

		Action theAction = new Action(0, 4);
		for (int s = 0; s < NUM_CODINGS; s++) {
			ActionValue maxAction = null;
			for (int i = 0; i < numStateTilings; i++) {
				ActionValue av = qTables[s].getMaxAction(states.get(s)[i]);
				if (maxAction == null) {
					maxAction = av;
				} else if (av.getValue() > maxAction.getValue()) {
					maxAction = av;
				}
			}
			if (maxAction.getAction() != null) {
				theAction.doubleArray[s] = maxAction.getAction().getDouble(0);
			} else {
				System.out.println("                                             R");
				theAction.doubleArray[s] = randGenerator.nextDouble() * 2 - 1;

			}
		}
		return theAction;
	}

	@Override
	public int getNumStates() {
		return qTables[0].getNumStates();
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedHierarchicalSARSAAgentPolicy());
		L.run();
	}

}
