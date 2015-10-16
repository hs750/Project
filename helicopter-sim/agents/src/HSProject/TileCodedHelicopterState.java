package HSProject;

import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.RL_abstract_type;

public class TileCodedHelicopterState implements marl.ext.tilecoding.TileCodingState<TileCodedHelicopterState>{
	private Observation o;
	
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
		if(obj instanceof TileCodedHelicopterState){
			TileCodedHelicopterState tchs = (TileCodedHelicopterState) obj;
			return this.o.doubleArray == tchs.o.doubleArray;
		}
		return false;
	}
}
