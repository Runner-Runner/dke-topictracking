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
using System.Windows.Forms;
using System.Windows.Forms.DataVisualization.Charting;

namespace TopicTrackingVisualization
{
	public partial class Main : Form
	{
		private Dataset _dataset;

		public Main()
		{
			InitializeComponent();
		}

		private void Main_Resize(object sender, EventArgs e)
		{
			topicOverviewChart.Width = this.Width - 40;
			topicOverviewChart.Height = this.Height - 62;
		}

		private void topicOverviewChart_AxisViewChanged(object sender, System.Windows.Forms.DataVisualization.Charting.ViewEventArgs e)
		{
			setAxisXInterval();
		}

		private void setAxisXInterval()
		{
			int selected = (int)Math.Abs((Double.IsNaN(topicOverviewChart.ChartAreas[0].AxisX.ScaleView.ViewMaximum) ? 0 : topicOverviewChart.ChartAreas[0].AxisX.ScaleView.ViewMaximum) - (Double.IsNaN(topicOverviewChart.ChartAreas[0].AxisX.ScaleView.ViewMinimum) ? 0 : topicOverviewChart.ChartAreas[0].AxisX.ScaleView.ViewMinimum));
			if (selected == 0)
			{
				selected = topicOverviewChart.Series[0].Points.Count;
			}
			//MessageBox.Show($"{topicOverviewChart.ChartAreas[0].AxisX.ScaleView.ViewMinimum} - {topicOverviewChart.ChartAreas[0].AxisX.ScaleView.ViewMaximum}");
			topicOverviewChart.ChartAreas[0].AxisX.Interval = 1 + (selected / 50);
		}

		private void topicOverviewChart_Paint(object sender, PaintEventArgs e)
		{
			//Pen p = new Pen(new SolidBrush(Color.FromArgb(255, 0, 0, 0)));
			//var series = 
			//MessageBox.Show("NOW!");
		}

