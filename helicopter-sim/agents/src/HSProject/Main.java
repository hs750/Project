package HSProject;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

public class Main {

	public static void main(String[] args)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (args.length > 0) {
			String className = args[0];
			Class<?> clazz = Class.forName(className);
			if (AgentInterface.class.isAssignableFrom(clazz)) {
				AgentInterface ai = (AgentInterface) clazz.newInstance();

				AgentLoader L = new AgentLoader(ai);
				L.run();
			} else {
				System.err.println(
						clazz.getCanonicalName() + " is not assignable from " + AgentInterface.class.getName());
			}
		} else {
			System.err.println("Must specify agent class name as first argument");
		}

	}

}
