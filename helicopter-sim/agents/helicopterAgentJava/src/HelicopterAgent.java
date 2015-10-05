
/* Random Agent that works in all domains
* Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */

import java.util.Random;

import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;

public class HelicopterAgent implements AgentInterface {
	private Action action;
	private int numInts = 1;
	private int numDoubles = 0;
	private Random random = new Random();

	TaskSpec TSO = null;
	
	// Indices into observation_t.doubleArray...
	private static int u_err = 0,   // forward velocity
	  v_err = 1,   // sideways velocity
	  w_err = 2,   // downward velocity
	  x_err = 3,   // forward error
	  y_err = 4,   // sideways error
	  z_err = 5,   // downward error
	  p_err = 6,   // angular rate around forward axis
	  q_err = 7,   // angular rate around sideways (to the right) axis
	  r_err = 8,   // angular rate around vertical (downward) axis
	  qx_err = 9,  // <-- quaternion entries, x,y,z,w   q = [ sin(theta/2) * axis; cos(theta/2)],
	  qy_err = 10, // where axis = axis of rotation; theta is amount of rotation around that axis
	  qz_err = 11; // [recall: any rotation can be represented by a single rotation around some axis]

	public HelicopterAgent(){
        }

	public void agent_cleanup() {
	}

	public void agent_end(double arg0) {

	}

	public void agent_freeze() {

	}

	public void agent_init(String taskSpec) {
		TSO = new TaskSpec(taskSpec);
		
		
		if (TSO.getVersionString().equals("Mario-v1")) {
			TaskSpecVRLGLUE3 hardCodedTaskSpec = new TaskSpecVRLGLUE3();
			hardCodedTaskSpec.setEpisodic();
			hardCodedTaskSpec.setDiscountFactor(1.0d);
			// Run
			hardCodedTaskSpec.addDiscreteAction(new IntRange(-1, 1));
			// Jump
			hardCodedTaskSpec.addDiscreteAction(new IntRange(0, 1));
			// Speed
			hardCodedTaskSpec.addDiscreteAction(new IntRange(0, 1));
			TSO = new TaskSpec(hardCodedTaskSpec);
		}
		action = new Action(TSO.getNumDiscreteActionDims(), TSO.getNumContinuousActionDims());
	}

	public String agent_message(String arg0) {
		return null;
	}

	public Action agent_start(Observation o) {
		agent_policy(o, action);
		return action;
	}

	public Action agent_step(double arg0, Observation o) {
		agent_policy(o, action);
		return action;
	}

	private void randomify(Action action) {
		for (int i = 0; i < TSO.getNumDiscreteActionDims(); i++) {
			IntRange thisActionRange = TSO.getDiscreteActionRange(i);
			action.intArray[i] = random.nextInt(thisActionRange.getRangeSize()) + thisActionRange.getMin();
		}
		for (int i = 0; i < TSO.getNumContinuousActionDims(); i++) {
			DoubleRange thisActionRange = TSO.getContinuousActionRange(i);
			action.doubleArray[i] = random.nextDouble() * (thisActionRange.getRangeSize()) + thisActionRange.getMin();
		}
	}
	
	private void agent_policy(Observation o, Action a){
		 double weights[] = {0.0196, 0.7475, 0.0367, 0.0185, 0.7904, 0.0322, 0.1969, 0.0513, 0.1348, 0.02, 0, 0.23};
		
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
	    
	  //x/y/z_error = body(x - x_target)
	    //q_error = inverse(Q_target) * Q, where Q is the orientation of the helicopter
	    //roll/pitch/yaw_error = scaled_axis(q_error)

	    // collective control
	    double coll = weights[z_w] * o.doubleArray[z_err] +
	        weights[w_w] * o.doubleArray[w_err] +
	        weights[coll_trim];

	    // forward-backward control
	    double elevator =  -weights[x_w] * o.doubleArray[x_err] +
	        -weights[u_w] * o.doubleArray[u_err] +
	        weights[pitch_w] * o.doubleArray[qy_err] +
	        weights[el_trim];

	    // left-right control
	    double aileron =
	        -weights[y_w] * o.doubleArray[y_err] +
	        -weights[v_w] * o.doubleArray[v_err] +
	        -weights[roll_w] * o.doubleArray[qx_err] +
	        weights[ail_trim];

	    double rudder = -weights[yaw_w] * o.doubleArray[qz_err];

	    a.doubleArray[0] = aileron;
	    a.doubleArray[1] = elevator;
	    a.doubleArray[2] = rudder;
	    a.doubleArray[3] = coll;
	}

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgent());
		L.run();
	}

}
