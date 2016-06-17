namespace ReutersCrawler
{
	partial class MainForm
	{
		/// <summary>
		/// Erforderliche Designervariable.
		/// </summary>
		private System.ComponentModel.IContainer components = null;

		/// <summary>
		/// Verwendete Ressourcen bereinigen.
		/// </summary>
		/// <param name="disposing">True, wenn verwaltete Ressourcen gelöscht werden sollen; andernfalls False.</param>
		protected override void Dispose(bool disposing)
		{
			if (disposing && (components != null))
			{
				components.Dispose();
			}
			base.Dispose(disposing);
		}

		#region Vom Windows Form-Designer generierter Code

		/// <summary>
		/// Erforderliche Methode für die Designerunterstützung.
		/// Der Inhalt der Methode darf nicht mit dem Code-Editor geändert werden.
		/// </summary>
		private void InitializeComponent()
		{
			System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
			this.DownloadAbortButton = new System.Windows.Forms.Button();
			this.TimespanTextBox = new System.Windows.Forms.TextBox();
			this.DateLabel = new System.Windows.Forms.Label();
			this.SaveInTextBox = new System.Windows.Forms.TextBox();
			this.SaveInLabel = new System.Windows.Forms.Label();
			this.ExplanationLabel = new System.Windows.Forms.Label();
			this.DayProgressBar = new System.Windows.Forms.ProgressBar();
			this.CurrentLabel = new System.Windows.Forms.Label();
			this.SkipProcessedCheckBox = new System.Windows.Forms.CheckBox();
			this.DownloadNewsBackgroundWorker = new System.ComponentModel.BackgroundWorker();
			this.groupBox1 = new System.Windows.Forms.GroupBox();
			this.SaveTxtCheckBox = new System.Windows.Forms.CheckBox();
			this.SaveHtmlCheckBox = new System.Windows.Forms.CheckBox();
			this.groupBox2 = new System.Windows.Forms.GroupBox();
			this.ErrorLogCheckBox = new System.Windows.Forms.CheckBox();
			this.CompleteLogCheckBox = new System.Windows.Forms.CheckBox();
			this.groupBox1.SuspendLayout();
			this.groupBox2.SuspendLayout();
			this.SuspendLayout();
			// 
			// DownloadAbortButton
			// 
			this.DownloadAbortButton.Location = new System.Drawing.Point(13, 13);
			this.DownloadAbortButton.Name = "DownloadAbortButton";
			this.DownloadAbortButton.Size = new System.Drawing.Size(100, 23);
			this.DownloadAbortButton.TabIndex = 0;
			this.DownloadAbortButton.Text = "Download Articles";
			this.DownloadAbortButton.UseVisualStyleBackColor = true;
			this.DownloadAbortButton.Click += new System.EventHandler(this.DownloadAbortButton_Click);
			// 
			// TimespanTextBox
			// 
			this.TimespanTextBox.Location = new System.Drawing.Point(12, 110);
			this.TimespanTextBox.Name = "TimespanTextBox";
			this.TimespanTextBox.Size = new System.Drawing.Size(100, 20);
			this.TimespanTextBox.TabIndex = 1;
			this.TimespanTextBox.TextChanged += new System.EventHandler(this.TimespanTextBox_TextChanged);
			// 
			// DateLabel
			// 
			this.DateLabel.AutoSize = true;
			this.DateLabel.Location = new System.Drawing.Point(12, 94);
			this.DateLabel.Name = "DateLabel";
			this.DateLabel.Size = new System.Drawing.Size(53, 13);
			this.DateLabel.TabIndex = 2;
			this.DateLabel.Text = "Timespan";
			// 
			// SaveInTextBox
			// 
			this.SaveInTextBox.Location = new System.Drawing.Point(15, 280);
			this.SaveInTextBox.Name = "SaveInTextBox";
			this.SaveInTextBox.Size = new System.Drawing.Size(317, 20);
			this.SaveInTextBox.TabIndex = 3;
			this.SaveInTextBox.Enter += new System.EventHandler(this.SaveInTextBox_Enter);
			// 
			// SaveInLabel
			// 
			this.SaveInLabel.AutoSize = true;
			this.SaveInLabel.Location = new System.Drawing.Point(15, 264);
			this.SaveInLabel.Name = "SaveInLabel";
			this.SaveInLabel.Size = new System.Drawing.Size(142, 13);
			this.SaveInLabel.TabIndex = 4;
			this.SaveInLabel.Text = "Save into a subdirectory of...";
			// 
			// ExplanationLabel
			// 
			this.ExplanationLabel.AutoSize = true;
			this.ExplanationLabel.Location = new System.Drawing.Point(118, 94);
			this.ExplanationLabel.Name = "ExplanationLabel";
			this.ExplanationLabel.Size = new System.Drawing.Size(214, 52);
			this.ExplanationLabel.TabIndex = 5;
			this.ExplanationLabel.Text = "Specify YYYY to download a year\r\nSpecify YYYY.MM to download a month\r\nSpecify YYY" +
    "Y.MM.DD to download a day\r\nDownloading a month may take a few hours";
			// 
			// DayProgressBar
			// 
			this.DayProgressBar.Location = new System.Drawing.Point(122, 13);
			this.DayProgressBar.Maximum = 10000;
			this.DayProgressBar.Name = "DayProgressBar";
			this.DayProgressBar.Size = new System.Drawing.Size(210, 23);
			this.DayProgressBar.TabIndex = 6;
			// 
			// CurrentLabel
			// 
			this.CurrentLabel.AutoSize = true;
			this.CurrentLabel.Location = new System.Drawing.Point(119, 39);
			this.CurrentLabel.Name = "CurrentLabel";
			this.CurrentLabel.Size = new System.Drawing.Size(0, 13);
			this.CurrentLabel.TabIndex = 7;
			// 
			// SkipProcessedCheckBox
			// 
			this.SkipProcessedCheckBox.AutoSize = true;
			this.SkipProcessedCheckBox.Checked = true;
			this.SkipProcessedCheckBox.CheckState = System.Windows.Forms.CheckState.Checked;
			this.SkipProcessedCheckBox.Location = new System.Drawing.Point(15, 42);
			this.SkipProcessedCheckBox.Name = "SkipProcessedCheckBox";
			this.SkipProcessedCheckBox.Size = new System.Drawing.Size(92, 30);
			this.SkipProcessedCheckBox.TabIndex = 9;
			this.SkipProcessedCheckBox.Text = "Skip articles\r\nalready saved";
			this.SkipProcessedCheckBox.UseVisualStyleBackColor = true;
			// 
			// DownloadNewsBackgroundWorker
			// 
			this.DownloadNewsBackgroundWorker.WorkerReportsProgress = true;
			this.DownloadNewsBackgroundWorker.WorkerSupportsCancellation = true;
			this.DownloadNewsBackgroundWorker.DoWork += new System.ComponentModel.DoWorkEventHandler(this.DownloadNewsBackgroundWorker_DoWork);
			this.DownloadNewsBackgroundWorker.ProgressChanged += new System.ComponentModel.ProgressChangedEventHandler(this.DownloadNewsBackgroundWorker_ProgressChanged);
			this.DownloadNewsBackgroundWorker.RunWorkerCompleted += new System.ComponentModel.RunWorkerCompletedEventHandler(this.DownloadNewsBackgroundWorker_RunWorkerCompleted);
			// 
			// groupBox1
			// 
			this.groupBox1.Controls.Add(this.SaveTxtCheckBox);
			this.groupBox1.Controls.Add(this.SaveHtmlCheckBox);
			this.groupBox1.Location = new System.Drawing.Point(13, 156);
			this.groupBox1.Name = "groupBox1";
			this.groupBox1.Size = new System.Drawing.Size(319, 46);
			this.groupBox1.TabIndex = 11;
			this.groupBox1.TabStop = false;
			this.groupBox1.Text = "Save Content from Download";
			// 
			// SaveTxtCheckBox
			// 
			this.SaveTxtCheckBox.AutoSize = true;
			this.SaveTxtCheckBox.Checked = true;
			this.SaveTxtCheckBox.CheckState = System.Windows.Forms.CheckState.Checked;
			this.SaveTxtCheckBox.Location = new System.Drawing.Point(125, 21);
			this.SaveTxtCheckBox.Name = "SaveTxtCheckBox";
			this.SaveTxtCheckBox.Size = new System.Drawing.Size(144, 17);
			this.SaveTxtCheckBox.TabIndex = 1;
			this.SaveTxtCheckBox.Text = "Formatted Textdocument";
			this.SaveTxtCheckBox.UseVisualStyleBackColor = true;
			this.SaveTxtCheckBox.CheckedChanged += new System.EventHandler(this.SaveTxtCheckBox_CheckedChanged);
			// 
			// SaveHtmlCheckBox
			// 
			this.SaveHtmlCheckBox.AutoSize = true;
			this.SaveHtmlCheckBox.Checked = true;
			this.SaveHtmlCheckBox.CheckState = System.Windows.Forms.CheckState.Checked;
			this.SaveHtmlCheckBox.Location = new System.Drawing.Point(17, 21);
			this.SaveHtmlCheckBox.Name = "SaveHtmlCheckBox";
			this.SaveHtmlCheckBox.Size = new System.Drawing.Size(81, 17);
			this.SaveHtmlCheckBox.TabIndex = 0;
			this.SaveHtmlCheckBox.Text = "Raw HTML";
			this.SaveHtmlCheckBox.UseVisualStyleBackColor = true;
			this.SaveHtmlCheckBox.CheckedChanged += new System.EventHandler(this.SaveHtmlCheckBox_CheckedChanged);
			// 
			// groupBox2
			// 
			this.groupBox2.Controls.Add(this.ErrorLogCheckBox);
			this.groupBox2.Controls.Add(this.CompleteLogCheckBox);
			this.groupBox2.Location = new System.Drawing.Point(13, 208);
			this.groupBox2.Name = "groupBox2";
			this.groupBox2.Size = new System.Drawing.Size(319, 46);
			this.groupBox2.TabIndex = 12;
			this.groupBox2.TabStop = false;
			this.groupBox2.Text = "Save Logfiles";
			// 
			// ErrorLogCheckBox
			// 
			this.ErrorLogCheckBox.AutoSize = true;
			this.ErrorLogCheckBox.Checked = true;
			this.ErrorLogCheckBox.CheckState = System.Windows.Forms.CheckState.Checked;
			this.ErrorLogCheckBox.Location = new System.Drawing.Point(125, 21);
			this.ErrorLogCheckBox.Name = "ErrorLogCheckBox";
			this.ErrorLogCheckBox.Size = new System.Drawing.Size(69, 17);
			this.ErrorLogCheckBox.TabIndex = 1;
			this.ErrorLogCheckBox.Text = "Error Log";
			this.ErrorLogCheckBox.UseVisualStyleBackColor = true;
			// 
			// CompleteLogCheckBox
			// 
			this.CompleteLogCheckBox.AutoSize = true;
			this.CompleteLogCheckBox.Location = new System.Drawing.Point(17, 21);
			this.CompleteLogCheckBox.Name = "CompleteLogCheckBox";
			this.CompleteLogCheckBox.Size = new System.Drawing.Size(91, 17);
			this.CompleteLogCheckBox.TabIndex = 0;
			this.CompleteLogCheckBox.Text = "Complete Log";
			this.CompleteLogCheckBox.UseVisualStyleBackColor = true;
			// 
			// MainForm
			// 
			this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
			this.ClientSize = new System.Drawing.Size(344, 311);
			this.Controls.Add(this.groupBox2);
			this.Controls.Add(this.groupBox1);
			this.Controls.Add(this.SkipProcessedCheckBox);
			this.Controls.Add(this.CurrentLabel);
			this.Controls.Add(this.DayProgressBar);
			this.Controls.Add(this.ExplanationLabel);
			this.Controls.Add(this.SaveInLabel);
			this.Controls.Add(this.SaveInTextBox);
			this.Controls.Add(this.DateLabel);
			this.Controls.Add(this.TimespanTextBox);
			this.Controls.Add(this.DownloadAbortButton);
			this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
			this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
			this.MaximizeBox = false;
			this.Name = "MainForm";
			this.Text = "Reuters Archive Crawler";
			this.groupBox1.ResumeLayout(false);
			this.groupBox1.PerformLayout();
			this.groupBox2.ResumeLayout(false);
			this.groupBox2.PerformLayout();
			this.ResumeLayout(false);
			this.PerformLayout();

		}

		#endregion

		private System.Windows.Forms.Button DownloadAbortButton;
		private System.Windows.Forms.TextBox TimespanTextBox;
		private System.Windows.Forms.Label DateLabel;
		private System.Windows.Forms.TextBox SaveInTextBox;
		private System.Windows.Forms.Label SaveInLabel;
		private System.Windows.Forms.Label ExplanationLabel;
		private System.Windows.Forms.ProgressBar DayProgressBar;
		private System.Windows.Forms.Label CurrentLabel;
		private System.Windows.Forms.CheckBox SkipProcessedCheckBox;
		private System.ComponentModel.BackgroundWorker DownloadNewsBackgroundWorker;
		private System.Windows.Forms.GroupBox groupBox2;
		private System.Windows.Forms.CheckBox ErrorLogCheckBox;
		private System.Windows.Forms.CheckBox CompleteLogCheckBox;
		private System.Windows.Forms.GroupBox groupBox1;
		private System.Windows.Forms.CheckBox SaveTxtCheckBox;
		private System.Windows.Forms.CheckBox SaveHtmlCheckBox;
	}
}

