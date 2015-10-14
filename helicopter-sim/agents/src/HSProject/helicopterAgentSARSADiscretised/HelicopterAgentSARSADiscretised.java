package HSProject.helicopterAgentSARSADiscretised;



import java.util.Random;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.QTable;

public class HelicopterAgentSARSADiscretised implements AgentInterface {
	private QTable qTable;
	
	private Action action;
	private Observation lastState;
	
	private Random randGenerator = new Random();
	private double epsilon = 0.1;
	private boolean exploringFrozen = false;

	TaskSpec TSO = null;
	
	double alpha = 0.1;
	double gamma = 1;

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

	public HelicopterAgentSARSADiscretised() {
		qTable = new QTable();
	}

	public void agent_cleanup() {
	}

	//Learn from the final reward
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
		if(message.equals("freeze-laerning")){
			exploringFrozen = true;
		}else if(message.equals("unfreeze-learning")){
			exploringFrozen = false;
		}
		return null;
	}

	public Action agent_start(Observation o) {
		discretiseState(o);
		lastState = o;
		action = egreedy(o);
		return action;
	}

	public Action agent_step(double reward, Observation o) {
		discretiseState(o);
		Action lastAction = action;
		double qValueForLastState = qTable.getQValue(lastState, lastAction);
		
		
		action = egreedy(o);
		
		double qValueForNextState = qTable.getQValue(o, action);
	
		if(!exploringFrozen){
			double newQValue = qValueForLastState + alpha * (reward + gamma * qValueForNextState - qValueForLastState);
			
			qTable.putQValue(lastState, lastAction, newQValue);
		}
		lastState = o;
		
		return action;
	}
	
	private void discretiseState(Observation o){
		//Discretise 
		// -5..5
		o.doubleArray[0] = Math.round(o.doubleArray[0]);
		o.doubleArray[1] = Math.round(o.doubleArray[1]);
		o.doubleArray[2] = Math.round(o.doubleArray[2]);
		//-20..20
		o.doubleArray[3] = Math.round(o.doubleArray[3]);
		o.doubleArray[4] = Math.round(o.doubleArray[4]);
		o.doubleArray[5] = Math.round(o.doubleArray[5]);
		//-12.566 .. 12.566
		o.doubleArray[6] = Math.round(o.doubleArray[6]);
		o.doubleArray[7] = Math.round(o.doubleArray[7]);
		o.doubleArray[8] = Math.round(o.doubleArray[8]);
		//-1 .. 1
		o.doubleArray[9] = Math.round(o.doubleArray[9] * 10) / 10.0;
		o.doubleArray[10] = Math.round(o.doubleArray[10] * 10) / 10.0;
		o.doubleArray[11] = Math.round(o.doubleArray[11] * 10) / 10.0;
		
//		// -5..5
//		o.doubleArray[0] = o.doubleArray[0] > 0 ? 5 : -5;
//		o.doubleArray[1] = o.doubleArray[0] > 0 ? 5 : -5;
//		o.doubleArray[2] = o.doubleArray[0] > 0 ? 5 : -5;
//		//-20..20
//		o.doubleArray[3] = o.doubleArray[0] > 0 ? 20 : -20;
//		o.doubleArray[4] = o.doubleArray[0] > 0 ? 20 : -20;
//		o.doubleArray[5] = o.doubleArray[0] > 0 ? 20 : -20;
//		//-12.566 .. 12.566
//		o.doubleArray[6] = o.doubleArray[0] > 0 ? 12 : -12;
//		o.doubleArray[7] = o.doubleArray[0] > 0 ? 12 : -12;
//		o.doubleArray[8] = o.doubleArray[0] > 0 ? 12 : -12;
//		//-1 .. 1
//		o.doubleArray[9] = o.doubleArray[0] > 0 ? 1 : -1;
//		o.doubleArray[10] = o.doubleArray[0] > 0 ? 1 : -1;
//		o.doubleArray[11] = o.doubleArray[0] > 0 ? 1 : -1;
		
//		System.out.println(o.doubleArray[0] + " " + 
//				o.doubleArray[1] + " " + 
//				o.doubleArray[2] + " " + 
//				o.doubleArray[3]+ " " + 
//				o.doubleArray[4]+ " " + 
//				o.doubleArray[5]+ " " + 
//				o.doubleArray[6] + " " + 
//				o.doubleArray[7] + " " + 
//				o.doubleArray[8] + " " + 
//				o.doubleArray[9]+ " " + 
//				o.doubleArray[10]+ " " + 
//				o.doubleArray[11]
//						);
	}

	private Action fixed_policy(Observation o) {
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

		Action a  =new Action(0,  4);
		a.doubleArray[0] = aileron;
		a.doubleArray[1] = elevator;
		a.doubleArray[2] = rudder;
		a.doubleArray[3] = coll;
		
		return a;
	}
	
	private Action randomAction(Observation o){
		Action a = new Action(0, 4);//fixed_policy(o);
//		a.doubleArray[0] = Math.round(((randGenerator.nextDouble() * 2) - 1) * 10) / 10.0;
//		a.doubleArray[1] = Math.round(((randGenerator.nextDouble() * 2) - 1) * 10) / 10.0;
//		a.doubleArray[2] = Math.round(((randGenerator.nextDouble() * 2) - 1) * 10) / 10.0;
//		a.doubleArray[3] = Math.round(((randGenerator.nextDouble() * 2) - 1) * 10) / 10.0;
//		a.doubleArray[0] = a.doubleArray[0] * randGenerator.nextDouble();
//		a.doubleArray[1] = a.doubleArray[1] * randGenerator.nextDouble();
//		a.doubleArray[2] = a.doubleArray[2] * randGenerator.nextDouble();
////		a.doubleArray[3] = a.doubleArray[3] * randGenerator.nextDouble();
		
//		a.doubleArray[0] = randGenerator.nextDouble() > 0.5 ? 0.5 : -0.5;
//		a.doubleArray[1] = randGenerator.nextDouble() > 0.5 ? 0.5 : -0.5;
//		a.doubleArray[2] = randGenerator.nextDouble() > 0.5 ? 0.5 : -0.5;
//		a.doubleArray[3] = randGenerator.nextDouble() > 0.5 ? 0.5 : -0.5;
		
//		for(int i = 0; i < a.doubleArray.length; i++){
//			int rand = randGenerator.nextInt(5);
//			double val = 0;
//			switch (rand) {
//			case 0:
//				val = -1;
//				break;
//			case 1:
//				val = -0.5;
//				break;
//			case 2:
//				val = 0;
//				break;
//			case 3:
//				val = 0.5;
//				break;
//			case 4:
//				val = 1;
//				break;
//			default:
//				break;
//			}
//			a.doubleArray[i] = val;
//		}
		
		for(int i = 0; i < a.doubleArray.length; i++){
			int rand = randGenerator.nextInt(3);
			double val = 0;
			switch (rand) {
			case 0:
				val = -0.1;
				break;
			case 1:
				val = 0;
				break;
			case 2:
				val = 0.1;
				break;
			default:
				break;
			}
			a.doubleArray[i] = val;
		}
		
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
               return randomAction(theState);
           }
       }
       if(qTable.size() == 0){
    	   return randomAction(theState);
       }

       /*otherwise choose the greedy action*/
       Action maxAction = qTable.getActionForMaxQValue(theState);
       if(maxAction == null){
    	   return randomAction(theState);
       }
       return maxAction;
   }

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentSARSADiscretised());
		L.run();
	}

}
