using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms.DataVisualization.Charting;

namespace TopicTrackingVisualization
{
	/// <summary>
	/// Dataset with Charting serieses and document data
	/// </summary>
	class CDataset
	{
		private List<string> _topics;					// all the topics
		private List<Series> _serieses;					// serieses, can be displayed by Chart
		private List<List<string[]>> _documentPoints;	// documents belonging to each point
		private DateTime _startdate;					// startdate (for labelling)
		private int _interval;							// interval in days (for labelling)

		public List<string> Topics { get { return _topics; } }
		public List<Series> Serieses { get { return _serieses; } }
		public List<List<string[]>> DocumentPoints { get { return _documentPoints; } }
		public DateTime Startdate { get { return _startdate; } }
		public int Interval { get { return _interval; } }


		public CDataset(List<string> topics, List<Series> serieses, List<List<string[]>> documentPoints, DateTime startdate, int interval)
		{
			if (topics == null ||serieses ==null || documentPoints == null || startdate == null)
			{
				throw new ArgumentException("None of the parameters may be null");
			}
			//incoherent data
			if (topics.Count != serieses.Count || topics.Count != documentPoints[0].Count)
			{
				throw new ArgumentException("The number of topics does not match the number of serieses");
			}
			if (interval <= 0)
			{
				throw new ArgumentException("Interval must be larger than 0");
			}
			_topics = topics;
			_serieses = serieses;
			_documentPoints = documentPoints;
			_startdate = startdate;
			_interval = interval;
		}

		/// <summary>
		/// Create a new CDataset from two files
		/// </summary>
		/// <param name="topicsFileContent"></param>
		/// <param name="dataFileContent"></param>
		/// <returns></returns>
		public static CDataset CreateFromFiles(string[] topicsFileContent, string[] dataFileContent)
		{
			// derive topics directly from topics-file
			List<string> topics = new List<string>();
			foreach(string s in topicsFileContent)
			{
				topics.Add(s);
			}

			// convert data file to list
			if (dataFileContent.Length == 0)
			{
				throw new ArgumentException("The datafile was empty!");
			}

			DateTime startdate;
			int interval;

			// parse first line of file, contains date and optional interval
			string[] dateInterval = dataFileContent[0].Replace(" ", "").Split(';').ToArray();
			try
			{
				startdate = DateTime.ParseExact(dateInterval[0], "dd.MM.yyyy", CultureInfo.InvariantCulture, DateTimeStyles.None);
			}
			catch(Exception e)
			{
				throw new ArgumentException($"Error in line 1: {e.Message}");
			}
			// if interval not present, use default value, otherwise throw exception
			try
			{
				interval = Int32.Parse(dateInterval[1]);
			}
			catch(IndexOutOfRangeException)
			{
				interval = 1;
			}
			catch(Exception e)
			{
				throw new ArgumentException($"Error in line 1: {e.Message}");
			}

			// contains one entry per day, each sub-entry is topicvalue => documents
			List<List<KeyValuePair<double, string[]>>> pointsDocuments = new List<List<KeyValuePair<double, string[]>>>();

			// iterate over lines with data
			for (int i = 1; i < dataFileContent.Length; i++)
			{
				// create temporary day-entry that contains the absolute topicvalue
				List<KeyValuePair<double, string[]>> dayValueDocuments = new List<KeyValuePair<double, string[]>>();
				// we want to normalize the topicvalue, so we will increment this
				double normalizer = 0.0;

				string[] dayTopics = dataFileContent[i].Replace(" ", "").Split(new char[] { ';' }, StringSplitOptions.RemoveEmptyEntries);
				// iterate over the topicvalue => documents per day
				for (int j = 0; j < dayTopics.Length; j++)
				{
					string[] valueDocuments = dayTopics[j].Split(':');
					if (valueDocuments.Length != 2)
					{
						throw new ArgumentException($"Error in line {i + 1}: Split at ':' does not result in array of length 2 in {dayTopics}");
					}
					double value;
					try
					{
						value = Double.Parse(valueDocuments[0], NumberStyles.Float, CultureInfo.GetCultureInfo("en-US"));
					}
					catch (Exception)
					{
						throw new ArgumentException($"Error in line {i + 1}: Parse into double failed with {valueDocuments[0]}");
					}
					string[] documents = valueDocuments[1].Split(',');
					// store values in temporary list
					dayValueDocuments.Add(new KeyValuePair<double, string[]>(value, documents));

					// increment normalizer
					normalizer += value;
				}
				// add all values to final list, divide topicvalues by the normalizer, keep documents-array as is
				pointsDocuments.Add(new List<KeyValuePair<double, string[]>>());
				for (int j = 0; j < dayValueDocuments.Count; j++)
				{
					pointsDocuments.Last().Add(new KeyValuePair<double, string[]>(dayValueDocuments[j].Key / normalizer, dayValueDocuments[j].Value));
				}
			}
			// create serieses out of the list
			List<Series> serieses = new List<Series>();
			for (int i = 0; i < pointsDocuments[0].Count; i++)
			{
				serieses.Add(new Series()
				{
					Name = topics[i],
					ChartType = SeriesChartType.StackedArea100
				});
				serieses[i].Points.AddXY(0, pointsDocuments[0][i].Key);
			}
			for (int i = 1; i < pointsDocuments.Count; i++)
			{
				for (int j = 0; j < pointsDocuments[i].Count; j++)
				{
					serieses[j].Points.AddXY(i, pointsDocuments[i][j].Key);
				}
			}
			// assign to each point a number of documents
			List<List<string[]>> documentPoints = pointsDocuments.Select(day => day.Select(topicDocs => topicDocs.Value).ToList()).ToList();
			// create CDataset-instance and return that
			return new CDataset(topics, serieses, documentPoints, startdate, interval);
		}
	}
}
