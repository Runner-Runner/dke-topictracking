using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ExtensionMethods
{
	public static class MyExtensions
	{
		public static string ToS(this string[] sa)
		{
			StringBuilder sb = new StringBuilder();
			foreach (string s in sa)
			{
				sb.AppendLine(s);
			}
			return sb.ToString();
		}

		public static string ToS(this int[] ia)
		{
			StringBuilder sb = new StringBuilder();
			foreach(int i in ia)
			{
				sb.AppendLine(i.ToString());
			}
			return sb.ToString();
		}

		public static int FindIndex<T>(this IEnumerable<T> items, Func<T, bool> predicate)
		{
			if (items == null) throw new ArgumentNullException("items");
			if (predicate == null) throw new ArgumentNullException("predicate");

			int retVal = 0;
			foreach (var item in items)
			{
				if (predicate(item)) return retVal;
				retVal++;
			}
			return -1;
		}
	}
}
