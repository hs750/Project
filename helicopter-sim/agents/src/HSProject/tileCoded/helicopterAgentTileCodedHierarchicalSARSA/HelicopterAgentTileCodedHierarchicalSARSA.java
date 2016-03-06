package HSProject.tileCoded.helicopterAgentTileCodedHierarchicalSARSA;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.tileCoded.TileCodedAgentSARSA;
import HSProject.tileCoded.tilings.Tile;
import HSProject.tileCoded.tilings.TileCodeQTable;
import HSProject.tileCoded.tilings.TileCoding;
import HSProject.tileCoded.tilings.TileCodeQTableInterface.ActionValue;

/**
 * A learning agent that independently learns <b>aileron</b>, <b>elevator</b>,
 * <b>rudder</b> and <b>collective</b> controls. How these controls are
 * separated is based of the implementation in
 * {@link #agent_policy(Observation, Action)}
 * 
 * @author harrison
 *
 */
public class HelicopterAgentTileCodedHierarchicalSARSA extends TileCodedAgentSARSA {
	private enum StateMode {
		ALL, SUBSET
	}

	private enum StateType {
		AILERON, ELEVATION, RUDDER, COLLECTIVE;
	}

	private StateMode stateMode = StateMode.ALL;
	private StateType currentStateType = StateType.AILERON;

	// Q-Tables for Aileron, Elevator, Rudder and Collective
	private TileCodeQTable[] qTables = { new TileCodeQTable(), new TileCodeQTable(), new TileCodeQTable(),
			new TileCodeQTable() };

	private TileCoding[] stateTileCodings;
	int numStateTilings;

	public HelicopterAgentTileCodedHierarchicalSARSA() {
		stateMode = StateMode.valueOf(getConfig().getString("stateMode"));
		if (stateMode == StateMode.SUBSET) {
			initialiseSubsetMode();
		} else {
			initialiseAllMode();
		}

	}

	private void initialiseSubsetMode() {
		int numTiles = getConfig().getInt("stateTiles");
		numStateTilings = getConfig().getInt("stateTilings");

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

		int aNumTiles = getConfig().getInt("actionTiles");
		int aNumFeatures = 1;
		int numActionTilings = getConfig().getInt("actionTilings");
		double[] aFeatureMin = { -1 };
		double[] aFeatureMax = { 1 };

		// To trick the super into thinking we are using all its
		initialiseStateTiling(1, new double[1], new double[1], numTiles, numStateTilings);
		initialiseActionTiling(aNumFeatures, aFeatureMin, aFeatureMax, aNumTiles, numActionTilings);
	}

	private void initialiseAllMode() {
		int numTiles = getConfig().getInt("stateTiles");
		int numVariables = 12;
		int numTilings = getConfig().getInt("stateTilings");
		numStateTilings = numTilings;
		double[] statesMin = { -5, -5, -5, -20, -20, -20, -12.566, -12.566, -12.566, -1, -1, -1 };
		double[] statesMax = { 5, 5, 5, 20, 20, 20, 12.566, 12.566, 12.566, 1, 1, 1 };

		TileCoding stateTileCoding = new TileCoding(numTiles, numVariables, numStateTilings, statesMin, statesMax);
		stateTileCodings = new TileCoding[] { stateTileCoding };

		initialiseStateTiling(numVariables, statesMin, statesMax, numTiles, numTilings);

		numTiles = getConfig().getInt("actionTiles");
		numVariables = 1;
		numTilings = getConfig().getInt("actionTilings");
		double[] actionsMin = { -1 };
		double[] actionsMax = { 1 };

		initialiseActionTiling(numVariables, actionsMin, actionsMax, numTiles, numTilings);
	}

	@Override
	protected Observation manipulateState(Observation o) {
		return manipulateState(o, currentStateType);
	}

	private Observation manipulateState(Observation o, StateType st) {
		if (stateMode == StateMode.SUBSET) {
			double[] no = null;
			Observation newObs;
			switch (st) {
			case AILERON:
				newObs = new Observation(0, 3);
				no = new double[] { o.doubleArray[v_err], o.doubleArray[y_err], o.doubleArray[qx_err] };
				newObs.doubleArray = no;
				break;
			case ELEVATION:
				newObs = new Observation(0, 3);
				no = new double[] { o.doubleArray[u_err], o.doubleArray[x_err], o.doubleArray[qy_err] };
				newObs.doubleArray = no;
				break;
			case RUDDER:
				newObs = new Observation(0, 1);
				no = new double[] { o.doubleArray[qz_err] };
				newObs.doubleArray = no;
				break;
			case COLLECTIVE:
			default:
				newObs = new Observation(0, 2);
				no = new double[] { o.doubleArray[w_err], o.doubleArray[z_err] };
				newObs.doubleArray = no;
				break;
			}
			return newObs;
		} else {
			return o;
		}
	}

	protected TileCoding getStateTileCoding() {
		if (stateMode == StateMode.SUBSET) {
			return stateTileCodings[currentStateType.ordinal()];
		} else {
			return stateTileCodings[0];
		}
	}

	protected TileCodeQTable getQTable() {
		return qTables[currentStateType.ordinal()];
	}

	@Override
	public String agent_message(String message) {
		if (message.startsWith("action-")) {
			String ac = message.substring(7);
			currentStateType = StateType.valueOf(ac);
		}
		return super.agent_message(message);
	}

	@Override
	protected Action randomAction() {
		Action randAc = new Action(0, 1);
		randAc.setDouble(0, (randGenerator.nextDouble() * 2) - 1);
		return randAc;
	}

	private Action fillAction(Observation o, Action a) {
		Action theAction = new Action(0, 4);
		if (isExploringFrozen()) {
			for (StateType st : StateType.values()) {
				Tile[] tiles = new Tile[numStateTilings];
				stateTileCodings[stateMode == StateMode.ALL? 0 : st.ordinal()].getTiles(tiles, manipulateState(o, st).doubleArray);

				ActionValue maxAction = null;
				for (int i = 0; i < numStateTilings; i++) {
					ActionValue av = qTables[st.ordinal()].getMaxAction(tiles[i]);
					if (maxAction == null) {
						maxAction = av;
					} else if (av.getValue() > maxAction.getValue()) {
						maxAction = av;
					}
				}

				if (maxAction.getAction() == null) {
					theAction.setDouble(st.ordinal(), randomAction().getDouble(0));
				} else {
					theAction.setDouble(st.ordinal(), maxAction.getAction().getDouble(0));
				}
			}
		} else {
			agent_policy(o, theAction);

			theAction.setDouble(currentStateType.ordinal(), a.getDouble(0));
		}

		return theAction;
	}

	@Override
	public Action agent_start(Observation o) {
		Action a = super.agent_start(o);
		return fillAction(o, a);
	}

	@Override
	public Action agent_step(double reward, Observation o) {
		Action a = super.agent_step(reward, o);
		return fillAction(o, a);
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentTileCodedHierarchicalSARSA());
		L.run();
	}

}
