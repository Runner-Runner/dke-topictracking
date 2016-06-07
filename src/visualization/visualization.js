$(document).ready(function(){
	
	// this is the leftmost date shown
	var startdate = new Date(1996, 7, 29, 0, 0, 0, 0);
	// used for coloring the topics
	var palette = new Rickshaw.Color.Palette();//{scheme: 'spectrum2001', interpolatedStopCount: 4});
	
	// add color from palette to data
	var series = data;
	$.each(series, function(idx,s){
		s["color"] = palette.color();
	});

	// create graph, render as stacked graph
	var graph = new Rickshaw.Graph( {
        element: document.querySelector("#chart"),
        width: 1024,
        height: 512,
		//renderer: 'line',
        series: series
	} );

	// create the X axis
	var x_axis = new Rickshaw.Graph.Axis.X({
		graph: graph,
		orientation: 'bottom',
		element: document.querySelector('#x_axis'),
		// increment the date by 1 day per x
		tickFormat: function(x){
			tmp_date = new Date(startdate.getTime());
			tmp_date.setDate(tmp_date.getDate() + x);
			return tmp_date.toLocaleDateString();
		}
	});

	// create the Y axis
	var y_axis = new Rickshaw.Graph.Axis.Y( {
        graph: graph,
        orientation: 'left',
		// default format for numbers
        tickFormat: Rickshaw.Fixtures.Number.formatKMBT,
        element: document.getElementById('y_axis'),
	} );

	// create legend with topics
	var legend = new Rickshaw.Graph.Legend( {
        element: document.querySelector('#legend'),
        graph: graph
	} );

	// enable toggling on series
	var toggling = new Rickshaw.Graph.Behavior.Series.Toggle({
		graph: graph,
		legend: legend
	});

	// show detail on hover
	var hoverDetail = new Rickshaw.Graph.HoverDetail({
		graph: graph,
		// show formatted day for x
		xFormatter: function(x) { 
			tmp_date = new Date(startdate.getTime());
			tmp_date.setDate(tmp_date.getDate() + x);
			return tmp_date.toLocaleDateString();
		},
		yFormatter: function(y) { return (y*100).toFixed(2) + "%"; }
	});

	// render the graph
	graph.render();

	// experimental change from area to line and backwards
	$('#change').on('submit', function(){
		graph.setRenderer(graph.renderer === "line" ? "area" : "line");
		graph.render();
		return false;
	});
	
	
});