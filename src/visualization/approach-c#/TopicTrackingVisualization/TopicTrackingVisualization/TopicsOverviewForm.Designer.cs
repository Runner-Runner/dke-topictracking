namespace TopicTrackingVisualization
{
	partial class TopicsOverviewForm
	{
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.IContainer components = null;

		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		/// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
		protected override void Dispose(bool disposing)
		{
			if (disposing && (components != null))
			{
				components.Dispose();
			}
			base.Dispose(disposing);
		}

		#region Windows Form Designer generated code

		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
			this.documentsListBox = new System.Windows.Forms.ListBox();
			this.topicListBox = new System.Windows.Forms.ListBox();
			this.searchDirBackgroundWorker = new System.ComponentModel.BackgroundWorker();
			this.SuspendLayout();
			// 
			// documentsListBox
			// 
			this.documentsListBox.Font = new System.Drawing.Font("Courier New", 8.25F);
			this.documentsListBox.FormattingEnabled = true;
			this.documentsListBox.ItemHeight = 14;
			this.documentsListBox.Location = new System.Drawing.Point(320, 13);
			this.documentsListBox.Name = "documentsListBox";
			this.documentsListBox.Size = new System.Drawing.Size(150, 298);
			this.documentsListBox.TabIndex = 1;
			this.documentsListBox.MouseDoubleClick += new System.Windows.Forms.MouseEventHandler(this.documentsListBox_MouseDoubleClick);
			// 
			// topicListBox
			// 
			this.topicListBox.Font = new System.Drawing.Font("Courier New", 8.25F);
			this.topicListBox.FormattingEnabled = true;
			this.topicListBox.ItemHeight = 14;
			this.topicListBox.Location = new System.Drawing.Point(10, 13);
			this.topicListBox.Name = "topicListBox";
			this.topicListBox.Size = new System.Drawing.Size(301, 298);
			this.topicListBox.TabIndex = 2;
			this.topicListBox.SelectedIndexChanged += new System.EventHandler(this.topicListBox_SelectedIndexChanged);
			// 
			// searchDirBackgroundWorker
			// 
			this.searchDirBackgroundWorker.WorkerSupportsCancellation = true;
			this.searchDirBackgroundWorker.DoWork += new System.ComponentModel.DoWorkEventHandler(this.searchDirBackgroundWorker_DoWork);
			this.searchDirBackgroundWorker.RunWorkerCompleted += new System.ComponentModel.RunWorkerCompletedEventHandler(this.searchDirBackgroundWorker_RunWorkerCompleted);
			// 
			// TopicsOverviewForm
			// 
			this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
			this.ClientSize = new System.Drawing.Size(484, 325);
			this.Controls.Add(this.topicListBox);
			this.Controls.Add(this.documentsListBox);
			this.MaximizeBox = false;
			this.MinimizeBox = false;
			this.MinimumSize = new System.Drawing.Size(500, 363);
			this.Name = "TopicsOverviewForm";
			this.ShowIcon = false;
			this.Text = "TopicsOverviewForm";
			this.Resize += new System.EventHandler(this.TopicsOverviewForm_Resize);
			this.ResumeLayout(false);

		}

		#endregion
		private System.Windows.Forms.ListBox documentsListBox;
		private System.Windows.Forms.ListBox topicListBox;
		private System.ComponentModel.BackgroundWorker searchDirBackgroundWorker;
	}
}