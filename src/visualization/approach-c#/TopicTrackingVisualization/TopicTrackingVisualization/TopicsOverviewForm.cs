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
		private Main _mainForm;

		private DateTime _date;
		private List<string> _topics;
		private List<string[]> _topicDocuments;

		public TopicsOverviewForm(Main mainForm)
		{
			_mainForm = mainForm;
			InitializeComponent();
		}

		public void DayTopicDocuments(DateTime date, List<string> topics, List<string[]> topicDocuments, int selection)
		{
			_date = date;
			_topics = topics;
			_topicDocuments = topicDocuments;
			this.Text = $"Topics for {date.ToShortDateString()}";
			topicListBox.Items.Clear();
			foreach(string topic in topics)
			{
				topicListBox.Items.Add(topic);
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
				string v = $"{documentsListBox.Items[documentsListBox.SelectedIndex].ToString().Split('.')[0]}.*";
				List<string> filesFound = new List<string>();
				DirSearch(filesFound, basefolder, v);
				if (filesFound.Count == 0)
				{
					MessageBox.Show("No files were found with that name");
				}
				else
				{
					Process.Start("explorer.exe", $"/select, \"{filesFound[0]}\"");
				}
			}
			else
			{
				MessageBox.Show("You can watch the file in your File Explorer by selecting a basefolder using \"Documents\" > \"Define Basefolder\"");
			}
		}

		private void DirSearch(List<string> lstFilesFound, string sDir, string search)
		{
			try
			{
				foreach (string f in Directory.GetFiles(sDir, search))
				{
					lstFilesFound.Add(f);
				}
				foreach (string d in Directory.GetDirectories(sDir))
				{
					DirSearch(lstFilesFound, d, search);
				}
			}
			catch (System.Exception excpt)
			{
				MessageBox.Show("Error while searching for the file!");
			}
		}
	}
}
