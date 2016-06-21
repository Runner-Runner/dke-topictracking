using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.IO;
using System.Windows.Forms;
using System.Threading;
using AngleSharp;
using AngleSharp.Parser.Html;

namespace ReutersCrawler
{
	/// <summary>
	/// Controller of the main form
	/// </summary>
	public partial class MainForm : Form
	{
		private const int _maxRequestInterval = 250; // milliseconds; too low values may Reuters cause to block the client
		private const string _reutersWebsite = "http://www.reuters.com";
		private const string _reutersArchive = _reutersWebsite + "/resources/archive/us/";
		private CLog _log;


		public MainForm()
		{
			InitializeComponent();
			TimespanTextBox.Text = (DateTime.Now.Year - 1).ToString() + ".01";
			SaveInTextBox.Text = Environment.GetFolderPath(Environment.SpecialFolder.Desktop);
		}


		/// <summary>
		/// Starts or aborts download of the Reuters articles
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void DownloadAbortButton_Click(object sender, EventArgs e)
		{
			string regexFragment = TimespanTextRegex(TimespanTextBox.Text);
			if (regexFragment == null)
			{
				MessageBox.Show("You have specified an invalid timespan");
				return;
			}
			else
			{
				if (DownloadNewsBackgroundWorker.IsBusy)
				{
					if(DownloadNewsBackgroundWorker.WorkerSupportsCancellation)
					{
						DownloadNewsBackgroundWorker.CancelAsync();
						DownloadAbortButton.Text = "Cancelling...";
						DownloadAbortButton.Enabled = false;
					}
				}
				else
				{
					_log = new CLog();
					ToggleControlsWhenDownloadStartsOrEnds();
					DownloadNewsBackgroundWorker.RunWorkerAsync();
				}
			}
		}


		/// <summary>
		/// Select a folder to save the articles in
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void SaveInTextBox_Enter(object sender, EventArgs e)
		{
			FolderBrowserDialog fbd = new FolderBrowserDialog()
			{
				ShowNewFolderButton = true
			};
			if (fbd.ShowDialog() == DialogResult.OK)
			{
				SaveInTextBox.Text = fbd.SelectedPath;
			}
			DownloadAbortButton.Focus();
		}


		/// <summary>
		/// Disables download button if format is invalid, or nothing is selected for download; also colors the text red
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void TimespanTextBox_TextChanged(object sender, EventArgs e)
		{
			ToggleDownlaodNewsButton();
			if (TimespanTextRegex(TimespanTextBox.Text) == null)
			{
				TimespanTextBox.ForeColor = Color.Red;
			}
			else
			{
				TimespanTextBox.ForeColor = Color.Black;
			}
		}


		/// <summary>
		/// Disables download button if format is invalid, or nothing is selected for download
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void SaveHtmlCheckBox_CheckedChanged(object sender, EventArgs e)
		{
			ToggleDownlaodNewsButton();
		}


		/// <summary>
		/// Disables download button if format is invalid, or nothing is selected for download
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void SaveTxtCheckBox_CheckedChanged(object sender, EventArgs e)
		{
			ToggleDownlaodNewsButton();
		}


