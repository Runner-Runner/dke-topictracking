using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ReutersCrawler
{
	/// <summary>
	/// Capsulates a Reuters article
	/// </summary>
	class CArticle
	{
		private string _url;
		private DateTime _time;
		private string _headline;
		private string _description;
		private string _contents;
		private string _tags;

		public string Url { get; set; }
		public DateTime Time { get; set; }
		public string Headline { get; set; }
		public string Description { get; set; }
		public string Contents { get; set; }
		public string Tags { get; set; }

		public CArticle(string url, DateTime time, string headline, string description, string contents, string tags)
		{
			_url = url;
			_time = time;
			_headline = headline;
			_description = description;
			_contents = contents;
			_tags = tags;
		}

		public override string ToString()
		{
			StringBuilder sb = new StringBuilder();
			sb.AppendLine(_url);
			sb.AppendLine(_time == new DateTime(0) ? "ERROR" : TimeZone.CurrentTimeZone.ToUniversalTime(_time) + "");
			sb.AppendLine(_headline == null ? "ERROR" : _headline);
			sb.AppendLine(_description == null ? "ERROR" : _description);
			sb.AppendLine(_contents == null ? "ERROR" : _contents);
			sb.AppendLine(_tags == null ? "ERROR" : _tags);
			return sb.ToString();
		}
	}
}
