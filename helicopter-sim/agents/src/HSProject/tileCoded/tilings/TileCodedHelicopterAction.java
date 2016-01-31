package HSProject.tileCoded.tilings;

import java.util.Arrays;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.RL_abstract_type;

/**
 * Action representation for input into tile coding algorithm. Acts as a bridge
 * between RL-Glue and YORLL.
 * 
 * @author harrison
 *
 */
public class TileCodedHelicopterAction implements marl.ext.tilecoding.TileCodingState<TileCodedHelicopterAction> {
	private Action a;

	/**
	 * A new Action
	 * 
	 * @param a
	 *            the action
	 */
	public TileCodedHelicopterAction(Action a) {
		this.a = new Action(a);
	}

	@Override
	public void set(TileCodedHelicopterAction action) {
		RL_abstract_type.RLStructCopy(action.a, this.a);
	}

	@Override
	public double getFeature(int i) {
		return a.doubleArray[i];
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TileCodedHelicopterAction) {
			TileCodedHelicopterAction tchs = (TileCodedHelicopterAction) obj;
			return Arrays.equals(this.a.doubleArray, tchs.a.doubleArray);
		}
		return false;
	}
}
