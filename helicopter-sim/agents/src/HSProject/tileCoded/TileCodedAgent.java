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

@SuppressWarnings("unused")
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

	private double alpha = 0.1;
	private double gamma = 1;

	// Indices into observation_t.doubleArray...
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
	
	public TileCodedAgent(double alpha, double gamma) {
		this.alpha = alpha;
		this.gamma = gamma;

		qTable = new TileCodeQTable();
	}
	
	protected void setQTable(TileCodeQTableInterface qTable){
		this.qTable = qTable;
	}

	protected void initialiseStateTiling(int numStateVariables, double[] statesMin, double[] statesMax, int numTiles,
			int numTilings) {
		numStateTilings = numTilings;
		stateTileCoding = new TileCoding(numTiles, numStateVariables, numStateTilings, statesMin, statesMax);
	}

	protected void initialiseActionTiling(int numActionVariables, double[] actionsMin, double[] actionsMax, int numTiles,
			int numTilings) {
		numActionTilings = numTilings;
		actionTileCoding = new TileCoding(numTiles, numActionVariables, numActionTilings, actionsMin, actionsMax);
	}
	
	protected TileCodeQTableInterface getQTable(){
		return qTable;
	}
	
	protected Action getNextAction(){
		return action;
	}
	
	protected TileCoding getActionTileCoding(){
		return actionTileCoding;
	}
	
	protected TileCoding getStateTileCoding(){
		return stateTileCoding;
	}

	public void agent_cleanup() {
	}

	// Learn from last reward
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

	public void agent_freeze() {

	}

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

	public String agent_message(String message) {
		if (message.equals("freeze-learning")) {
			exploringFrozen = true;
			System.out.println("Evaluation! States=" + qTable.getNumStates());
		} else if (message.equals("unfreeze-learning")) {
			exploringFrozen = false;
			System.out.println("Learning");
		}
		return null;
	}

	public Action agent_start(Observation o) {
		lastState = o;
		action = egreedy(o);
		return action;
	}

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

	protected abstract void learn(double reward, Action lastAction, Tile[] tiledLastStates, Tile[] tiledLastActions, Tile[] tiledCurStates);

	protected abstract void learnEnd(double reward, Tile[] tiledLastStates, Tile[] tiledLastActions);

	protected Action randomAction(Observation o) {
		return randomAction();
	}
	
	protected Observation manipulateState(Observation o){
		//By default do nothing
		return o;
	}

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
	 * Selects a random action with probability 1-sarsa_epsilon, and the action
	 * with the highest value otherwise. This is a quick'n'dirty implementation,
	 * it does not do tie-breaking.
	 * 
	 * @param theState
	 * @return
	 */
	private Action egreedy(Observation theState) {
		if (!exploringFrozen) {
			if (randGenerator.nextDouble() <= epsilon) {
				return randomAction(theState);
			}
		}

		Tile[] tiles = new Tile[numStateTilings];
		stateTileCoding.getTiles(tiles, new TileCodedHelicopterState(theState));
		
//		HashIntDoubleMap actionValues = HashIntDoubleMaps.newUpdatableMap();
//		Map<Integer, Action> actions = HashIntObjMaps.<Action>newUpdatableMap();
//		HashIntIntMap actionUsages = HashIntIntMaps.newUpdatableMap();
//		
//		//average over just state tiles
//		for(int i = 0; i<numStateTilings; i++){
//			ActionValue av = qTable.getMaxAction(tiles[i]);
//			if(av!= null){
//			int usages = actionUsages.getOrDefault(av.getActionTile(), 0);
//			if(usages > 0){
//				Action action = actions.get(av.getActionTile());
//				if(action != null && av.getAction() != null){
//				action.doubleArray[0] += av.getAction().getDouble(0);
//				action.doubleArray[1] += av.getAction().getDouble(1);
//				action.doubleArray[2] += av.getAction().getDouble(2);
//				action.doubleArray[3] += av.getAction().getDouble(3);
//				}
//			}else{
//				actions.put(av.getActionTile(), av.getAction());
//			}
//			actionUsages.addValue(av.getActionTile(), 1);
//			actionValues.addValue(av.getActionTile(), av.getValue());
//			}
//		}
			
		
		//average over state and action tiles
//		for(int i=0; i< numStateTilings; i++){
//			Map<Integer, ActionValue> avs = qTable.getActionsValues(tiles[i]);
//			if(avs != null){
//			avs.forEach((a, av)->{
//				int usages = actionUsages.getOrDefault((int)a, 0);
//				if(usages > 0){
//					Action action = actions.get((int) a);
//					action.doubleArray[0] += av.getAction().getDouble(0);
//					action.doubleArray[1] += av.getAction().getDouble(1);
//					action.doubleArray[2] += av.getAction().getDouble(2);
//					action.doubleArray[3] += av.getAction().getDouble(3);
//				}else{
//					actions.put((int) a, av.getAction());
//				}
//				actionUsages.addValue((int) a, 1);
//				actionValues.addValue((int)a, av.getValue());
//				
//			});
//			}
//		}
		
//		Integer maxAction = null;
//		double maxValue = Integer.MIN_VALUE;
//		for(Entry<Integer, Double> avs : actionValues.entrySet()){
//			if(maxAction == null){
//				maxAction = avs.getKey();
//			}else{
//				if(avs.getValue() > maxValue){
//					maxAction = avs.getKey();
//					maxValue = avs.getValue();
//				}
//			}
//		}
//		
//		if(maxAction != null){
//			Action theAction = actions.get(maxAction);
//			if(theAction !=null){
//			double usages = actionUsages.get((int)maxAction);
//			theAction.doubleArray[0] /= usages;
//			theAction.doubleArray[1] /= usages;
//			theAction.doubleArray[2] /= usages;
//			theAction.doubleArray[3] /= usages;
//			return theAction;
//			}
//			
//			
//		}
//		return randomAction(theState);
		
		
		
		
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
