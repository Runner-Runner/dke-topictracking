package preProcessing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NLPUtils {

	public static boolean containsSpecialChars(final String word)
	{
		if (word.length() < 2)
		{
			return true;
		}
		
		if (word.toLowerCase().contains("-lrb-") || word.toLowerCase().contains("-rrb-"))
		{
			return true;
		}
		
		Pattern pattern = Pattern.compile("[0-9$&+_,.:;=?@#/|'<>^*()%!\\{}\\[\\]]");
		Matcher matcher = pattern.matcher(word);
		if (matcher.find())
		{
			return true;
		}
		
		String splitter = "-";
		if (word.contains(splitter)) 
		{
			String[] parts = word.split(Pattern.quote(splitter));
			boolean number = true;
			
			for (String part : parts)
			{
				try  
				{  
					Double.parseDouble(part);  
				}  
				catch(NumberFormatException e)  
				{  
					number = false;
					break;
				}  
			}
			
			if (number)
			{
				return true;
			}
			
		}
		
		return false;
	}
	
	public static int levenshteinDistance(String a, String b)
	{
		a = a.toLowerCase();
		b = b.toLowerCase();
		// i == 0
		int [] costs = new int [b.length() + 1];
		for (int j = 0; j < costs.length; j++)
		{
			costs[j] = j;
		}
		for (int i = 1; i <= a.length(); i++) 
		{
			// j == 0; nw = lev(i - 1, j)
			costs[0] = i;
			int nw = i - 1;
			for (int j = 1; j <= b.length(); j++) 
			{
				int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
				nw = costs[j];
				costs[j] = cj;
			}
		}
		return costs[b.length()];
    }
}
