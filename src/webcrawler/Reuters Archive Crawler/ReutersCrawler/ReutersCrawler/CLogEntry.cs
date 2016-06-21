using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ReutersCrawler
{
	/// <summary>
	/// Statuscode of a CLogEntry
	/// </summary>
	public enum ELogcode { Status, Output, Warning, Error };

	/// <summary>
	/// Single log entry for CLog
	/// </summary>
	class CLogEntry
	{
		private DateTime _time;
		private ELogcode _logcode;
		private string _message;

		public DateTime Time { get { return _time; } }
		public ELogcode Logcode { get { return _logcode; } }
		public string Message { get { return _message; } }

		public CLogEntry(ELogcode logcode, string message)
		{
			_time = DateTime.Now;
			_logcode = logcode;
			_message = message;
		}

		public override string ToString()
		{
			return _time.ToString("s") + " - " + _logcode.ToString() + " - " + _message;
		}
	}
}
