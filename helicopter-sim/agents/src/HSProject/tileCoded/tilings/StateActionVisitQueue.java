package HSProject.tileCoded.tilings;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class StateActionVisitQueue {
	private LinkedHashSet<StateActionPair> queue;
	private int length;
	
	public StateActionVisitQueue(int length) {
		queue = new LinkedHashSet<>();
		this.length = length;
	}
	
	public StateActionPair add(StateActionPair sap){
		if(queue.contains(sap)){
			queue.remove(sap);
			queue.add(sap);
		}else{
			StateActionPair removed = null;
			if(queue.size() >= length){
				removed = queue.iterator().next();
				queue.remove(removed);
			}
			queue.add(sap);
			return removed;
		}
		return null;
	}
	
	public void forEach(Consumer<? super StateActionPair> action){
		queue.forEach(action);
	}
	
	public Collection<StateActionPair> getList(){
		return queue;
	}
	
	public void forEach(BiConsumer<? super Tile, ? super Tile> action){
		for(StateActionPair sap : queue){
			Tile s = sap.getState();
			Tile a = sap.getAction();
			action.accept(s, a);
		}
	}
	
}
