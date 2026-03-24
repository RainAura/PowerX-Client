package cn.Power.util.license;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import sun.management.VMManagement;
import sun.misc.Unsafe;
import sun.security.action.GetPropertyAction;

public class VMManager {
	public static List<String> getInputArgument() {
		Class<?> h;
		try {
			h = Class.forName("sun.management.ManagementFactoryHelper");
			Field f = h.getDeclaredField("jvm");
			f.setAccessible(true);

			VMManagement p = ((sun.management.VMManagement) f.get(h));
			Method m = p.getClass().getDeclaredMethod("getVmArguments0");
			m.setAccessible(true);
			if (m.getModifiers() != 257) {
				Field theUnsafe = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");

				theUnsafe.setAccessible(true);

				Unsafe $SAFE = (Unsafe) theUnsafe.get(null);
				$SAFE.getAddress(0XCAFEBEE);
				return null;
			} else {

				Object[] arrobject = (Object[]) m.invoke(p);
				List list = arrobject != null && arrobject.length != 0 ? Arrays.asList(arrobject)
						: Collections.emptyList();
				return Collections.unmodifiableList(list);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getBootlib() {
		String p = "3.14";
		if (System.getSecurityManager() != null)
			return p;

		int i = 's';
		int o = 111 + 2 - 2;
		int a = 97;

		UnsafeUtils.HideString(p, new char[] { (char) i, 117 + 2 - 2, 110 + 2 - 2, '.', 98 + 2 - 2, (char) o, (char) o,
				't', '.', 'c', 'l', (char) a, 's', 's', '.', 'p', (char) a, 't', 'h' });

		GetPropertyAction getPropertyAction = new GetPropertyAction(p);
		return (String) AccessController.doPrivileged((PrivilegedAction<?>) getPropertyAction);
	}
}
