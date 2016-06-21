using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.IO;
using ExtensionMethods;
using System.Diagnostics;

namespace TopicTrackingVisualization
{
	public partial class TopicsOverviewForm : Form
	{
		private MainForm _mainForm;

		private DateTime _date;
		private List<KeyValuePair<string, double>> _topics;
		private List<string[]> _topicDocuments;

		private System.Threading.Timer _workTimer;

		public TopicsOverviewForm(MainForm mainForm)
		{
			_mainForm = mainForm;
			InitializeComponent();
		}

		public void DayTopicDocuments(DateTime date, List<KeyValuePair<string, double>> topics, List<string[]> topicDocuments, int selection)
		{
			_date = date;
			_topics = topics;
			_topicDocuments = topicDocuments;
			this.Text = $"Topics for {date.ToShortDateString()}";
			topicListBox.Items.Clear();
			foreach(KeyValuePair<string, double> topic in topics)
			{
				topicListBox.Items.Add($"{Math.Round(topic.Value*100, 2).ToString("0.00").PadLeft(5, ' ')}\t{topic.Key}");
			}
			topicListBox.SelectedIndex = selection;
		}

		private void topicListBox_SelectedIndexChanged(object sender, EventArgs e)
		{
			documentsListBox.Items.Clear();
			foreach(string document in _topicDocuments[topicListBox.SelectedIndex])
			{
				documentsListBox.Items.Add(document);
			}
		}

		private void TopicsOverviewForm_Resize(object sender, EventArgs e)
		{
			topicListBox.Height = this.Height - 60;
			documentsListBox.Height = this.Height - 60;
			topicListBox.Width = (this.Width - 50) / 3 * 2;
			documentsListBox.Left = topicListBox.Left + topicListBox.Width + 10;
			documentsListBox.Width = topicListBox.Width / 2;
		}

		private void documentsListBox_MouseDoubleClick(object sender, MouseEventArgs e)
		{
			string basefolder = _mainForm.Basefolder;
			if (basefolder != null && basefolder != "")
			{
				string filenameNoExtension = $"{documentsListBox.Items[documentsListBox.SelectedIndex].ToString().Split('.')[0]}.*";
				searchDirBackgroundWorker.RunWorkerAsync(new string[] { basefolder, filenameNoExtension });
				_workTimer = new System.Threading.Timer(Tick, null, 30000, 30000);

			}
			else
			{
				MessageBox.Show("You can watch the file in your File Explorer by selecting a basefolder using \"File\" > \"Source Documents\"");
			}
		}

		private void Tick(object state)
		{
			searchDirBackgroundWorker.CancelAsync();
			_workTimer.Dispose();
		}

		private void DirSearch(BackgroundWorker worker, List<string> lstFilesFound, string sDir, string search)
		{
			if (worker.CancellationPending) return;
			try
			{
				foreach (string f in Directory.GetFiles(sDir, search))
				{
					lstFilesFound.Add(f);
				}
				foreach (string d in Directory.GetDirectories(sDir))
				{
					DirSearch(worker, lstFilesFound, d, search);
				}
			}
			catch (Exception)
			{
				MessageBox.Show("Error while searching for the file!");
			}
		}

		private void searchDirBackgroundWorker_DoWork(object sender, DoWorkEventArgs e)
		{
			string[] args = (string[])e.Argument;
			List<string> filesFound = new List<string>();
			DirSearch((BackgroundWorker)sender, filesFound, args[0], args[1]);
			if (searchDirBackgroundWorker.CancellationPending)
			{
				e.Cancel = true;
				return;
			}
			e.Result = filesFound;
		}

		private void searchDirBackgroundWorker_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
		{
			_workTimer.Dispose();
			if (e.Cancelled)
			{
				MessageBox.Show("Search took too long so it was aborted, please consider using less documents or a more precise location.");
				return;
			}
			List<string> filesFound = (List<string>)e.Result;
			if (filesFound.Count == 0)
			{
				MessageBox.Show("No files were found with that name");
			}
			else
			{
				Process.Start("explorer.exe", $"/select, \"{filesFound[0]}\"");
			}
		}
	}
}
