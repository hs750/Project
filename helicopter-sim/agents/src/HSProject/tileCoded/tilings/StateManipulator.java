package HSProject.tileCoded.tilings;

import org.rlcommunity.rlglue.codec.types.Observation;

public interface StateManipulator {
	public Observation manipulateState(Observation o);
}