		/// <summary>
		/// Is executed, when the worker is started
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void DownloadNewsBackgroundWorker_DoWork(object sender, DoWorkEventArgs e)
		{
			BackgroundWorker worker = sender as BackgroundWorker;

			string regexFragment = TimespanTextRegex(TimespanTextBox.Text);
			string yearSite = _reutersArchive + TimespanTextBox.Text.Split('.')[0] + ".html";

			// use a webclient to download HTML pages
			using (WebClient client = new WebClient() { Encoding = Encoding.UTF8 } )
			{
				worker.ReportProgress(0, "Downloading overview...");
				string yearHtml = null;

				try
				{
					yearHtml = client.DownloadString(yearSite);
				}
				catch(WebException)
				{
					_log.AddLogEntry(ELogcode.Error, "Unable to connect to the specified year's overview page");
					e.Cancel = true;
					return;
				}

				if (worker.CancellationPending)
				{
					_log.AddLogEntry(ELogcode.Status, "Aborted by user input");
					e.Cancel = true;
					return;
				}
				_log.AddLogEntry(ELogcode.Status, "Year overview downloaded");


				AngleSharp.Dom.Html.IHtmlDocument yearOverview = new HtmlParser().Parse(yearHtml);
				List<string> sites = yearOverview.QuerySelectorAll(".moduleBody h5 + p > a")
					.Where(s => Regex.IsMatch(s.GetAttribute("href"), regexFragment))
					.Select(s => s.GetAttribute("href"))
					.ToList();

				// create dictionary, that groups news by year, month, day
				Dictionary<int, Dictionary<int, Dictionary<int, string>>> urlsByDate = new Dictionary<int, Dictionary<int, Dictionary<int, string>>>();
				foreach (string s in sites)
				{
					string date = s.Replace("/resources/archive/us/", "").Replace(".html", "");
					int year = Int32.Parse(date.Substring(0, 4));
					int month = Int32.Parse(date.Substring(4, 2));
					int day = Int32.Parse(date.Substring(6, 2));
					if (!urlsByDate.ContainsKey(year))
					{
						urlsByDate.Add(year, new Dictionary<int, Dictionary<int, string>>());
					}
					if (!urlsByDate[year].ContainsKey(month))
					{
						urlsByDate[year].Add(month, new Dictionary<int, string>());
					}
					if (!urlsByDate[year][month].ContainsKey(day))
					{
						urlsByDate[year][month].Add(day, s);
					}
				}

				// use this to generate pseudorandom sleep times
				Random r = new Random();
				foreach (var year in urlsByDate)
				{
					foreach (var month in year.Value)
					{
						foreach (var day in month.Value)
						{
							// create directory for that day
							string dayDirectory = Path.Combine(SaveInTextBox.Text, String.Format("{0:D4}", year.Key), String.Format("{0:D2}", month.Key), String.Format("{0:D2}", day.Key));
							if (!Directory.Exists(dayDirectory))
							{
								try
								{
									Directory.CreateDirectory(dayDirectory);
									_log.AddLogEntry(ELogcode.Output, "Created Directory '" + dayDirectory + "'");
								}
								catch (IOException exc)
								{
									_log.AddLogEntry(ELogcode.Error, "Failed creating Directory '" + dayDirectory + "', " + exc.Message);
									e.Cancel = true;
									return;
								}
							}

							string dayArticlesHtml = "";
							string dayArticlesUrl = _reutersWebsite + day.Value;
							try
							{
								dayArticlesHtml = client.DownloadString(dayArticlesUrl);
							}
							catch (WebException)
							{
								_log.AddLogEntry(ELogcode.Error, "Download failed of articles list from '" + dayArticlesUrl + "'");
								e.Cancel = true;
								return;
							}
							_log.AddLogEntry(ELogcode.Status, "Downloaded articles list from '" + dayArticlesUrl + "'");


							AngleSharp.Dom.Html.IHtmlDocument dayArticles = new HtmlParser().Parse(dayArticlesHtml);
							List<string> articleUrls = dayArticles.QuerySelectorAll(".headlineMed > a")
								.Where(s => s.GetAttribute("href").Contains("/article/"))
								.Select(s => s.GetAttribute("href"))
								.ToList();

							for (int i = 0; i < articleUrls.Count; i++)
							{
								if (worker.CancellationPending)
								{
									_log.AddLogEntry(ELogcode.Status, "Aborted by user input");
									e.Cancel = true;
									return;
								}
								else
								{
									worker.ReportProgress((int)(DayProgressBar.Maximum * ((double)i / articleUrls.Count)), "Current: " + String.Format("{0:D2}", day.Key) + "." + String.Format("{0:D2}", month.Key) + "." + String.Format("{0:D4}", year.Key) + "\n" + i + "/" + articleUrls.Count);
									string savefilename = Path.Combine(new string[] { SaveInTextBox.Text, String.Format("{0:D4}", year.Key), String.Format("{0:D2}", month.Key), String.Format("{0:D2}", day.Key), String.Format("article_{0:D4}", i + 1) });

									// if the option for skipping already processed articles is checked
									if (SkipProcessedCheckBox.Checked)
									{
										// if all files, that are supposed to be saved already exist, skip
										if ((File.Exists(savefilename + ".html") || !SaveHtmlCheckBox.Checked) && (File.Exists(savefilename + ".txt") || !SaveTxtCheckBox.Checked))
										{
											_log.AddLogEntry(ELogcode.Status, "Skipping articles for '" + savefilename + "'; they already exist");
											continue;
										}
									}

									string url = articleUrls[i];
									string rawHtml = null;

									string headline = null;
									string description = null;
									DateTime time = new DateTime(0);
									string contents = null;
									string tags = null;

									try
									{
										rawHtml = client.DownloadString(url);
									}
									// can happen when the page is unreachable, timeout occurrs, etc...
									catch (WebException)
									{
										_log.AddLogEntry(ELogcode.Error, "Download failed of article from '" + url + "'");
										if (SaveHtmlCheckBox.Checked)
										{
											File.WriteAllText(savefilename + ".html", rawHtml);
											_log.AddLogEntry(ELogcode.Output, "Saved empty article file '" + savefilename +".html'");
										}
										if (SaveTxtCheckBox.Checked)
										{
											File.WriteAllText(savefilename + ".txt", new CArticle(url, time, headline, description, contents, tags).ToString());
											_log.AddLogEntry(ELogcode.Output, "Saved empty formatted text file '" + savefilename + ".txt'");
										}
										continue;
									}

									if (SaveHtmlCheckBox.Checked)
									{
										File.WriteAllText(savefilename + ".html", rawHtml);
										_log.AddLogEntry(ELogcode.Output, "Saved raw article data in '" + savefilename + ".html'");
									}
									if (SaveTxtCheckBox.Checked)
									{
										AngleSharp.Dom.Html.IHtmlDocument document = new HtmlParser().Parse(rawHtml);

										// scan HTML document for important elements and, if possible extract information
										try
										{
											headline = document.QuerySelector("meta[name='sailthru.title']").GetAttribute("content");
										}
										catch
										{
											_log.AddLogEntry(ELogcode.Error, savefilename + ": Couldn't find attribute 'content' in selector 'meta[name='sailthru.title']'");
										}
										try
										{
											description = document.QuerySelector("meta[name='sailthru.description']").GetAttribute("content");
										}
										catch
										{
											_log.AddLogEntry(ELogcode.Error, savefilename + ": Couldn't find attribute 'content' in selector 'meta[name='sailthru.description']'");
										}
										try
										{
											time = DateTime.Parse(document.QuerySelector("meta[name='sailthru.date']").GetAttribute("content"));
										}
										catch
										{
											_log.AddLogEntry(ELogcode.Error, savefilename + ": Couldn't find attribute 'content' in selector 'meta[name='sailthru.date']'");
										}
										try
										{
											contents = CompactString(document.QuerySelector("#articleText").TextContent);
										}
										catch
										{
											_log.AddLogEntry(ELogcode.Error, savefilename + ": Couldn't find selector '#articleText'");
										}
										try
										{
											tags = document.QuerySelector("meta[name='sailthru.tags']").GetAttribute("content");
										}
										catch
										{
											_log.AddLogEntry(ELogcode.Error, savefilename + ": Couldn't find attribute 'content' in selector 'meta[name='sailthru.tags']'");
										}

										// save file with all the info that could be gathered;
										// missing information is replaced by the phrase "ERROR"
										File.WriteAllText(savefilename + ".txt", new CArticle(url, time, headline, description, contents, tags).ToString());
										_log.AddLogEntry(ELogcode.Output, "Formatted file saved in '" + savefilename + ".txt'");
									}
									// sleep to prevent DDOS blockage from Reuters
									Thread.Sleep((int)(r.NextDouble() * _maxRequestInterval));
								}
							}
							worker.ReportProgress(DayProgressBar.Maximum, "Current: " + String.Format("{0:D2}", day.Key) + "." + String.Format("{0:D2}", month.Key) + "." + String.Format("{0:D4}", year.Key) + "\n" + articleUrls.Count + "/" + articleUrls.Count);
							Thread.Sleep(20);
						}
					}
				}
			}
		}