		private void openToolStripMenuItem_Click(object sender, EventArgs e)
		{
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
					_dataset = Dataset.CreateFromFiles(File.ReadAllLines(ofd.FileName), File.ReadAllLines(ofd2.FileName));
					for (int i = 0; i <  _dataset.Serieses.Count; i++)
					{
						topicOverviewChart.Series.Add(_dataset.Serieses[i]);
					}
					topicOverviewChart.ChartAreas[0].CursorX.IsUserSelectionEnabled = true;

					topicOverviewChart.ChartAreas[0].AxisX.Maximum = topicOverviewChart.Series[0].Points.Count;
					topicOverviewChart.ChartAreas[0].AxisX.Minimum = 1;
					topicOverviewChart.ChartAreas[0].AxisX.MajorTickMark.Interval = 1;

					setAxisXInterval();

					/*
					topicOverviewChart.ChartAreas[0].AxisX.MajorGrid.Enabled = false;
					topicOverviewChart.ChartAreas[0].AxisY.MajorGrid.Enabled = false;
					topicOverviewChart.ChartAreas[0].AxisX.LineColor = Color.Transparent;
					topicOverviewChart.ChartAreas[0].AxisY.LineColor = Color.Transparent;
					*/
					
					topicOverviewChart.ApplyPaletteColors();
					foreach (Series s in topicOverviewChart.Series) s.Color = Color.FromArgb(192, s.Color);
					
				}
			}
		}

		private void randomToolStripMenuItem_Click(object sender, EventArgs e)
		{
			DateTime start = new DateTime(1996, 8, 26);
			Random rand = new Random();

			//List<System.Windows.Forms.DataVisualization.Charting.Legend> legends = new List<System.Windows.Forms.DataVisualization.Charting.Legend>();
			List<Series> serieses = new List<Series>();
			for (int i = 0; i < 20; i++)
			{
				//legends.Add(new System.Windows.Forms.DataVisualization.Charting.Legend());
				serieses.Add(new Series());
			}
			for (int i = 0; i < serieses.Count; i++)
			{
				for (int j = 0; j < 365; j++)
				{
					//double y = (double)j / ((double)(j + 1));
					serieses[i].Points.AddXY(start.AddDays(j).ToShortDateString(), rand.NextDouble());
				}
				serieses[i].Name = $"Topic #{i}";
				serieses[i].ChartType = SeriesChartType.StackedArea100;
				//serieses[i].BorderDashStyle = System.Windows.Forms.DataVisualization.Charting.ChartDashStyle.Solid;
				topicOverviewChart.Series.Add(serieses[i]);
				//legends[i].Title = $"Sample Title {i}";
				//topicOverviewChart.Legends.Add(legends[i]);
			}
			for (int i = 0; i < serieses.Count; i++)
			{
				//serieses[i].Color = Color.FromArgb(serieses[i].Color.R, serieses[i].Color.G, serieses[i].Color.B, 0);
				foreach (var p in serieses[i].Points)
				{
					p.ToolTip = $"{serieses[i].Name} --- {Math.Round(p.YValues[0], 2)}";
				}
			}
			topicOverviewChart.ChartAreas[0].CursorX.IsUserSelectionEnabled = true;

			//topicOverviewChart.ChartAreas[0].AxisY.Interval = 0.05;
			topicOverviewChart.ChartAreas[0].AxisX.Maximum = topicOverviewChart.Series[0].Points.Count;
			topicOverviewChart.ChartAreas[0].AxisX.Minimum = 1;
			//topicOverviewChart.ChartAreas[0].AxisX.MajorGrid.LineColor = Color.FromArgb(255, 0, 0, 0);
			//topicOverviewChart.ChartAreas[0].AxisX.MajorGrid = new System.Windows.Forms.DataVisualization.Charting.Grid();
			topicOverviewChart.ChartAreas[0].AxisX.MajorTickMark.Interval = 1;

			setAxisXInterval();
		}

		private void tESTToolStripMenuItem_Click(object sender, EventArgs e)
		{
			MessageBox.Show("TODO: Show tooltips, show documents");
		}

		private void topicOverviewChart_PostPaint(object sender, ChartPaintEventArgs e)
		{
			return;
			ChartArea ca = topicOverviewChart.ChartAreas[0];
			RectangleF ipar = InnerPlotPositionClientRectangle(topicOverviewChart, ca);
			Axis ax = ca.AxisX;
			Axis ay = ca.AxisY;
			Color gc = ax.MajorGrid.LineColor;
			Pen pen = new Pen(gc);
			double ix = ax.Interval == 0 ? 1 : ax.Interval;  // best make sure to set..
			double iy = ay.Interval == 0 ? 50 : ay.Interval; // ..the intervals!

			for (double vx = ax.Minimum; vx <= ax.Maximum; vx += ix)
			{
				int x = (int)ax.ValueToPixelPosition(vx);
				e.ChartGraphics.Graphics.DrawLine(pen, x, ipar.Top, x, ipar.Bottom);
			}

			for (double vy = ay.Minimum; vy <= ay.Maximum; vy += iy)
			{
				int y = (int)ay.ValueToPixelPosition(vy);
				e.ChartGraphics.Graphics.DrawLine(pen, ipar.Left, y, ipar.Right, y);
			}
		}

		private RectangleF ChartAreaClientRectangle(Chart chart, ChartArea CA)
		{
			RectangleF CAR = CA.Position.ToRectangleF();
			float pw = chart.ClientSize.Width / 100f;
			float ph = chart.ClientSize.Height / 100f;
			return new RectangleF(pw * CAR.X, ph * CAR.Y, pw * CAR.Width, ph * CAR.Height);
		}

		private RectangleF InnerPlotPositionClientRectangle(Chart chart, ChartArea CA)
		{
			RectangleF IPP = CA.InnerPlotPosition.ToRectangleF();
			RectangleF CArp = ChartAreaClientRectangle(chart, CA);

			float pw = CArp.Width / 100f;
			float ph = CArp.Height / 100f;

			return new RectangleF(CArp.X + pw * IPP.X, CArp.Y + ph * IPP.Y,
			pw * IPP.Width, ph * IPP.Height);
		}
	}
}
