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

		public List<string> Topics { get { return _topics; } }
		public List<Series> Serieses { get { return _serieses; } }

		public Dataset(List<string> topics, List<Series> serieses)
		{
			//incoherent data
			if (topics.Count != serieses.Count)
			{
				throw new ArgumentException("The number of topics does not match the number of serieses");
			}
			_topics = topics;
			_serieses = serieses;
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
							pointsDocuments.Last().Add(new KeyValuePair<double, string[]>(y, pointDocs[1].Split(',').Select(x => x.Trim()).ToArray()));
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
					//System.Windows.Forms.MessageBox.Show($"{startdate.ToShortDateString()}, {pointsDocuments[0][i].Key}");
					serieses[i].Points.AddXY(startdate.ToShortDateString(), pointsDocuments[0][i].Key);
				}
				for (int i = 1; i < pointsDocuments.Count; i++)
				{
					for (int j = 0; j < pointsDocuments[i].Count; j++)
					{
						serieses[j].Points.AddXY(startdate.AddDays(i).ToShortDateString(), pointsDocuments[i][j].Key);
					}
				}
				return new Dataset(topics, serieses);
			}
			else
			{
				throw new ArgumentException($"The date in the first line of the datafile could not be parsed; {dataFileLines[0]}");
			}
		}
	}
}
