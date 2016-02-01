package HSProject.tileCoded;

import java.util.Random;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import HSProject.tileCoded.tilings.Tile;
import HSProject.tileCoded.tilings.TileCodeQTable;
import HSProject.tileCoded.tilings.TileCodeQTableInterface;
import HSProject.tileCoded.tilings.TileCodeQTableInterface.ActionValue;
import HSProject.tileCoded.tilings.TileCodedHelicopterAction;
import HSProject.tileCoded.tilings.TileCodedHelicopterState;
import HSProject.tileCoded.tilings.TileCoding;

/**
 * An RL-Glue agent that uses tile coding and epsilon-greedy exploration
 * 
 * @author harrison
 *
 */
public abstract class TileCodedAgent implements AgentInterface {
	private TileCodeQTableInterface qTable;
	private TileCoding stateTileCoding;
	private TileCoding actionTileCoding;

	private int numStateTilings = 32;
	private int numActionTilings = 4;

	private Action action;
	private Observation lastState;

	protected Random randGenerator = new Random();
	private double epsilon = 0.1;
	private boolean exploringFrozen = false;

	private TaskSpec TSO = null;

	private boolean explorationAction;

	private static double gb = 1024 * 1024 * 1024;

	// Indices into observation_t.doubleArray...
	@SuppressWarnings("unused")
	private static int u_err = 0, // forward velocity
			v_err = 1, // sideways velocity
			w_err = 2, // downward velocity
			x_err = 3, // forward error
			y_err = 4, // sideways error
			z_err = 5, // downward error
			p_err = 6, // angular rate around forward axis
			q_err = 7, // angular rate around sideways (to the right) axis
			r_err = 8, // angular rate around vertical (downward) axis
			qx_err = 9, // <-- quaternion entries, x,y,z,w q = [ sin(theta/2) *
						// axis; cos(theta/2)],
			qy_err = 10, // where axis = axis of rotation; theta is amount of
							// rotation around that axis
			qz_err = 11; // [recall: any rotation can be represented by a single
							// rotation around some axis]

	/**
	 * A new RL-Glue agent
	 */
	public TileCodedAgent() {
		qTable = new TileCodeQTable();

		System.out.println("JVM MEMORY = " + (Runtime.getRuntime().maxMemory() / gb) + "GB");
	}

	/**
	 * Replace the current q table with another.
	 * 
	 * @param qTable
	 *            the new Q Table
	 */
	protected void setQTable(TileCodeQTableInterface qTable) {
		this.qTable = qTable;
	}

	/**
	 * Initialise the tile coding of states. <br>
	 * <br>
	 * 
	 * Note: If this method is not called before connecting to RL-Glue a
	 * {@link TilingsNotInitialisedException} will be thrown on connection to
	 * RL-Glue
	 * 
	 * @param numStateVariables
	 *            the number of variables in the state
	 * @param statesMin
	 *            the minimum values of each of the state variables
	 * @param statesMax
	 *            the maximum values of each of the state variables
	 * @param numTiles
	 *            the number of tiles to use in each state dimension
	 * @param numTilings
	 *            the number of tilings to layer over the state variables
	 */
	protected void initialiseStateTiling(int numStateVariables, double[] statesMin, double[] statesMax, int numTiles,
			int numTilings) {
		numStateTilings = numTilings;
		stateTileCoding = new TileCoding(numTiles, numStateVariables, numStateTilings, statesMin, statesMax);
	}

	/**
	 * Initialise the tile coding of actions<br>
	 * <br>
	 * 
	 * Note: If this method is not called before connecting to RL-Glue a
	 * {@link TilingsNotInitialisedException} will be thrown on connection to
	 * RL-Glue
	 * 
	 * @param numActionVariables
	 *            the number of variables in the action
	 * @param actionsMin
	 *            the minimum values of each of the action variables
	 * @param actionsMax
	 *            the maximum values of each of the action variables
	 * @param numTiles
	 *            the number of tiles to use in each action dimension
	 * @param numTilings
	 *            the number of tilings to layer over the action variables
	 */
	protected void initialiseActionTiling(int numActionVariables, double[] actionsMin, double[] actionsMax,
			int numTiles, int numTilings) {
		numActionTilings = numTilings;
		actionTileCoding = new TileCoding(numTiles, numActionVariables, numActionTilings, actionsMin, actionsMax);
	}

	/**
	 * Get the agent's Q Table
	 * 
	 * @return the q table
	 */
	protected TileCodeQTableInterface getQTable() {
		return qTable;
	}

