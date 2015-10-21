package HSProject.helicopterAgentQ;



import java.util.Random;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.HelicopterQTable;

public class HelicopterAgentQ implements AgentInterface {
	private HelicopterQTable qTable;
	
	private Action action;
	private Observation lastState;
	
	protected Random randGenerator = new Random();
	private double epsilon = 0.1;
	private boolean exploringFrozen = false;

	TaskSpec TSO = null;
	
	double alpha = 0.1;
	double gamma = 1;

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

	public HelicopterAgentQ() {
		qTable = new HelicopterQTable();
	}

	public void agent_cleanup() {
	}

	//Learn from last reward
	public void agent_end(double reward) {
		if(!exploringFrozen){
			double qValueForLastState = qTable.getQValue(lastState, action);
			
			double newQValue = qValueForLastState + alpha * (reward - qValueForLastState);
			
			qTable.putQValue(lastState, action, newQValue);
		}
	}

	public void agent_freeze() {

	}

	public void agent_init(String taskSpec) {
		System.out.println(taskSpec);
		TSO = new TaskSpec(taskSpec);
		action = new Action(TSO.getNumDiscreteActionDims(), TSO.getNumContinuousActionDims());
		
		lastState = new Observation(0, TSO.getNumContinuousActionDims());
	}

	public String agent_message(String message) {
		if(message.equals("freeze-learning")){
			exploringFrozen = true;
		}else if(message.equals("unfreeze-learning")){
			exploringFrozen = false;
		}
		return null;
	}

	public Action agent_start(Observation o) {
		lastState = o;
		action = egreedy(o);
		return action;
	}

	public Action agent_step(double reward, Observation o) {
		Action lastAction = action;
		double qValueForLastState = qTable.getQValue(lastState, lastAction);
		double maxQValueForNextState = qTable.getMaxQValue(o);
		
		action = egreedy(o);
		
		if(!exploringFrozen){
			double newQValue = qValueForLastState + alpha * (reward + gamma * maxQValueForNextState - qValueForLastState);
			
			qTable.putQValue(lastState, lastAction, newQValue);
		}
		lastState = o;
		//System.out.println(qTable.size());
		return action;
	}
	
	protected Action randomAction(){
		Action a = new Action(0, 4);
		a.doubleArray[0] = (randGenerator.nextDouble() * 2) - 1;
		a.doubleArray[1] = (randGenerator.nextDouble() * 2) - 1;
		a.doubleArray[2] = (randGenerator.nextDouble() * 2) - 1;
		a.doubleArray[3] = (randGenerator.nextDouble() * 2) - 1;
		return a;
	}
	
	/**
    *
    * Selects a random action with probability 1-sarsa_epsilon,
    * and the action with the highest value otherwise.  This is a
    * quick'n'dirty implementation, it does not do tie-breaking.

    * @param theState
    * @return
    */
   private Action egreedy(Observation theState) {
       if (!exploringFrozen) {
           if (randGenerator.nextDouble() <= epsilon) {
               return randomAction();
           }
       }
       if(qTable.size() == 0){
    	   return randomAction();
       }

       /*otherwise choose the greedy action*/
       Action maxAction = qTable.getActionForMaxQValue(theState);
       if(maxAction == null){
    	   return randomAction();
       }
       return maxAction;
   }

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentQ());
		L.run();
	}

}
