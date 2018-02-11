var numUsers = 6;

window.onload= function(){
	var chart = new CanvasJS.Chart("chartContainer", {
		title: {
			text: "Overview of Emotion Measurements"
		},
		axisY: {
			title: "Emotion level",
			suffix: ""
		},
		data:[
			{
				type: "column",	
				showInLegend: true,
				name: "user1",
				color: "Green",
				yValueFormatString: "0.##",
				indexLabel: "{y}",
				dataPoints: [
					{ label: "Engagement", y: 0.80 },
					{ label: "Focus", y: 0.80 },
					{ label: "Excitement", y: 0.80 },
					{ label: "Frustration", y: 0.40 },
					{ label: "Stress", y: 0.40 },
					{ label: "Relaxation", y: 0.80 }
				]
			},
			{
				type: "column",
				showInLegend: true,
				name: "user2",
				color: "Blue",
				yValueFormatString: "0.##",
				indexLabel: "{y}",
				dataPoints: [
					{ label: "Engagement", y: 0.80 },
					{ label: "Focus", y: 0.80 },
					{ label: "Excitement", y: 0.80 },
					{ label: "Frustration", y: 0.40 },
					{ label: "Stress", y: 0.40 },
					{ label: "Relaxation", y: 0.80 }
				]
			},
			{
				type: "column",
				showInLegend: true,
				name: "user3",
				color: "Green",
				yValueFormatString: "0.##",
				indexLabel: "{y}",
				dataPoints: [
					{ label: "Engagement", y: 0.80 },
					{ label: "Focus", y: 0.80 },
					{ label: "Excitement", y: 0.80 },
					{ label: "Frustration", y: 0.40 },
					{ label: "Stress", y: 0.40 },
					{ label: "Relaxation", y: 0.80 }
				]
			},
			{
				type: "column",	
				showInLegend: true,
				name: "user4",
				color: "Blue",
				yValueFormatString: "0.##",
				indexLabel: "{y}",
				dataPoints: [
					{ label: "Engagement", y: 0.80 },
					{ label: "Focus", y: 0.80 },
					{ label: "Excitement", y: 0.80 },
					{ label: "Frustration", y: 0.40 },
					{ label: "Stress", y: 0.40 },
					{ label: "Relaxation", y: 0.80 }
				]
			},
			{
				type: "column",	
				showInLegend: true,
				name: "user5",
				color: "Green",
				yValueFormatString: "0.##",
				indexLabel: "{y}",
				dataPoints: [
					{ label: "Engagement", y: 0.80 },
					{ label: "Focus", y: 0.80 },
					{ label: "Excitement", y: 0.80 },
					{ label: "Frustration", y: 0.40 },
					{ label: "Stress", y: 0.40 },
					{ label: "Relaxation", y: 0.80 }
				]
			},
			{
				type: "column",	
				showInLegend: true,
				name: "user6",
				color: "Blue",
				yValueFormatString: "0.##",
				indexLabel: "{y}",
				dataPoints: [
					{ label: "Engagement", y: 0.80 },
					{ label: "Focus", y: 0.80 },
					{ label: "Excitement", y: 0.80 },
					{ label: "Frustration", y: 0.40 },
					{ label: "Stress", y: 0.40 },
					{ label: "Relaxation", y: 0.80 }
				]
			}]
		});		
	
	updateChart(chart);

	setInterval(function() {updateChart(chart)}, 1000);
}



var updateChart = function (chart) {
	var barColor, yVal;
	var dps = new Array(numUsers);
	var measurements = ["Engagement","Focus","Excitement","Frustration","Stress","Relaxation"];
	var resource = "EEG sensor";
	console.log("I am here2");
	
	provider.getData(resource, function(newData){
		data.addNewData(newData);
		if (callback) {
			callback();
		}
	});	
	
	
	for (var i=0; i< 6; i++){
		dps[i] = chart.options.data[i].dataPoints;
//		for (var j = 0; j < dps[i].length; j++) {
			
//			var max = dps[i][j].y + deviation/3.0;
//	    	var min = dps[i][j].y - deviation/3.0;
//	        yVal = Math.random() * (max - min) + min;
//	        if (yVal <= 0.0){
//	        	yVal=0.01;
//	        }
//	        if (yVal >= 1.0){
//	        	yVal=0.99;
//	        } 
			//barColor = yVal > 0.8 ? "Red" : yVal >= 0.5 ? "Yellow" : yVal < 0.5 ? "Green" : null;
	        if (j==0 || j==1 || j==2 || j==5){
	        	barColor = yVal <0.7? "Red": chart.options.data[i].color;
	        }
	        else {
	        	barColor = yVal > 0.5 ? "Red": chart.options.data[i].color;
	        }
			dps[i][j] = {label: measurements[j] , y: yVal, color: barColor};
		}
		chart.options.data[i].dataPoints = dps[i]; 
	}
	chart.render();			
}




