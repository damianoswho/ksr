package pl.damianolczyk.ksr.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CommonUtils {
	public static String removeDots(String text) {
		return text.replaceAll("[.]", "");
	}

	public enum Metryka {
		EUKLIDESOWA, ULICZNA, CZEBYSZEWA, CANBERRA, MINKOWSKIEGO, MIARA_MIN_MAX, MIARA_SREDNIA_ARYTMETYCZNA_MINIMUM
	}
}