		/// <summary>
		/// Happens, when the worker is told to report progress
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void DownloadNewsBackgroundWorker_ProgressChanged(object sender, ProgressChangedEventArgs e)
		{
			DayProgressBar.Value = e.ProgressPercentage;
			CurrentLabel.Text = e.UserState as string;
		}


		/// <summary>
		/// Happens when the worker is done, cancelled or ran into an error
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void DownloadNewsBackgroundWorker_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
		{
			if (e.Cancelled)
			{
				DayProgressBar.Value = 0;
				CurrentLabel.Text = "Download cancelled!";
			}
			else if (e.Error != null)
			{
				DayProgressBar.Value = 0;
				CurrentLabel.Text = "Error!";
				MessageBox.Show(e.Error.Message);
			}
			else
			{
				DayProgressBar.Value = DayProgressBar.Maximum;
				CurrentLabel.Text = "Done!";
			}

			// save logfiles
			if (CompleteLogCheckBox.Checked)
			{
				File.WriteAllText(Path.Combine(SaveInTextBox.Text, TimespanTextBox.Text.Split('.')[0], "ReutersCrawler-Log_" + DateTime.Now.ToString("dd-MM-yyyy_hh-mm-ss") + ".txt"), _log.FullLog());
			}
			if (ErrorLogCheckBox.Checked)
			{
				File.WriteAllText(Path.Combine(SaveInTextBox.Text, TimespanTextBox.Text.Split('.')[0], "ReutersCrawler-Errors_" + DateTime.Now.ToString("dd-MM-yyyy_hh-mm-ss") + ".txt"), _log.ErrorLog());
			}
			ToggleControlsWhenDownloadStartsOrEnds();
		}


