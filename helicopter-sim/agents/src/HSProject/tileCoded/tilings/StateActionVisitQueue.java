package HSProject.tileCoded.tilings;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A queue structure that stores a finite history of state-action pairs
 * 
 * @author harrison
 *
 */
public class StateActionVisitQueue {
	private LinkedHashSet<StateActionPair> queue;
	private int length;

	/**
	 * A new queue
	 * 
	 * @param length
	 *            maximum length of the queue
	 */
	public StateActionVisitQueue(int length) {
		queue = new LinkedHashSet<>();
		this.length = length;
	}

	/**
	 * Add a state-action pair to the end of the queue. If the queue is full the
	 * item and the front of the queue will be returned
	 * 
	 * @param sap
	 *            the state-action pair
	 * @return the state-action pair that was removed, if any
	 */
	public StateActionPair add(StateActionPair sap) {
		if (queue.contains(sap)) {
			queue.remove(sap);
			queue.add(sap);
		} else {
			StateActionPair removed = null;
			if (queue.size() >= length) {
				removed = queue.iterator().next();
				queue.remove(removed);
			}
			queue.add(sap);
			return removed;
		}
		return null;
	}

	/**
	 * Allows iteration though the list of state-action pairs
	 * 
	 * @param action
	 *            what to perform for each state-action pair
	 */
	public void forEach(BiConsumer<? super Tile, ? super Tile> action) {
		for (StateActionPair sap : queue) {
			Tile s = sap.getState();
			Tile a = sap.getAction();
			action.accept(s, a);
		}
	}

}
