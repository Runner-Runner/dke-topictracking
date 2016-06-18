using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Windows.Forms;

namespace TopicTrackingVisualization
{
	public partial class BasefolderSelectionForm : Form
	{
		public string Basefolder { get { return basefolderTextBox.Text == "" ? null : basefolderTextBox.Text; } }

		public BasefolderSelectionForm()
		{
			InitializeComponent();
		}

		private void BrowseForBasefolder(object sender, EventArgs e)
		{
			FolderBrowserDialog fbd = new FolderBrowserDialog()
				{
					ShowNewFolderButton = true,
					Description = "Select the basefolder to search from"
				};
			if (fbd.ShowDialog() == DialogResult.OK)
			{
				if (Directory.Exists(fbd.SelectedPath))
				{
					basefolderTextBox.Text = fbd.SelectedPath;
				}
			}
		}

		private void okButton_Click(object sender, EventArgs e)
		{
			this.Close();
			this.DialogResult = DialogResult.OK;
		}
	}
}
