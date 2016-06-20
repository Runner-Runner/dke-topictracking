package tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Tools {

	public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> 
    sortByValue( Map<K, V> map )
	{
		LinkedHashMap<K, V> result = new LinkedHashMap<>();
	    Stream<Map.Entry<K, V>> st = map.entrySet().stream();
	
	    st.sorted( Map.Entry.comparingByValue(Comparator.reverseOrder()) )
	        .forEachOrdered( e -> result.put(e.getKey(), e.getValue()) );
	
	    return result;
	}
	
	public static double[] convertToArray(List<Float> list)
	{
		double[] doubleArray = list.stream()
			    .mapToDouble(f -> f != null ? f : Float.NaN)
			    .toArray();
		
		return doubleArray;
	}
	
	public static LinkedHashMap<Integer, Float> convertListToMap(ArrayList<Float> list)
	{
		LinkedHashMap<Integer, Float> map = new LinkedHashMap<Integer, Float>();
		
		for (int index = 0; index < list.size(); ++index)
		{
			map.put(index, list.get(index));
		}
		
		return map;
	}
	
	public static String removeLeadingZeros(String s)
	{
	      try {
	          Integer intVal = Integer.parseInt(s);
	          s = intVal.toString();
	      } catch (Exception ex) {
	          // whatever
	      }
	      return s;
	    }
}