		/// <summary>
		/// Toggles all important control elements; should be called when a download action starts or ends
		/// </summary>
		private void ToggleControlsWhenDownloadStartsOrEnds()
		{
			TimespanTextBox.Enabled = !TimespanTextBox.Enabled;
			SaveInTextBox.Enabled = !SaveInTextBox.Enabled;
			SkipProcessedCheckBox.Enabled = !SkipProcessedCheckBox.Enabled;
			SaveHtmlCheckBox.Enabled = !SaveHtmlCheckBox.Enabled;
			SaveTxtCheckBox.Enabled = !SaveTxtCheckBox.Enabled;
			CompleteLogCheckBox.Enabled = !CompleteLogCheckBox.Enabled;
			ErrorLogCheckBox.Enabled = !ErrorLogCheckBox.Enabled;
			DownloadAbortButton.Text = DownloadAbortButton.Text == "Download Articles" ? "Cancel" : "Download Articles";
			DownloadAbortButton.Enabled = true;
		}


		/// <summary>
		/// Returns the Regex-String that represents the dates, that are included in the timespan
		/// </summary>
		/// <param name="text">Timespan to check</param>
		/// <returns>Regex representing valid dates or null if invalid</returns>
		private string TimespanTextRegex(string text)
		{
			string[] components = text.Split('.');
			List<int> datecomponents = new List<int>();
			if (components.Length >= 4 || components.Length == 0)
			{
				return null;
			}
			if (components.Length == 3)
			{
				try
				{
					datecomponents.Insert(0, Int32.Parse(components[2]));
				}
				catch (FormatException)
				{
					return null;
				}
			}
			if (components.Length >= 2)
			{
				try
				{
					datecomponents.Insert(0, Int32.Parse(components[1]));
				}
				catch (FormatException)
				{
					return null;
				}
			}
			if (components.Length >= 1)
			{
				try
				{
					datecomponents.Insert(0, Int32.Parse(components[0]));
				}
				catch (FormatException)
				{
					return null;
				}
			}
			// year is too far in the past or in the future
			if (datecomponents[0] <= DateTime.Now.Year - 10 || datecomponents[0] > DateTime.Now.Year)
			{
				return null;
			}
			// invalid month
			if (datecomponents.Count >= 2 && (datecomponents[1] < 1 || datecomponents[1] > 12))
			{
				return null;
			}
			// invalid date altogether (f.e. 29.02.2015)
			if (datecomponents.Count == 3)
			{
				try
				{
					new DateTime(datecomponents[0], datecomponents[1], datecomponents[2]);
				}
				catch (ArgumentOutOfRangeException)
				{
					return null;
				}
			}
			// build string that can be regex-compared to url with articles
			if (datecomponents.Count == 3)
			{
				return String.Format("{0:D4}", datecomponents[0]) + String.Format("{0:D2}", datecomponents[1]) + String.Format("{0:D2}", datecomponents[2]);
			}
			else if (datecomponents.Count == 2)
			{
				return String.Format("{0:D4}", datecomponents[0]) + String.Format("{0:D2}", datecomponents[1]) + "\\d{2}";
			}
			else
			{
				return String.Format("{0:D4}", datecomponents[0]) + "\\d{4}";
			}
		}


		/// <summary>
		/// Returns a compact version of provided string, erasing surplus whitespaces and formatting the punctuation
		/// </summary>
		/// <param name="clean">The string to clean</param>
		/// <returns>Clean string</returns>
		private string CompactString(string clean)
		{
			StringBuilder sb = new StringBuilder();
			bool lastCharWasWhitespace = true;
			bool lastCharWasPunctuation = false;
			char[] punctuation = new char[] { ',', '.', ';', ':', '"', ')', '(' };
			for (int i = 0; i < clean.Length; i++)
			{
				// char is a whitespace
				if (Char.IsWhiteSpace(clean[i]))
				{
					if (!lastCharWasWhitespace)
					{
						sb.Append(" ");
						lastCharWasWhitespace = true;
					}
				}
				// char is letter or digit, or punctuation
				else
				{
					if (lastCharWasPunctuation && !lastCharWasWhitespace && !punctuation.Contains(clean[i]) && !Char.IsNumber(clean[i]))
					{
						sb.Append(" ");
					}
					sb.Append(clean[i]);
					if (punctuation.Contains(clean[i]))
					{
						lastCharWasPunctuation = true;
					}
					else
					{
						lastCharWasPunctuation = false;
					}
					lastCharWasWhitespace = false;
				}
			}
			return sb.ToString();
		}


		/// <summary>
		/// Toggles download button, depending on the state of the inputs
		/// </summary>
		private void ToggleDownlaodNewsButton()
		{
			if ((!SaveHtmlCheckBox.Checked && !SaveTxtCheckBox.Checked) || TimespanTextRegex(TimespanTextBox.Text) == null)
			{
				DownloadAbortButton.Enabled = false;
			}
			else
			{
				DownloadAbortButton.Enabled = true;
			}
		}
	}
}