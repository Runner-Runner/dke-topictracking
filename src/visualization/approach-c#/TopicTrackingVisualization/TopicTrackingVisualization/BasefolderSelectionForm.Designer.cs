namespace TopicTrackingVisualization
{
	partial class BasefolderSelectionForm
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
			System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(BasefolderSelectionForm));
			this.browseButton = new System.Windows.Forms.Button();
			this.basefolderTextBox = new System.Windows.Forms.TextBox();
			this.okButton = new System.Windows.Forms.Button();
			this.descriptionLabel = new System.Windows.Forms.Label();
			this.SuspendLayout();
			// 
			// browseButton
			// 
			this.browseButton.Location = new System.Drawing.Point(266, 12);
			this.browseButton.Name = "browseButton";
			this.browseButton.Size = new System.Drawing.Size(75, 23);
			this.browseButton.TabIndex = 0;
			this.browseButton.Text = "Browse...";
			this.browseButton.UseVisualStyleBackColor = true;
			this.browseButton.Click += new System.EventHandler(this.BrowseForBasefolder);
			// 
			// basefolderTextBox
			// 
			this.basefolderTextBox.Location = new System.Drawing.Point(13, 15);
			this.basefolderTextBox.Name = "basefolderTextBox";
			this.basefolderTextBox.ReadOnly = true;
			this.basefolderTextBox.Size = new System.Drawing.Size(247, 20);
			this.basefolderTextBox.TabIndex = 1;
			this.basefolderTextBox.Click += new System.EventHandler(this.BrowseForBasefolder);
			// 
			// okButton
			// 
			this.okButton.Location = new System.Drawing.Point(139, 107);
			this.okButton.Name = "okButton";
			this.okButton.Size = new System.Drawing.Size(75, 23);
			this.okButton.TabIndex = 2;
			this.okButton.Text = "OK";
			this.okButton.UseVisualStyleBackColor = true;
			this.okButton.Click += new System.EventHandler(this.okButton_Click);
			// 
			// descriptionLabel
			// 
			this.descriptionLabel.AutoSize = true;
			this.descriptionLabel.Location = new System.Drawing.Point(13, 42);
			this.descriptionLabel.Name = "descriptionLabel";
			this.descriptionLabel.Size = new System.Drawing.Size(326, 65);
			this.descriptionLabel.TabIndex = 3;
			this.descriptionLabel.Text = resources.GetString("descriptionLabel.Text");
			// 
			// BasefolderSelectionForm
			// 
			this.AcceptButton = this.okButton;
			this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
			this.ClientSize = new System.Drawing.Size(353, 142);
			this.Controls.Add(this.descriptionLabel);
			this.Controls.Add(this.okButton);
			this.Controls.Add(this.basefolderTextBox);
			this.Controls.Add(this.browseButton);
			this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
			this.MaximizeBox = false;
			this.MinimizeBox = false;
			this.Name = "BasefolderSelectionForm";
			this.ShowIcon = false;
			this.ShowInTaskbar = false;
			this.Text = "Select a folder to search from...";
			this.ResumeLayout(false);
			this.PerformLayout();

		}

		#endregion

		private System.Windows.Forms.Button browseButton;
		private System.Windows.Forms.TextBox basefolderTextBox;
		private System.Windows.Forms.Button okButton;
		private System.Windows.Forms.Label descriptionLabel;
	}
}