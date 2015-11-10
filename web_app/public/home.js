(function() {
	var homeApp = angular.module('home_app');
	homeApp.controller('homeController',['$http', function($http){
		var vm = this;
		var data = {
                route: vm.route,
                graph: vm.gtype,
                sdate: vm.date1,
                edate: vm.date2
            };
		// calling our create view function.
        vm.createView = function() {
        		data = {
                route: vm.route,
                graph: vm.gtype,
                sdate: vm.date1,
                edate: vm.date2
            };
            console.log(data);
            $http.post("/view1", data, {headers: {'Content-Type': 'application/json'} })
        .then(function (response) {
        	console.log(response.data);
        	var table = new Array();
        	var res = response.data;
        	table.push(['Stop','In','Out']);
        	for (var i=0;i<res.data.length;i++){
	            console.log(res.data[i].name);
              table.push([res.data[i].name.toString(),res.data[i].IN,res.data[i].OUT]);
            }
            console.log(table.toString());
            var graphData = new google.visualization.arrayToDataTable(table);
            var options = {
          
          chart: {
            title: 'Passenger Net Flow by stops',
            subtitle: 'Passenger IN on left, Passenger out on right'
          },
           // Required for Material Bar Charts.
          series: {
            0: { axis: 'IN' }, // Bind series 0 to an axis named 'distance'.
            1: { axis: 'OUT' } // Bind series 1 to an axis named 'brightness'.
          }
        };
      	var chart = new google.visualization.ColumnChart(document.getElementById("chart1"));
        chart.draw(graphData, options);
            return response;
        });
        };
        vm.createView2 = function() {
        		data = {
                route: vm.route2,
                graph: vm.gtype2,
                sdate: vm.date3,
                edate: vm.date4
            };
            console.log(data);
            $http.post("/view1", data, {headers: {'Content-Type': 'application/json'} })
        .then(function (response) {
        	console.log(response.data);
        	
        	var res = response.data;
        	var table = new Array();
        	if(vm.gtype2 == '2'){
        		table.push(['Stop','In','Out']);
        	for (var i=0;i<res.data.length;i++){
	            console.log(res.data[i].name);
              table.push([res.data[i].name.toString(),res.data[i].IN,res.data[i].OUT]);
            }
            console.log(table.toString());
            var graphData = new google.visualization.arrayToDataTable(table);
            var options = {
          
          chart: {
            title: 'Passenger Net Flow by stops',
            subtitle: 'Passenger IN on left, Passenger out on right'
          },
           // Required for Material Bar Charts.
          series: {
            0: { axis: 'IN' }, // Bind series 0 to an axis named 'distance'.
            1: { axis: 'OUT' } // Bind series 1 to an axis named 'brightness'.
          }
        };
      	var chart = new google.visualization.ColumnChart(document.getElementById("chart2"));
        chart.draw(graphData, options);
        	}
            return response;
        });
        };
	}]);
})();