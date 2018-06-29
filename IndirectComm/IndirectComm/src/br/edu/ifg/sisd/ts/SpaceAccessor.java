package br.edu.ifg.sisd.ts;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.lookup.entry.Name;
import net.jini.space.JavaSpace;

public class SpaceAccessor {
	
	public static final long MAX_LOOKUP_WAIT = 2000L;

	public static JavaSpace findSpace(String jiniURL, String spaceName) {
		LookupLocator locator = null;
		ServiceRegistrar registrar = null;
		JavaSpace space = null;

		try {
			// Get lookup service locator at "jini://hostname"
			// use default port and register of the locator
			locator = new LookupLocator(jiniURL);
			registrar = locator.getRegistrar();
			// Space name provided in property file
			ServiceTemplate template;
			if (spaceName != null) {
				// Specify the service requirement, array (length 1) of
				// Entry interfaces (such as the Name interface)
				Entry[] attr = new Entry[1];
				attr[0] = new Name(spaceName);
				template = new ServiceTemplate(null, null, attr);
			} else {
				// Specify the service requirement, array (length 1) of
				// instances of Class
				Class<?>[] types = new Class[] { JavaSpace.class };
				template = new ServiceTemplate(null, types, null);
			}
			Object obj = registrar.lookup(template);
			// Get space, 10 attempts!
			for (int i = 0; i < 10; i++) {
				if (obj instanceof JavaSpace) {
					space = (JavaSpace) obj;
					break;
				}
				System.err.println("BasicService. JavaSpace not " + "available. Trying again...");
				Thread.sleep(MAX_LOOKUP_WAIT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return space;
	}
}