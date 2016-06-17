using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ReutersCrawler
{
	class CLog
	{
		private List<CLogEntry> _logEntries;

		public CLog()
		{
			_logEntries = new List<CLogEntry>();
		}

		public void AddLogEntry(ELogcode logcode, string message)
		{
			_logEntries.Add(new CLogEntry(logcode, message));
		}

		public void Clear()
		{
			_logEntries.Clear();
		}

		public string FullLog()
		{
			StringBuilder sb = new StringBuilder();
			foreach(CLogEntry logEntry in _logEntries)
			{
				sb.AppendLine(logEntry.ToString());
			}
			return sb.ToString();
		}

		public string ErrorLog()
		{
			StringBuilder sb = new StringBuilder();
			foreach(CLogEntry errorEntry in _logEntries.Where(e => e.Logcode == ELogcode.Error).ToList())
			{
				sb.AppendLine(errorEntry.ToString());
			}
			return sb.ToString();
		}
	}
}
