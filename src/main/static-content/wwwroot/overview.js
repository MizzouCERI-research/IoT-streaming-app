

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
	
	data=[{"name":"user1","userData":[{"label":"engagement","y":0.88},{"label":"focus","y":0.816},{"label":"excitement","y":0.713},{"label":"frustration","y":0.293},{"label":"stress","y":0.175},{"label":"relaxation","y":0.512}]},
		{"name":"user2","userData":[{"label":"engagement","y":0.883},{"label":"focus","y":0.776},{"label":"excitement","y":0.669},{"label":"frustration","y":0.32},{"label":"stress","y":0.2},{"label":"relaxation","y":0.478}]},
			{"name":"user3","userData":[{"label":"engagement","y":0.883},{"label":"focus","y":0.776},{"label":"excitement","y":0.669},{"label":"frustration","y":0.32},{"label":"stress","y":0.2},{"label":"relaxation","y":0.478}]}];
 
	updateNumBars(numUsers, chart);

	//data = updateData(resource, secondsAgo);
	
	updateChart(chart, data);
	
	setInterval(function() {updateChart(chart, data)}, 1000);

}//window.onload closing parenthesis



var updateNumBars = function (num, paramChart){
	for (var i=1; i<=num; i++) {
		if(i % 2 ==0){
			paramChart.options.data.push(
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
			paramChart.options.data.push(
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

var updateData = function(resource, secondsAgo) {
	
	var localData;
    // Fetch data from our data provider
    provider.getData(resource, secondsAgo, function(newData) {
      // Store the data locally
    	dataAll.addNewData(newData);
    	localData = dataAll.getData();
      	str = JSON.stringify(localData);
        console.log(str);
        alert(str); 
    });
    return localData;
}

var updateChart = function (paramChart , paramData) {
//	var barColor, yVal;
	var dps = new Array(6);		
	var measurements = ["Engagement","Focus","Excitement","Frustration","Stress","Relaxation"];		
	console.log("I am here2");

	for (var i=0; i< numUsers; i++){
//		dps[i] = chart.options.data[i].dataPoints;
		var name = paramChart.options.data[i].name;
		
		for (var j=0; j< numUsers; j++){
			if (paramData[j].name == name) {
				paramChart.options.data[i].dataPoints = paramData[j].userData;
			}				
		}
	}
	paramChart.render();			
}


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

  var data = [];
  var dataPerUser={name:{}, userData:[]};

//	var data={};
	
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
    		
    		var user= record.host;

	        // Add individual measurement
	        record.values.forEach(function(measurementValue) {
	          // create a new data series entry for this measurement
	          measureData = 
		          {
		            label : measurementValue.measurement,
		                y : measurementValue.value
		          };
	          
	          // Update the measurement data
	          dataPerUser.name = record.host;
	          dataPerUser.userData.push(measureData);
//	          data[j].push(measureData);
	        });
	        data.push(dataPerUser);	
	        
      });    	
//    	str = JSON.stringify(data);
//        console.log(str);
//        alert(str);    	
    }
  }
}

//var uiHelper = new UIHelper(data, chart);
var numUsers = 3;
var resource = "EEG sensor";
var secondsAgo = 1;
var data;
var dataAll = new MeasurementData();
var provider = new MeasurementDataProvider();