	/**
	 * 
	 * @return the action the agent will invoke on the next time step
	 */
	protected Action getNextAction() {
		return action;
	}

	/**
	 * Get the tile coding for the the actions
	 * 
	 * @return the action tile coding
	 */
	protected TileCoding getActionTileCoding() {
		return actionTileCoding;
	}

	/**
	 * get the tile coding for the states
	 * 
	 * @return the state tile coding
	 */
	protected TileCoding getStateTileCoding() {
		return stateTileCoding;
	}

	/**
	 * @deprecated This is unused
	 */
	@Override
	public void agent_cleanup() {
	}

	/**
	 * Learn from the last time step in the episode.
	 * 
	 * @param reward
	 *            the final reward from the episode
	 */
	@Override
	public void agent_end(double reward) {
		if (!exploringFrozen) {
			// Get all the tiles for the current state
			Tile[] curStates = new Tile[numStateTilings];
			stateTileCoding.getTiles(curStates, new TileCodedHelicopterState(lastState));

			Tile[] actions = new Tile[numActionTilings];
			actionTileCoding.getTiles(actions, new TileCodedHelicopterAction(action));

			learnEnd(reward, curStates, actions);
		}
	}

	/**
	 * Initialise the agent. Called on connection to RL-Glue. <br>
	 * <br>
	 * 
	 * @param taskSpec
	 *            the task specification provided by the experiment trainer via
	 *            RL-Glue
	 * @throws TilingsNotInitialisedException
	 *             if {@link #initialiseStateTiling} or
	 *             {@link #initialiseActionTiling} not called.
	 * 
	 */
	@Override
	public void agent_init(String taskSpec) {
		if (stateTileCoding == null) {
			throw new TilingsNotInitialisedException("State tiling not set");
		}
		if (actionTileCoding == null) {
			throw new TilingsNotInitialisedException("Action tiling not set");
		}

		System.out.println(taskSpec);
		TSO = new TaskSpec(taskSpec);
		action = new Action(TSO.getNumDiscreteActionDims(), TSO.getNumContinuousActionDims());

		lastState = new Observation(0, TSO.getNumContinuousActionDims());
	}

	/**
	 * Allows the experiment trainer and the agent to communicate. Currently
	 * this only recieves two messages:
	 * <ul>
	 * <li><code>freeze-learning</code> Stops the agent updating its q table and
	 * from taking exploratory actions</li>
	 * <li><code>unfreeze-learning</code> Resumes the agents learning.
	 * </ul>
	 * 
	 * @param message
	 *            the message received from the experiment trainer containing
	 *            one of the two above messages
	 * @return <code>null</code>
	 */
	@Override
	public String agent_message(String message) {
		if (message.equals("freeze-learning")) {
			exploringFrozen = true;
			System.out.println("Evaluation! States=" + qTable.getNumStates() + " Memory="
					+ (Runtime.getRuntime().totalMemory() / gb));
		} else if (message.equals("unfreeze-learning")) {
			exploringFrozen = false;
			System.out.println("Learning");
		}
		return null;
	}

	/**
	 * Initialises the first step of an episode with a state and chooses a
	 * (random) action to procede to the next step in the episode
	 * 
	 * @param o
	 *            the first state of the episode
	 * @return the first action of the episode
	 */
	@Override
	public Action agent_start(Observation o) {
		lastState = o;
		action = egreedy(o);
		return action;
	}

	/**
	 * The agent takes a step in the episode
	 * 
	 * @param reward
	 *            the reward the agent received from the last state and action
	 * @param o
	 *            the new state
	 * @return the action to perfom based on o
	 */
	public Action agent_step(double reward, Observation o) {
		o = manipulateState(o);

		Action lastAction = action;

		action = egreedy(o);

		if (!exploringFrozen) {
			// Get all the tiles for the current state
			Tile[] curStates = new Tile[numStateTilings];
			stateTileCoding.getTiles(curStates, new TileCodedHelicopterState(lastState));

			// Get all the tiles of the new states
			Tile[] newStates = new Tile[numStateTilings];
			stateTileCoding.getTiles(newStates, new TileCodedHelicopterState(o));

			Tile[] actions = new Tile[numActionTilings];
			actionTileCoding.getTiles(actions, new TileCodedHelicopterAction(lastAction));

			learn(reward, lastAction, curStates, actions, newStates);
		}
		lastState = o;

		return action;
	}

