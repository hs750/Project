package HSProject.helicopterAgentQDiscretised;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

import HSProject.helicopterAgentQ.HelicopterAgentQ;

/**
 * {@link HelicopterAgentQ} extended to manually discretise the states and
 * actions.
 * 
 * @author harrison
 *
 */
public class HelicopterAgentQDiscretised extends HelicopterAgentQ {

	@Override
	public Action agent_start(Observation o) {
		discretiseState(o);
		return super.agent_start(o);
	}

	@Override
	public Action agent_step(double reward, Observation o) {
		discretiseState(o);
		return super.agent_step(reward, o);
	}

	private void discretiseState(Observation o) {
		// Discretise
		// // -5..5
		// o.doubleArray[0] = Math.round(o.doubleArray[0]);
		// o.doubleArray[1] = Math.round(o.doubleArray[1]);
		// o.doubleArray[2] = Math.round(o.doubleArray[2]);
		// //-20..20
		// o.doubleArray[3] = Math.round(o.doubleArray[3]);
		// o.doubleArray[4] = Math.round(o.doubleArray[4]);
		// o.doubleArray[5] = Math.round(o.doubleArray[5]);
		// //-12.566 .. 12.566
		// o.doubleArray[6] = Math.round(o.doubleArray[6]);
		// o.doubleArray[7] = Math.round(o.doubleArray[7]);
		// o.doubleArray[8] = Math.round(o.doubleArray[8]);
		// //-1 .. 1
		// o.doubleArray[9] = Math.round(o.doubleArray[9] * 10) / 10.0;
		// o.doubleArray[10] = Math.round(o.doubleArray[10] * 10) / 10.0;
		// o.doubleArray[11] = Math.round(o.doubleArray[11] * 10) / 10.0;

		// -5..5
		o.doubleArray[0] = o.doubleArray[0] > 0 ? 5 : -5;
		o.doubleArray[1] = o.doubleArray[0] > 0 ? 5 : -5;
		o.doubleArray[2] = o.doubleArray[0] > 0 ? 5 : -5;
		// -20..20
		o.doubleArray[3] = o.doubleArray[0] > 0 ? 20 : -20;
		o.doubleArray[4] = o.doubleArray[0] > 0 ? 20 : -20;
		o.doubleArray[5] = o.doubleArray[0] > 0 ? 20 : -20;
		// -12.566 .. 12.566
		o.doubleArray[6] = o.doubleArray[0] > 0 ? 12 : -12;
		o.doubleArray[7] = o.doubleArray[0] > 0 ? 12 : -12;
		o.doubleArray[8] = o.doubleArray[0] > 0 ? 12 : -12;
		// -1 .. 1
		o.doubleArray[9] = o.doubleArray[0] > 0 ? 1 : -1;
		o.doubleArray[10] = o.doubleArray[0] > 0 ? 1 : -1;
		o.doubleArray[11] = o.doubleArray[0] > 0 ? 1 : -1;

		// System.out.println(o.doubleArray[0] + " " +
		// o.doubleArray[1] + " " +
		// o.doubleArray[2] + " " +
		// o.doubleArray[3]+ " " +
		// o.doubleArray[4]+ " " +
		// o.doubleArray[5]+ " " +
		// o.doubleArray[6] + " " +
		// o.doubleArray[7] + " " +
		// o.doubleArray[8] + " " +
		// o.doubleArray[9]+ " " +
		// o.doubleArray[10]+ " " +
		// o.doubleArray[11]
		// );
	}

	@Override
	protected Action randomAction() {
		Action a = new Action(0, 4);// fixed_policy(o);
		// a.doubleArray[0] = Math.round(((randGenerator.nextDouble() * 2) - 1)
		// * 10) / 10.0;
		// a.doubleArray[1] = Math.round(((randGenerator.nextDouble() * 2) - 1)
		// * 10) / 10.0;
		// a.doubleArray[2] = Math.round(((randGenerator.nextDouble() * 2) - 1)
		// * 10) / 10.0;
		// a.doubleArray[3] = Math.round(((randGenerator.nextDouble() * 2) - 1)
		// * 10) / 10.0;
		// a.doubleArray[0] = a.doubleArray[0] * randGenerator.nextDouble();
		// a.doubleArray[1] = a.doubleArray[1] * randGenerator.nextDouble();
		// a.doubleArray[2] = a.doubleArray[2] * randGenerator.nextDouble();
		//// a.doubleArray[3] = a.doubleArray[3] * randGenerator.nextDouble();

		// a.doubleArray[0] = randGenerator.nextDouble() > 0.5 ? 0.5 : -0.5;
		// a.doubleArray[1] = randGenerator.nextDouble() > 0.5 ? 0.5 : -0.5;
		// a.doubleArray[2] = randGenerator.nextDouble() > 0.5 ? 0.5 : -0.5;
		// a.doubleArray[3] = randGenerator.nextDouble() > 0.5 ? 0.5 : -0.5;

		// for(int i = 0; i < a.doubleArray.length; i++){
		// int rand = randGenerator.nextInt(5);
		// double val = 0;
		// switch (rand) {
		// case 0:
		// val = -1;
		// break;
		// case 1:
		// val = -0.5;
		// break;
		// case 2:
		// val = 0;
		// break;
		// case 3:
		// val = 0.5;
		// break;
		// case 4:
		// val = 1;
		// break;
		// default:
		// break;
		// }
		// a.doubleArray[i] = val;
		// }

		for (int i = 0; i < a.doubleArray.length; i++) {
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

	public static void main(String[] args) {
		AgentLoader L = new AgentLoader(new HelicopterAgentQDiscretised());
		L.run();
	}

}
