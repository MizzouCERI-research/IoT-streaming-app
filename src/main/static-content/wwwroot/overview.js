var numUsers = 2;

window.onload= function(){
	
	var chart = new CanvasJS.Chart("chartContainer", {
		title: {
			text: "Overview of Emotion Measurements"
		},
		axisY: {
			title: "Emotion level",
			suffix: ""
		},
		data:[]
	});		
	
	var updateUserNum = function (numUser){
		for (var i=0; i<numUser; i++) {
			if(i % 2 ==0){
				chart.options.data.push(
					{
						type: "column",	
						showInLegend: true,
						name: "user"+i,
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
					}			
				);
			}else{
				chart.options.data.push(
					{
						type: "column",	
						showInLegend: true,
						name: "user"+i,
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
					}			
				);
			}	
		};		
	};
	
	var updateData = function(resource, secondsAgo, callback) {
	    // Fetch data from our data provider
	    provider.getData(resource, secondsAgo, function(newData) {
	      // Store the data locally
	      data.addNewData(newData);
//	      
//	      // Remove data that's outside the window of data we are displaying. This
//	      // is unnecessary to keep around.
//	      data.removeDataOlderThan((new Date()).getTime()
//	          - (graph.getTotalDurationToGraphInSeconds() * 1000));
	      if (callback) {
	        callback();
	      }
	    });
	}
	
	var updateChart = function (chart) {
		var barColor, yVal;
		var dps = new Array(6);
		var deviation =0.1;
		var measurements = ["Engagement","Focus","Excitement","Frustration","Stress","Relaxation"];
		var resource = "EEG sensor";
		console.log("I am here2");
	
	//	provider.getData(resource, function(newData){
	//		data.addNewData(newData);
	//		if (callback) {
	//			callback();
	//		}
	//	});	
	
	
		for (var i=0; i< numUsers; i++){
			dps[i] = chart.options.data[i].dataPoints;
			for (var j = 0; j < dps[i].length; j++) {
			
				var max = dps[i][j].y + deviation/3.0;
		    	var min = dps[i][j].y - deviation/3.0;
		        yVal = Math.random() * (max - min) + min;			
			
		        if (yVal <= 0.0){
		        	yVal=0.01;
		        }
		        if (yVal >= 1.0){
		        	yVal=0.99;
		        } 
				barColor = yVal > 0.8 ? "Red" : yVal >= 0.5 ? "Yellow" : yVal < 0.5 ? "Green" : null;
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

	updateUserNum(numUsers);
	
	var resource = "EEG sensor";
	var secondsAgo = 1;
	updateData(resource, secondsAgo);
	
	updateChart(chart);
	
	setInterval(function() {updateChart(chart)}, 1000);





}//window.onload closing parenthesis



/**
 * Provides access to records data.
 */
var MeasurementDataProvider = function() {
  var _endpoint = "http://" + location.host + "/api/GetMeasurements";

  /**
   * Builds URL to fetch the number of records for a given resource in the past
   */
  buildUrl = function(resource, range_in_seconds) {
    return _endpoint + "?resource=" + resource + "&range_in_seconds="
        + range_in_seconds;
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
    getData : function(resource,range_in_seconds,callback) {
      $.ajax({
        url : buildUrl(resource, range_in_seconds)
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
    }

    
    // convert data to overview data required data format
//    toChartData : function() {
//    	
//    	chartData = [];
//    	$.each(data, function (host, values){
//	    	$.each(data, function(measurement, measureData) {
//	    		chartData.push(
//	    			{
//		    			label: measurement,
//		    			y:{} 
//	    			}
//	    		);	
//	    	});
//    	}); 
//    }
  }
}

//var uiHelper = new UIHelper(data, chart);
var data = new MeasurementData();
var provider = new MeasurementDataProvider();