///**
// * A collection of methods used to manipulate visible elements of the page.
// */
//var UIHelper = function(data, graph) {
//
//	 var activeResource = "EEG sensor";
//	 var running = true;
//   var updateIntervalInMillis = 1000;
//
//  /**
//   * Fetch records from the last second.
//   *
//   * @param {string}
//   *          resource The resource to fetch records for.
//   * @param {number}
//   *          secondsAgo The range in seconds since now to fetch records for.
//   * @param {function}
//   *          callback The callback to invoke when data has been updated.
//   */
//  var updateData = function(resource, callback) {
//    // Fetch data from our data provider
//    provider.getData(resource, function(newData) {
//      // Store the data locally
//      data = newData;
//      
//      if (callback) {
//        callback();
//      }
//    });
//  }
//  
//  var update = function() {
//	  
//	    // Update our local data for the active resource
//	    updateData(activeResource);
//
//	    // Update the graph with our new data, transformed into the data series
//	    // format Flot expects
////	    chart.update(data.toFlotData());
//	    chart.update();
//
//	    // If we're still running schedule this method to be executed again at the
//	    // next interval
////	    if (running) {
////	      //setTimeout(arguments.callee, updateIntervalInMillis);
////	    	chart.update();
////	    }
//	  }
//  
//  return {  
//	    
//	  /**
//	   * Starts updating the graph at our defined interval.
//	   */
//	  start : function() {
////	    var _this = this;
////	    // Load an initial range of data, decorate the page, and start the update polling process.
////	    updateData(activeResource,function() {
//	          // Start our polling update
//	          running = true;
//	          update();
//	          while (running) {
//	    	    	chart.update();
//	    	  }
////	        });
//	  }
//  }
//}
//
/**
 * Provides easy access to records data.
 */
var MeasurementDataProvider = function() {
  var _endpoint = "http://" + location.host + "/api/GetMeasurements";

  /**
   * Builds URL to fetch the number of records for a given resource in the past
   */
  buildUrl = function(resource) {
    return _endpoint + "?resource=" + resource + "&range_in_seconds="
        + 1;
  };

  return {
    /**
     * Set the endpoint to request records with.
     */
    setEndpoint : function(endpoint) {
      _endpoint = endpoint;
    },

    /**
     * Requests new data and passed it to the callback provided. 
     */
    getData : function(resource,callback) {
      $.ajax({
        url : buildUrl(resource, 1)
      }).done(callback);
    }
  }
}

/**
 * Internal representation of data. 
 */
var MeasurementData = function() {

  var data = {};

  return {
    /**
     * @returns {object} The internal representation of record data.
     */
    getData : function() {
      return data;
    },

    /**
     * Merges new data in to our existing data set.
     *
     * @param {object} Record data returned by our data provider.
     */
    addNewData : function(newMeasurementData) {

    	newMeasurementData.forEach(function(record) {

	        // Add individual measurement
	        record.values.forEach(function(measurementValue) {
	          // create a new data series entry for this measurement
	          measureData = 
		          {
		            label : measurementValue.measurement,
		                y : measurementValue.value
		          };
	          
	          // Update the measurement data
	          data[record.host][measurementValue.measurement] = measureData;

        });
      });
    },

    
    // convert data to overview data required data format
/*    toChartData : function() {
    	
    	chartData = [];
    	$.each(data, function (host, values)
	    	$.each(data, function(measurement, measureData) {
	    		chartData.push({
	    			label: measurement,
	    			y: 
	    		})	
	    	}
    	
    	
    	
    	
    }*/
    
    
    
    
    
    
    
    
    
    
    
  }
}


//var chart = new Chart();
//var uiHelper = new UIHelper(data, chart);
var data = new MeasurementData();
var provider = new MeasurementDataProvider();
