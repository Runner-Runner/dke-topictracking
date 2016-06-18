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
	}
}
