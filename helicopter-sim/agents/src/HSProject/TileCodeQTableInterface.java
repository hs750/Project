package HSProject;

import org.rlcommunity.rlglue.codec.types.Action;

public interface TileCodeQTableInterface {
	
	public double getQValue(Tile state, Tile action);
	
	public double getMaxQValue(Tile state);
	
	public ActionValue getMaxAction(Tile state);
	
	
	public void put(Tile state, Tile action, double value, Action actualAction);
	
	public class ActionValue{
		private double value;
		private Action actualAction;
		public ActionValue(double value, Action actualAction) {
			this.value = value;
			if(actualAction != null){
				this.actualAction = new Action(actualAction);
			}else{
				this.actualAction = null;
			}
		}
		
		public void update(double value, Action action){
			this.value = value;
			this.actualAction = action;
		}
		
		public void setValue(double value){
			this.value = value;
		}
		
		public double getValue() {
			return value;
		}
		
		public Action getAction() {
			return actualAction;
		}
		
	}

}
