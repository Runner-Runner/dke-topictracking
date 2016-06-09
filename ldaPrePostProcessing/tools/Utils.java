package tools;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Utils {

	public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> 
    sortByValue( Map<K, V> map )
	{
		LinkedHashMap<K, V> result = new LinkedHashMap<>();
	    Stream<Map.Entry<K, V>> st = map.entrySet().stream();
	
	    st.sorted( Map.Entry.comparingByValue(Comparator.reverseOrder()) )
	        .forEachOrdered( e -> result.put(e.getKey(), e.getValue()) );
	
	    return result;
	}
}
