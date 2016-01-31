package HSProject.tileCoded.tilings;

import java.util.Arrays;

import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.RL_abstract_type;

/**
 * State representation for input into tile coding algorithm. Acts as a bridge
 * between RL-Glue and YORLL.
 * 
 * @author harrison
 *
 */
public class TileCodedHelicopterState implements marl.ext.tilecoding.TileCodingState<TileCodedHelicopterState> {
	private Observation o;

	/**
	 * New State
	 * 
	 * @param o
	 *            the observation the state is based on
	 */
	public TileCodedHelicopterState(Observation o) {
		this.o = new Observation(o);
	}

	@Override
	public void set(TileCodedHelicopterState state) {
		RL_abstract_type.RLStructCopy(state.o, this.o);
	}

	@Override
	public double getFeature(int i) {
		return o.doubleArray[i];
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TileCodedHelicopterState) {
			TileCodedHelicopterState tchs = (TileCodedHelicopterState) obj;
			return Arrays.equals(this.o.doubleArray, tchs.o.doubleArray);
		}
		return false;
	}
}
