package HSProject.tileCoded.tilings;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.RL_abstract_type;

public class TileCodedHelicopterAction implements marl.ext.tilecoding.TileCodingState<TileCodedHelicopterAction>{
	private Action a;
	
	public TileCodedHelicopterAction(Action o) {
		this.a = new Action(o);
	}
	
	public void set(TileCodedHelicopterAction state) {
		RL_abstract_type.RLStructCopy(state.a, this.a);
	}

	public double getFeature(int i) {
		return a.doubleArray[i];
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TileCodedHelicopterAction){
			TileCodedHelicopterAction tchs = (TileCodedHelicopterAction) obj;
			return this.a.doubleArray == tchs.a.doubleArray;
		}
		return false;
	}
}