	/**
	 * Learn from the last and next states and actions. Subclasses should
	 * implement a Reinforcement Learning algorithm.
	 * 
	 * @param reward
	 *            the reward received for the last state-action pair
	 * @param lastAction
	 *            the last action taken
	 * @param tiledLastStates
	 *            the last state, tiled
	 * @param tiledLastActions
	 *            the last action, tiled
	 * @param tiledCurStates
	 *            the current state, tiled
	 */
	protected abstract void learn(double reward, Action lastAction, Tile[] tiledLastStates, Tile[] tiledLastActions,
			Tile[] tiledCurStates);

	/**
	 * Learn from the last state and actions. Subclasses should implement a
	 * Reinforcement Learning algorithm. This method is only called on the last
	 * step of an episode
	 * 
	 * @param reward
	 *            the reward received for the last state-action pair
	 * @param tiledLastStates
	 *            the last state, tiled
	 * @param tiledLastActions
	 *            the last action, tiled
	 */
	protected abstract void learnEnd(double reward, Tile[] tiledLastStates, Tile[] tiledLastActions);

	/**
	 * Find a random action based on a state. Default behaviour is to ignore the
	 * state and call {@link #randomAction()}. Potential use is to use the state
	 * to bias the random selection.
	 * 
	 * @param o
	 *            the state to base the random action off of
	 * @return the randomly selected action
	 */
	protected Action randomAction(Observation o) {
		return randomAction();
	}

	/**
	 * Modify the input state in some way and return the modified state. Default
	 * behaviour is to do nothing to the state.
	 * 
	 * @param o
	 *            the state to modify
	 * @return the modified state
	 */
	protected Observation manipulateState(Observation o) {
		// By default do nothing
		return o;
	}

	/**
	 * @return whether or not the last action chosen was an exploratory action.
	 */
	protected boolean lastActionExploration() {
		return explorationAction;
	}

	/**
	 * Randomly select an action
	 * 
	 * @return the randomly selected action
	 */
	Action randomAction() {
		Action a = new Action(0, 4);
		a.doubleArray[0] = (randGenerator.nextDouble() * 2) - 1;
		a.doubleArray[1] = (randGenerator.nextDouble() * 2) - 1;
		a.doubleArray[2] = (randGenerator.nextDouble() * 2) - 1;
		a.doubleArray[3] = (randGenerator.nextDouble() * 2) - 1;
		return a;
	}

	/**
	 *
	 * Selects a random action with probability 1-epsilon, and the action with
	 * the highest value otherwise. No tie-breaking is handled.
	 * 
	 * @param theState
	 *            the state to base the action selection off of.
	 * @return the action selected
	 */
	private Action egreedy(Observation theState) {
		if (!exploringFrozen) {
			explorationAction = randGenerator.nextDouble() <= epsilon;
			if (explorationAction) {
				return randomAction(theState);
			}
		}

		Tile[] tiles = new Tile[numStateTilings];
		stateTileCoding.getTiles(tiles, new TileCodedHelicopterState(theState));

		// HashIntDoubleMap actionValues = HashIntDoubleMaps.newUpdatableMap();
		// Map<Integer, Action> actions =
		// HashIntObjMaps.<Action>newUpdatableMap();
		// HashIntIntMap actionUsages = HashIntIntMaps.newUpdatableMap();
		//
		// //average over just state tiles
		// for(int i = 0; i<numStateTilings; i++){
		// ActionValue av = qTable.getMaxAction(tiles[i]);
		// if(av!= null){
		// int usages = actionUsages.getOrDefault(av.getActionTile(), 0);
		// if(usages > 0){
		// Action action = actions.get(av.getActionTile());
		// if(action != null && av.getAction() != null){
		// action.doubleArray[0] += av.getAction().getDouble(0);
		// action.doubleArray[1] += av.getAction().getDouble(1);
		// action.doubleArray[2] += av.getAction().getDouble(2);
		// action.doubleArray[3] += av.getAction().getDouble(3);
		// }
		// }else{
		// actions.put(av.getActionTile(), av.getAction());
		// }
		// actionUsages.addValue(av.getActionTile(), 1);
		// actionValues.addValue(av.getActionTile(), av.getValue());
		// }
		// }

		// average over state and action tiles
		// for(int i=0; i< numStateTilings; i++){
		// Map<Integer, ActionValue> avs = qTable.getActionsValues(tiles[i]);
		// if(avs != null){
		// avs.forEach((a, av)->{
		// int usages = actionUsages.getOrDefault((int)a, 0);
		// if(usages > 0){
		// Action action = actions.get((int) a);
		// action.doubleArray[0] += av.getAction().getDouble(0);
		// action.doubleArray[1] += av.getAction().getDouble(1);
		// action.doubleArray[2] += av.getAction().getDouble(2);
		// action.doubleArray[3] += av.getAction().getDouble(3);
		// }else{
		// actions.put((int) a, av.getAction());
		// }
		// actionUsages.addValue((int) a, 1);
		// actionValues.addValue((int)a, av.getValue());
		//
		// });
		// }
		// }

		// Integer maxAction = null;
		// double maxValue = Integer.MIN_VALUE;
		// for(Entry<Integer, Double> avs : actionValues.entrySet()){
		// if(maxAction == null){
		// maxAction = avs.getKey();
		// }else{
		// if(avs.getValue() > maxValue){
		// maxAction = avs.getKey();
		// maxValue = avs.getValue();
		// }
		// }
		// }
		//
		// if(maxAction != null){
		// Action theAction = actions.get(maxAction);
		// if(theAction !=null){
		// double usages = actionUsages.get((int)maxAction);
		// theAction.doubleArray[0] /= usages;
		// theAction.doubleArray[1] /= usages;
		// theAction.doubleArray[2] /= usages;
		// theAction.doubleArray[3] /= usages;
		// return theAction;
		// }
		//
		//
		// }
		// return randomAction(theState);

		ActionValue maxAction = null;
		for (int i = 0; i < numStateTilings; i++) {
			ActionValue av = qTable.getMaxAction(tiles[i]);
			if (maxAction == null) {
				maxAction = av;
			} else if (av.getValue() > maxAction.getValue()) {
				maxAction = av;
			}
		}

		if (maxAction.getAction() == null) {
			return randomAction(theState);
		}
		return maxAction.getAction();
	}

