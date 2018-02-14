window.onload= function(){

	var numUsers = 2;
	var resource = "EEG sensor";
	var secondsAgo = 1;
//	var data=[{"name":"user1","userData":[{"label":"engagement","y":0.88},{"label":"focus","y":0.816},{"label":"excitement","y":0.713},{"label":"frustration","y":0.293},{"label":"stress","y":0.175},{"label":"relaxation","y":0.512}]},
//		      {"name":"user2","userData":[{"label":"engagement","y":0.883},{"label":"focus","y":0.776},{"label":"excitement","y":0.669},{"label":"frustration","y":0.32},{"label":"stress","y":0.2},{"label":"relaxation","y":0.478}]}];
	var data=[];
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
		

	var updateNumBars = function (num, paramChart){
		console.log("I am here 1");

		paramChart.options.data=[];
		for (var i=1; i<num+1; i++) {
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
							{ label: "Engagement", y: {} },
							{ label: "Focus", y: {} },
							{ label: "Excitement", y: {} },
							{ label: "Frustration", y: {} },
							{ label: "Stress", y: {} },
							{ label: "Relaxation", y: {} }
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
							{ label: "Engagement", y: {} },
							{ label: "Focus", y: {} },
							{ label: "Excitement", y: {} },
							{ label: "Frustration", y: {} },
							{ label: "Stress", y: {} },
							{ label: "Relaxation", y: {} }
						]					
					}			
				);
			}	
		};

		chart.render();
	};
	
	var updateData = function(resource, secondsAgo, callback) {
		console.log("I am here 2");

		//var localData;
	    // Fetch data from our data provider
	    provider.getData(resource, secondsAgo, function(newData) {
			console.log("I am here 2.1");

	      // Store the data locally
			dataAll.resetData();
	    	dataAll.addNewData(newData);
//	    	dataAll.removeDataOlderThan((new Date()).getTime() - 1000);
	    	//data=dataAll.getData();
	    	str = JSON.stringify(data);
	        console.log(str);
	        if (callback) {
		       callback();
	        }	      	  
	    });	 	    
	};
	
	var updateChart = function (paramChart , paramData) {
	//	var barColor, yVal;
		var dps = new Array(6);		
		var measurements = ["Engagement","Focus","Excitement","Frustration","Stress","Relaxation"];		
		console.log("I am here 3");
	
		str = JSON.stringify(paramData);
        console.log(str);
		
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

	};
	
	
	/**
	 * Provides access to records data.
	 */
	var MeasurementDataProvider = function() {
		console.log("I am here 4");

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
	};
	
	/**
	 * Internal representation of data. 
	 */
	var MeasurementData = function() {
		console.log("I am here 5");

	  //var localData = [];
	  var dataPerUser={name:{}, resource:{}, timestamp:{}, userData:[]};
	
	  return {
	    /**
	     * @returns {object} The internal representation of record data.
	     */
	    getData : function() {
	      return data;
	    },
	
	    resetData: function() {
	    	data=[];
	    },
	    /**
	     * Merges new data in to our existing data set.
	     *
	     * @param {object} Record data returned by our data provider.
	     */
	    addNewData : function(newMeasurementData) {
			console.log("I am here 5.1");
			
	    	str = JSON.stringify(newMeasurementData);
	        console.log(str);				
	    	
	    	newMeasurementData.forEach(function(record) {
	    		dataPerUser={name:{}, resource:{}, timeStamp:{}, userData:[]};
	        	dataPerUser.timeStamp = record.timeStamp;
	        	dataPerUser.resource = record.resource;
	        	dataPerUser.name = record.host;
	        	
	    		// Add individual measurement
		        record.values.forEach(function(measurementValue) {
		          // create a new data series entry for this measurement
		        	measureData = 
			          {
			            label : measurementValue.measurement,
			                y : measurementValue.value
			          };
		          
		          // Update the measurement data
	
		            dataPerUser.userData.push(measureData);
	//	            data[j].push(measureData);
		        });
		        
		        data.push(dataPerUser);	
		        
	      });    	
//	    	str = JSON.stringify(data);
//	        console.log(str);
	    },
	    
	    removeDataOlderThan : function(currentTimeStamp) {
			console.log("I am here 5.2");

	        // For each measurement
	          $.each(data, function(measurementData) {
	        	  	          
		            // If the data point is older than the provided time
		            if (measurementData.timestamp < currentTimeStamp) {
		              // Remove the timestamp from the data        	  
		              delete measurementData;
		            }
		       });	     
	     }   
	  }
	};
	
	
	var dataAll = new MeasurementData();
	var provider = new MeasurementDataProvider();
	//var uiHelper = new UIHelper(data, chart);
	updateNumBars(numUsers, chart); 
	updateData(resource, secondsAgo, 
			function(){
		updateChart(chart, data);
		});
//	updateData(resource, secondsAgo);
//	updateChart(chart, data);
	
	setInterval(function() {
			
		updateNumBars(numUsers, chart);  	
		updateData(resource, secondsAgo, 
				function(){
			updateChart(chart, data);
			});
	}, 1000);
	



}//window.onload closing parenthesis
