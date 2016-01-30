package HSProject.tileCoded.helicopterAgentTileCodedEligibilitySARSA;

import java.util.Map;

import org.rlcommunity.rlglue.codec.types.Action;

import HSProject.tileCoded.tilings.Tile;
import HSProject.tileCoded.tilings.TileCodeQTable;
import net.openhft.koloboke.collect.map.hash.HashIntDoubleMaps;
import net.openhft.koloboke.collect.map.hash.HashIntObjMaps;

public class EligibilityQTable extends TileCodeQTable {
	Map<Integer, Map<Integer, Double>> eligibilityTable;
	//Set<StateActionPair> state_actions;
	//private StateActionVisitQueue savq = new StateActionVisitQueue(100);
	//private Set<StateActionPair> qUpdated;
	
	double alpha;
	double gamma;
	double lambda;
	
	public EligibilityQTable(double alpha, double gamma, double lambda) {
		eligibilityTable = HashIntObjMaps.<Map<Integer, Double>>newUpdatableMap();
		//state_actions = HashObjSets.<StateActionPair>newUpdatableSet();
		//qUpdated = HashObjSets.<StateActionPair>newUpdatableSet();
		
		this.alpha = alpha;
		this.gamma = gamma;
		this.lambda = lambda;
	}
	
//	public void qUpdate(Tile state, Tile action, double delta, Action lastAction){
//		StateActionPair stateAction = new StateActionPair(state, action);
//		qUpdated.add(stateAction);
//		savq.add(stateAction);
//		state_actions.add(stateAction);
//		
//		double oldElig = getEligibilityValue(state.hashCode(), action.hashCode());
//		double curElig = oldElig + 1;
//		double q = getQValue(state, action);
//		
//		put(state, action, q + alpha * delta * curElig, lastAction);
//		putEligibilityValue(state.hashCode(), action.hashCode(), gamma * lambda * curElig);
//		
//	}
	
//	public void updateEligibilities(double delta){
//		state_actions.forEach((sap)->{
//			if(!qUpdated.contains(sap)){
//				int s = sap.getState().hashCode();
//				int a = sap.getAction().hashCode();
//				double curElig = getEligibilityValue(s, a);
//				
//				ActionValue av = table.get(s).get(a);
//				double curQValue = av.getValue();
//				av.setValue(curQValue + alpha * delta * curElig);
//				
//				putEligibilityValue(s, a, gamma * lambda * curElig);
//			}
//		});
//		
//		qUpdated.clear();
//		//Iterator<StateActionPair> sapIt = savq.getQueueIterator();
////		while(sapIt.hasNext()){
////			StateActionPair sa = sapIt.next();
////			int s = sa.getState().hashCode();
////			int a = sa.getAction().hashCode();
////			double curElig = getEligibilityValue(s, a);
////			
////			// Q(s,a) <- Q(s,a) + alpha delta e(s,a)
////			if(!state.equals(sa.getState()) && !action.equals(sa.getAction())){
////				//update others
////				ActionValue av = table.get(s).get(a);
////				double curQValue = av.getValue();
////				av.setValue(curQValue + alpha * delta * curElig);
////			}else{
////				//put current
////				double q = getQValue(state, action);
////				put(state, action, q + alpha * delta * curElig, lastAction);
////			}
////			putEligibilityValue(s, a, gamma * lambda * curElig);
////		}
//		
//	}
	
//	public void eligibilityUpdate(Tile state, Tile action, double delta, Action lastAction){
//		StateActionPair stateAction = new StateActionPair(state, action);
//		state_actions.add(stateAction);
//		StateActionPair removed = savq.add(stateAction);
//		
//		double oldElig = getEligibilityValue(state.hashCode(), action.hashCode());
//		putEligibilityValue(state.hashCode(), action.hashCode(), oldElig + 1);
//		
//		Iterator<StateActionPair> sapIt = savq.getQueueIterator();
//		while(sapIt.hasNext()){
//			StateActionPair sa = sapIt.next();
//			int s = sa.getState().hashCode();
//			int a = sa.getAction().hashCode();
//			double curElig = getEligibilityValue(s, a);
//			
//			// Q(s,a) <- Q(s,a) + alpha delta e(s,a)
//			if(!state.equals(sa.getState()) && !action.equals(sa.getAction())){
//				//update others
//				ActionValue av = table.get(s).get(a);
//				double curQValue = av.getValue();
//				av.setValue(curQValue + alpha * delta * curElig);
//			}else{
//				//put current
//				double q = getQValue(state, action);
//				put(state, action, q + alpha * delta * curElig, lastAction);
//			}
//			putEligibilityValue(s, a, gamma * lambda * curElig);
//		}
//		if(removed != null){
//			StateActionPair sa = removed;
//			int s = sa.getState().hashCode();
//			int a = sa.getAction().hashCode();
//			double curElig = getEligibilityValue(s, a);
//			
//			if(!state.equals(sa.getState()) && !action.equals(sa.getAction())){
//				ActionValue av = table.get(s).get(a);
//				double curQValue = av.getValue();
//				av.setValue(curQValue + alpha * delta * curElig);
//				
//				putEligibilityValue(s, a, 0);
//			}
//		}
////		state_actions.forEach((sa)->{
////			int s = sa.getState().hashCode();
////			int a = sa.getAction().hashCode();
////			double curElig = getEligibilityValue(s, a);
////			
////			// Q(s,a) <- Q(s,a) + alpha delta e(s,a)
////			if(!state.equals(sa.getState()) && !action.equals(sa.getAction())){
////				//update others
////				ActionValue av = table.get(s).get(a);
////				double curQValue = av.getValue();
////				av.setValue(curQValue + alpha * delta * curElig);
////			}else{
////				//put current
////				double q = getQValue(state, action);
////				put(state, action, q + alpha * delta * curElig, lastAction);
////			}
////			putEligibilityValue(s, a, gamma * lambda * curElig);
////		});
//		
//	}
	
	private double getEligibilityValue(Integer state, Integer action){
		Map<Integer, Double> eligActions = eligibilityTable.get(state);
		if(eligActions == null){
			return 0;
		}
		Double oldElig = eligActions.get(action);
		if(oldElig == null){
			return 0;
		}
		return oldElig;
	}
	
	private void putEligibilityValue(Integer state, Integer action, double elig){
		Map<Integer, Double> eligActions = eligibilityTable.get(state);
		if(eligActions == null){
			eligActions = HashIntDoubleMaps.newUpdatableMap();
		}
		eligActions.put(action, elig);
		eligibilityTable.put(state, eligActions);
	}
	
	public void updateEligibility(Tile state, Tile action, double eligibility){
		putEligibilityValue(state.hashCode(), action.hashCode(), eligibility);
	}
	
	public double getEligibility(Tile state, Tile action){
		return getEligibilityValue(state.hashCode(), action.hashCode());
	}
	
	@Override
	public void put(Tile state, Tile action, double value, Action actualAction) {
		//state_actions.add(new StateActionPair(state, action));
		//savq.add(new StateActionPair(state, action));
		super.put(state, action, value, actualAction);
	}
	
	
}
