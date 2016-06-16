require 'date'
require 'json'

### script for formatting the raw text files to json format, running will overwrite format explanation
### approach here was not quite right, must be changed for fixed-height (100%) topic river

# the two together: how strongly is a word connected to a topic?
topics = "dtm_out/ldatopics.txt" # length: 104*2 á 66276 words
voc = "dtm_out/voc.txt" # length: 66276

# interesting stuff for visualization
t20 = "dtm_out/ldatopics20.txt" # length: 104*2 á 20 words
tpd = "dtm_out/ldaTopicsPerDoc.txt" # length: 23307 with possible topics

buzzwords = File.read(t20).split("\n").reject.with_index{|line,idx| idx.even? }.collect{|line| line.split(" ")[0..4].collect{|word| word.split(":")[0] }.join(", ") }
per_doc = File.read(tpd).split("\n")

docs = Array.new()
per_doc.each do |doc|
  topics = Hash.new()
  doc.split(" ").each do |w|
    k, v = w.split(":")
	topics[k.to_i] = v.to_f
  end
  docs.push(topics)
end
series = (0..103).collect{|i| {:name => "Topic \"#{buzzwords[i]}\"", :data => Array.new()} }
docs.each_slice(docs.count/12).with_index do |docs,i|
  if i == 12
    break
  end
  merged_hash = Hash.new()
  docs.each do |doc|
    doc.each do |k,v|
	  if merged_hash.key?(k)
	    merged_hash[k] += v/(docs.count/12)
	  else
	    merged_hash[k] = v/(docs.count/12)
	  end
	end
  end
  series.each_with_index do |s,j|
    s[:data].push({"x" => i, "y" => merged_hash[j] || 0 })
  end
end
importance = Hash.new()
series.each_with_index do |s,i|
  importance[i] = s[:data].collect{|h| h["y"]}.inject(0){|sum,x| sum+x}
end

most_important = importance.sort_by{|k,v| v}[0..19].collect{|imp| series[imp[0]]}
File.open("topics.js", "w"){ |f| f.write("var data = #{most_important.to_json};") }