using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms.DataVisualization.Charting;

namespace TopicTrackingVisualization
{
	class Dataset
	{
		private List<string> _topics;
		private List<Series> _serieses;
		private List<List<string[]>> _documentPoints;
		private DateTime _startdate;


		public List<string> Topics { get { return _topics; } }
		public List<Series> Serieses { get { return _serieses; } }
		public List<List<string[]>> DocumentPoints { get { return _documentPoints; } }
		public DateTime Startdate { get { return _startdate; } }

		public Dataset(List<string> topics, List<Series> serieses, List<List<string[]>> documentPoints, DateTime startdate)
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
			_topics = topics;
			_serieses = serieses;
			_documentPoints = documentPoints;
			_startdate = startdate;
		}

		public static Dataset CreateFromFiles(string[] topicsFileContent, string[] dataFileContent)
		{
			List<string> topics = new List<string>();
			foreach(string s in topicsFileContent)
			{
				topics.Add(s);
			}
			List<string> dataFileLines = dataFileContent.ToList<string>();
			if (dataFileContent.Length == 0)
			{
				throw new ArgumentException("The datafile was empty!");
			}

			//System.Windows.Forms.DataVisualization.Charting.Series s = new System.Windows.Forms.DataVisualization.Charting.Series();
			//new System.Windows.Forms.DataVisualization.Charting.DataPoint(0.5, 0.7);

			DateTime startdate;
			//System.Windows.Forms.MessageBox.Show($"\"{dataFileLines[0]}\"");
			if (DateTime.TryParseExact(dataFileLines[0].Trim(), "dd.MM.yyyy", CultureInfo.InvariantCulture, DateTimeStyles.None, out startdate))
			{
				List<List<KeyValuePair<double, string[]>>> pointsDocuments = new List<List<KeyValuePair<double, string[]>>>();
				dataFileLines.RemoveAt(0);
				for (int i = 0; i < dataFileLines.Count; i++)
				{
					pointsDocuments.Add(new List<KeyValuePair<double, string[]>>());
					double normalizer = DataFilelineSum(dataFileLines[i]);
					foreach(string point in dataFileLines[i].Split(new char[] { ';' }, StringSplitOptions.RemoveEmptyEntries))
					{
						string[] pointDocs = point.Split(new char[] { ':' }, StringSplitOptions.RemoveEmptyEntries);
						//System.Windows.Forms.MessageBox.Show($"\"{point}\" {pointDocs.Length.ToString()}");
						if (pointDocs.Length != 2)
						{
							throw new ArgumentException($"Format of (y):(doc1,doc2,...) violated in {dataFileLines[i]}");
						}
						double y;
						if (Double.TryParse(pointDocs[0].Trim(), NumberStyles.Float, CultureInfo.GetCultureInfo("en-US"), out y))
						{
							pointsDocuments.Last().Add(new KeyValuePair<double, string[]>(y / normalizer, pointDocs[1].Split(',').Select(x => x.Trim()).ToArray()));
						}
						else
						{
							throw new ArgumentException($"Datapoint could not be parsed into Double in {dataFileLines[i]}");
						}
					}
				}
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
				List<List<string[]>> documentPoints = pointsDocuments.Select(x => x.Select(y => y.Value).ToList()).ToList();
				return new Dataset(topics, serieses, documentPoints, startdate);
			}
			else
			{
				throw new ArgumentException($"The date in the first line of the datafile could not be parsed; {dataFileLines[0]}");
			}
		}

		private static double DataFilelineSum(string line)
		{
			return line.Split(new char[] { ';' }, StringSplitOptions.RemoveEmptyEntries).
				Select(x => x.Split(new char[] { ':' }, StringSplitOptions.RemoveEmptyEntries)[0].Trim()).
				Sum(x => Double.Parse(x, NumberStyles.Float, CultureInfo.GetCultureInfo("en-US")));
		}
	}
}
