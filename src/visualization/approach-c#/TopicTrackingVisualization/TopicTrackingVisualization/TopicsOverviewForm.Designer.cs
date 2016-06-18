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
			this.topicListBox = new System.Windows.Forms.ListBox();
			this.documentsListBox = new System.Windows.Forms.ListBox();
			this.SuspendLayout();
			// 
			// topicListBox
			// 
			this.topicListBox.FormattingEnabled = true;
			this.topicListBox.Location = new System.Drawing.Point(10, 13);
			this.topicListBox.Name = "topicListBox";
			this.topicListBox.Size = new System.Drawing.Size(300, 303);
			this.topicListBox.TabIndex = 0;
			this.topicListBox.SelectedIndexChanged += new System.EventHandler(this.topicListBox_SelectedIndexChanged);
			// 
			// documentsListBox
			// 
			this.documentsListBox.FormattingEnabled = true;
			this.documentsListBox.Location = new System.Drawing.Point(320, 13);
			this.documentsListBox.Name = "documentsListBox";
			this.documentsListBox.Size = new System.Drawing.Size(150, 303);
			this.documentsListBox.TabIndex = 1;
			this.documentsListBox.MouseDoubleClick += new System.Windows.Forms.MouseEventHandler(this.documentsListBox_MouseDoubleClick);
			// 
			// TopicsOverviewForm
			// 
			this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
			this.ClientSize = new System.Drawing.Size(484, 325);
			this.Controls.Add(this.documentsListBox);
			this.Controls.Add(this.topicListBox);
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

		private System.Windows.Forms.ListBox topicListBox;
		private System.Windows.Forms.ListBox documentsListBox;
	}
}