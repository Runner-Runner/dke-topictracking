using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ExtensionMethods;
using System.Windows.Forms;
using System.Windows.Forms.DataVisualization.Charting;

namespace TopicTrackingVisualization
{
	public partial class MainForm : Form
	{
		private CDataset _dataset;
		private string _basefolder;
		private TopicsOverviewForm _tof;

		private int _min;

		public string Basefolder { get { return _basefolder; } }

		public void DataForDay(int index)
		{
			//_dataset.Serieses[index];
		}

		public MainForm()
		{
			InitializeComponent();
		}

		private void Main_Resize(object sender, EventArgs e)
		{
			topicOverviewChart.Width = this.Width - 40;
			topicOverviewChart.Height = this.Height - 62;
		}

		private void topicOverviewChart_AxisViewChanged(object sender, ViewEventArgs e)
		{
			setAxisXInterval();
		}

		private void setAxisXInterval()
		{
			int min = (int)(Double.IsNaN(topicOverviewChart.ChartAreas[0].AxisX.ScaleView.ViewMinimum) ? 0 : topicOverviewChart.ChartAreas[0].AxisX.ScaleView.ViewMinimum);
			int max = (int)(Double.IsNaN(topicOverviewChart.ChartAreas[0].AxisX.ScaleView.ViewMaximum) ? 0 : topicOverviewChart.ChartAreas[0].AxisX.ScaleView.ViewMaximum);
			int selected = (int)Math.Abs(max - min);
			if (selected == 0)
			{
				selected = topicOverviewChart.Series[0].Points.Count;
			}
			//MessageBox.Show($"{topicOverviewChart.ChartAreas[0].AxisX.ScaleView.ViewMinimum} - {topicOverviewChart.ChartAreas[0].AxisX.ScaleView.ViewMaximum}");
			topicOverviewChart.ChartAreas[0].AxisX.Interval = 1 + (selected / 50);
			_min = min;
		}

		private void topicOverviewChart_Paint(object sender, PaintEventArgs e)
		{
			//Pen p = new Pen(new SolidBrush(Color.FromArgb(255, 0, 0, 0)));
			//var series = 
			//MessageBox.Show("NOW!");
		}

		private void openToolStripMenuItem_Click(object sender, EventArgs e)
		{
			if(_tof != null && !_tof.IsDisposed)
			{
				_tof.Dispose();
			}
			OpenFileDialog ofd = new OpenFileDialog()
				{
					CheckFileExists = true,
					Filter = "Topic Data Files (*.dat)|*.dat",
					Title = "Choose a file with topic data"
				};
			if (ofd.ShowDialog() == DialogResult.OK)
			{
				OpenFileDialog ofd2 = new OpenFileDialog()
					{
						CheckFileExists = true,
						Filter = "Data Point Files (*.dat)|*.dat",
						Title = "Choose a file with data points"
					};
				if (ofd2.ShowDialog() == DialogResult.OK)
				{
					try
					{
						string[] topicsFile = File.ReadAllLines(ofd.FileName);
						string[] documentsFile = File.ReadAllLines(ofd2.FileName);
						if (documentsFile.Length > 100)
						{
							MessageBox.Show("You have selected a large file, so loading will probably take some time");
						}
						_dataset = CDataset.CreateFromFiles(topicsFile, documentsFile);
					}
					catch(Exception exc)
					{
						MessageBox.Show(exc.Message);
						return;
					}
					topicOverviewChart.Series.Clear();
					for (int i = 0; i < _dataset.Serieses.Count; i++)
					{
						if (!topicOverviewChart.Series.Any(x => x.Name == _dataset.Serieses[i].Name))
						{
							topicOverviewChart.Series.Add(_dataset.Serieses[i]);
						}
					}
					foreach (var s in topicOverviewChart.Series)
					{
						foreach (var pt in s.Points)
						{
							pt.ToolTip = $"{s.Name}:   {Math.Round(pt.YValues[0], 2) * 100}%";
						}
					}

					topicOverviewChart.ChartAreas[0].CursorX.IsUserSelectionEnabled = true;

					topicOverviewChart.ChartAreas[0].AxisX.Maximum = topicOverviewChart.Series[0].Points.Count - 1;
					topicOverviewChart.ChartAreas[0].AxisX.Minimum = 0;
					topicOverviewChart.ChartAreas[0].AxisX.MajorTickMark.Interval = 1;

					setAxisXInterval();

					topicOverviewChart.ApplyPaletteColors();
					foreach (Series s in topicOverviewChart.Series) s.Color = Color.FromArgb(192, s.Color);
				}
			}
		}

		private void sourceDocumentsToolStripMenuItem_Click(object sender, EventArgs e)
		{
			BasefolderSelectionForm bsf = new BasefolderSelectionForm();
			if (bsf.ShowDialog() == DialogResult.OK)
			{
				_basefolder = bsf.Basefolder;
				sourceDocumentsToolStripMenuItem.Text = "Define Basefolder"  + (_basefolder != null ? $"\n{_basefolder}" : "");
			}
		}

		private void topicOverviewChart_MouseClick(object sender, MouseEventArgs e)
		{
			if (e.Button == MouseButtons.Right)
			{
				HitTestResult r = topicOverviewChart.HitTest(e.X, e.Y);
				int x = r.PointIndex;
				int sx = topicOverviewChart.Series.IndexOf(r.Series);

				if (x == -1 || sx == -1)
				{
					return;
				}
				if (_tof == null || _tof.IsDisposed)
				{
					_tof = new TopicsOverviewForm(this);
				}
				_tof.Show();
				_tof.Focus();
				List<KeyValuePair<string, double>> relevantTopics = _dataset.Serieses
					.Where(elem => elem.Points[x].YValues[0] > 0)
					.Select(elem => new KeyValuePair<string, double>(elem.Name, elem.Points[x].YValues[0]))
					.ToList();
				List<int> relevantTopicIndexes = _dataset.Topics
					.Select((value, index) => new { value, index })
					.Where(elem => relevantTopics.Any(namePoint => namePoint.Key == elem.value))
					.Select(elem => elem.index).ToList();
				List<string[]> relevantDocuments = _dataset.DocumentPoints[x].Where((value, index) => relevantTopicIndexes.Contains(index)).ToList();
				_tof.DayTopicDocuments(_dataset.Startdate.AddDays(x), relevantTopics, relevantDocuments, relevantTopics.FindIndex(namePoint => namePoint.Key == r.Series.Name));
			}
		}

		private void topicOverviewChart_Customize(object sender, EventArgs e)
		{
			for (int i = 0; i < topicOverviewChart.ChartAreas[0].AxisX.CustomLabels.Count; i++)
			{
				topicOverviewChart.ChartAreas[0].AxisX.CustomLabels[i].Text = _dataset.Startdate.AddDays(_min + topicOverviewChart.ChartAreas[0].AxisX.Interval * (i * _dataset.Interval)).ToShortDateString();
			}
		}

		private void helpToolStripMenuItem_Click(object sender, EventArgs e)
		{
			HelpForm hf = new HelpForm();
			hf.ShowDialog();
		}

		private void buildChartBackgroundWorker_ProgressChanged(object sender, ProgressChangedEventArgs e)
		{
			this.Text = e.UserState.ToString();
		}
	}
}
