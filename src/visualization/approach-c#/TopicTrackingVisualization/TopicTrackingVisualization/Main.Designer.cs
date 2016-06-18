﻿namespace TopicTrackingVisualization
{
	partial class Main
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
			System.Windows.Forms.DataVisualization.Charting.ChartArea chartArea1 = new System.Windows.Forms.DataVisualization.Charting.ChartArea();
			System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Main));
			this.topicOverviewChart = new System.Windows.Forms.DataVisualization.Charting.Chart();
			this.menuStrip1 = new System.Windows.Forms.MenuStrip();
			this.fileToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
			this.openToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
			this.randomToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
			this.documentsToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
			this.defineBasefolderToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
			this.helpToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
			((System.ComponentModel.ISupportInitialize)(this.topicOverviewChart)).BeginInit();
			this.menuStrip1.SuspendLayout();
			this.SuspendLayout();
			// 
			// topicOverviewChart
			// 
			chartArea1.Name = "ChartArea1";
			this.topicOverviewChart.ChartAreas.Add(chartArea1);
			this.topicOverviewChart.Location = new System.Drawing.Point(12, 27);
			this.topicOverviewChart.Name = "topicOverviewChart";
			this.topicOverviewChart.Size = new System.Drawing.Size(533, 361);
			this.topicOverviewChart.TabIndex = 0;
			this.topicOverviewChart.Text = "chart1";
			this.topicOverviewChart.AxisViewChanged += new System.EventHandler<System.Windows.Forms.DataVisualization.Charting.ViewEventArgs>(this.topicOverviewChart_AxisViewChanged);
			this.topicOverviewChart.Customize += new System.EventHandler(this.topicOverviewChart_Customize);
			this.topicOverviewChart.Paint += new System.Windows.Forms.PaintEventHandler(this.topicOverviewChart_Paint);
			this.topicOverviewChart.MouseClick += new System.Windows.Forms.MouseEventHandler(this.topicOverviewChart_MouseClick);
			// 
			// menuStrip1
			// 
			this.menuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.fileToolStripMenuItem,
            this.documentsToolStripMenuItem,
            this.helpToolStripMenuItem});
			this.menuStrip1.Location = new System.Drawing.Point(0, 0);
			this.menuStrip1.Name = "menuStrip1";
			this.menuStrip1.Size = new System.Drawing.Size(557, 24);
			this.menuStrip1.TabIndex = 1;
			this.menuStrip1.Text = "menuStrip1";
			// 
			// fileToolStripMenuItem
			// 
			this.fileToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.openToolStripMenuItem,
            this.randomToolStripMenuItem});
			this.fileToolStripMenuItem.Name = "fileToolStripMenuItem";
			this.fileToolStripMenuItem.Size = new System.Drawing.Size(37, 20);
			this.fileToolStripMenuItem.Text = "File";
			// 
			// openToolStripMenuItem
			// 
			this.openToolStripMenuItem.Name = "openToolStripMenuItem";
			this.openToolStripMenuItem.ShortcutKeys = ((System.Windows.Forms.Keys)((System.Windows.Forms.Keys.Control | System.Windows.Forms.Keys.O)));
			this.openToolStripMenuItem.Size = new System.Drawing.Size(146, 22);
			this.openToolStripMenuItem.Text = "Open";
			this.openToolStripMenuItem.Click += new System.EventHandler(this.openToolStripMenuItem_Click);
			// 
			// randomToolStripMenuItem
			// 
			this.randomToolStripMenuItem.Name = "randomToolStripMenuItem";
			this.randomToolStripMenuItem.Size = new System.Drawing.Size(146, 22);
			this.randomToolStripMenuItem.Text = "Random";
			this.randomToolStripMenuItem.Click += new System.EventHandler(this.randomToolStripMenuItem_Click);
			// 
			// documentsToolStripMenuItem
			// 
			this.documentsToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.defineBasefolderToolStripMenuItem});
			this.documentsToolStripMenuItem.Name = "documentsToolStripMenuItem";
			this.documentsToolStripMenuItem.Size = new System.Drawing.Size(80, 20);
			this.documentsToolStripMenuItem.Text = "Documents";
			// 
			// defineBasefolderToolStripMenuItem
			// 
			this.defineBasefolderToolStripMenuItem.Name = "defineBasefolderToolStripMenuItem";
			this.defineBasefolderToolStripMenuItem.ShortcutKeys = ((System.Windows.Forms.Keys)((System.Windows.Forms.Keys.Control | System.Windows.Forms.Keys.D)));
			this.defineBasefolderToolStripMenuItem.Size = new System.Drawing.Size(208, 22);
			this.defineBasefolderToolStripMenuItem.Text = "Define Basefolder";
			this.defineBasefolderToolStripMenuItem.Click += new System.EventHandler(this.defineBasefolderToolStripMenuItem_Click);
			// 
			// helpToolStripMenuItem
			// 
			this.helpToolStripMenuItem.Name = "helpToolStripMenuItem";
			this.helpToolStripMenuItem.Size = new System.Drawing.Size(44, 20);
			this.helpToolStripMenuItem.Text = "Help";
			this.helpToolStripMenuItem.Click += new System.EventHandler(this.helpToolStripMenuItem_Click);
			// 
			// Main
			// 
			this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
			this.ClientSize = new System.Drawing.Size(557, 402);
			this.Controls.Add(this.topicOverviewChart);
			this.Controls.Add(this.menuStrip1);
			this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
			this.Name = "Main";
			this.Text = "Topic Tracking";
			this.Resize += new System.EventHandler(this.Main_Resize);
			((System.ComponentModel.ISupportInitialize)(this.topicOverviewChart)).EndInit();
			this.menuStrip1.ResumeLayout(false);
			this.menuStrip1.PerformLayout();
			this.ResumeLayout(false);
			this.PerformLayout();

		}

		#endregion

		private System.Windows.Forms.DataVisualization.Charting.Chart topicOverviewChart;
		private System.Windows.Forms.MenuStrip menuStrip1;
		private System.Windows.Forms.ToolStripMenuItem fileToolStripMenuItem;
		private System.Windows.Forms.ToolStripMenuItem openToolStripMenuItem;
		private System.Windows.Forms.ToolStripMenuItem randomToolStripMenuItem;
		private System.Windows.Forms.ToolStripMenuItem documentsToolStripMenuItem;
		private System.Windows.Forms.ToolStripMenuItem defineBasefolderToolStripMenuItem;
		private System.Windows.Forms.ToolStripMenuItem helpToolStripMenuItem;
	}
}
