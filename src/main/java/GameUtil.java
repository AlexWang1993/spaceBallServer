
public class GameUtil {
	public static String getCommaSeparatedFields(Object... args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i].toString());
			if (i < args.length - 1) {
				sb.append(";");
			}
		}
		return sb.toString();
	}
	
	public static String getRealCommaSeparatedFields(Object... args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i].toString());
			if (i < args.length - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
}