	/**
	 * A controller for the helicopter which performs well but not optimally.
	 * This method was provided by the reinforcement learning competition.
	 * 
	 * @param o
	 *            the state
	 * @param a
	 *            the action (return via this parameter)
	 */
	protected void agent_policy(Observation o, Action a) {
		double weights[] = { 0.0196, 0.7475, 0.0367, 0.0185, 0.7904, 0.0322, 0.1969, 0.0513, 0.1348, 0.02, 0, 0.23 };

		int y_w = 0;
		int roll_w = 1;
		int v_w = 2;
		int x_w = 3;
		int pitch_w = 4;
		int u_w = 5;
		int yaw_w = 6;
		int z_w = 7;
		int w_w = 8;
		int ail_trim = 9;
		int el_trim = 10;
		int coll_trim = 11;

		// x/y/z_error = body(x - x_target)
		// q_error = inverse(Q_target) * Q, where Q is the orientation of the
		// helicopter
		// roll/pitch/yaw_error = scaled_axis(q_error)

		// collective control
		double coll = weights[z_w] * o.doubleArray[z_err] + weights[w_w] * o.doubleArray[w_err] + weights[coll_trim];

		// forward-backward control
		double elevator = -weights[x_w] * o.doubleArray[x_err] + -weights[u_w] * o.doubleArray[u_err]
				+ weights[pitch_w] * o.doubleArray[qy_err] + weights[el_trim];

		// left-right control
		double aileron = -weights[y_w] * o.doubleArray[y_err] + -weights[v_w] * o.doubleArray[v_err]
				+ -weights[roll_w] * o.doubleArray[qx_err] + weights[ail_trim];

		double rudder = -weights[yaw_w] * o.doubleArray[qz_err];

		a.doubleArray[0] = aileron;
		a.doubleArray[1] = elevator;
		a.doubleArray[2] = rudder;
		a.doubleArray[3] = coll;
	}

	/**
	 * An exception thrown when and agents tilings are not initialised. Extends
	 * {@link NullPointerException}.
	 * 
	 * @see TileCodedAgent#initialiseStateTiling
	 * @see TileCodedAgent#initialiseActionTiling
	 * 
	 * @author harrison
	 *
	 */
	public class TilingsNotInitialisedException extends NullPointerException {
		private static final long serialVersionUID = -3106567915978439442L;

		public TilingsNotInitialisedException() {
			super();
		}

		public TilingsNotInitialisedException(String arg) {
			super(arg);
		}
	}

}
