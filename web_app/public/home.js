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
                var data = {
                route: vm.route,
                graph: vm.gtype,
                sdate: vm.date1,
                edate: vm.date2
            };
            console.log(data);
            $http.post("/view1", data, {headers: {'Content-Type': 'application/json'} })
        .then(function (response) {
            console.log(response.data);
            
            var res = response.data;
            var table = new Array();
            var data2 = new google.visualization.DataTable();
                      // Add columns
                      data2.addColumn('string', 'Origin/Destination');
                      data2.addColumn('number', 'Fisica');
                      data2.addColumn('number', 'PatioCentral');
                      data2.addColumn('number', 'Biblioteca');
                      data2.addColumn('number', 'CentroEstudiante');
                      data2.addColumn('number', 'EdificioTPinero');
                      data2.addColumn('number', 'TownCenter');
                      data2.addColumn('number', 'GimnasioEspada');
                      data2.addColumn('number', 'CITAI');
                      data2.addColumn('number', 'EdificioA');
                      data2.addColumn('number', 'Empresas');
                      data2.addColumn('number', 'Vita');

                      data2.addRow(['Fisica',0,2,3,4,5,6,7,8,9,12,13]);
                        data2.addRow(['PatioCentral',4,0,3,6,4,5,4,6,422,34,55]);
                        data2.addRow(['Biblioteca',11,22,0,44,55,66,77,88,5,3,5]);
                        data2.addRow(['CentroEstudiante',21,11,22,0,4,5,6,7,8,9,7]);
                        data2.addRow(['EdificioTPinero',23,11,12,13,0,15,16,5,6,5,5]);
                        data2.addRow(['TownCenter',1,2,3,4,5,0,7,8,9,10,11]);
                        data2.addRow(['GimnasioEspada',10,9,8,7,6,5,0,3,2,1,22]);
                        data2.addRow(['CITAI',1,2,3,4,5,6,7,0,8,9,11]);
                        data2.addRow(['EdificioA',2,3,4,5,6,7,8,9,0,12,11]);
                        data2.addRow(['Empresas',1,2,3,4,5,6,7,8,9,0,13]);
                        data2.addRow(['Vita',2,3,4,5,6,7,8,9,10,11,0]);
            if (vm.gtype2 == '1'){
                
                      var table2 = new google.visualization.Table(document.getElementById('chart1'));
                      table2.draw(data2, {showRowNumber: true, width: '100%', height: '100%'});
            }
            else if(vm.gtype2 == '2'){
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
            }
            else if (vm.gtype2 == '3'){

            }
            else {
                
            }
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
            var data2 = new google.visualization.DataTable();
                      // Add columns
                      data2.addColumn('string', 'Origin/Destination');
                      data2.addColumn('number', 'Fisica');
                      data2.addColumn('number', 'PatioCentral');
                      data2.addColumn('number', 'Biblioteca');
                      data2.addColumn('number', 'CentroEstudiante');
                      data2.addColumn('number', 'EdificioTPinero');
                      data2.addColumn('number', 'TownCenter');
                      data2.addColumn('number', 'GimnasioEspada');
                      data2.addColumn('number', 'CITAI');
                      data2.addColumn('number', 'EdificioA');
                      data2.addColumn('number', 'Empresas');
                      data2.addColumn('number', 'Vita');

                      data2.addRow(['Fisica',0,2,3,4,5,6,7,8,9,12,13]);
                        data2.addRow(['PatioCentral',4,0,3,6,4,5,4,6,422,34,55]);
                        data2.addRow(['Biblioteca',11,22,0,44,55,66,77,88,5,3,5]);
                        data2.addRow(['CentroEstudiante',21,11,22,0,4,5,6,7,8,9,7]);
                        data2.addRow(['EdificioTPinero',23,11,12,13,0,15,16,5,6,5,5]);
                        data2.addRow(['TownCenter',1,2,3,4,5,0,7,8,9,10,11]);
                        data2.addRow(['GimnasioEspada',10,9,8,7,6,5,0,3,2,1,22]);
                        data2.addRow(['CITAI',1,2,3,4,5,6,7,0,8,9,11]);
                        data2.addRow(['EdificioA',2,3,4,5,6,7,8,9,0,12,11]);
                        data2.addRow(['Empresas',1,2,3,4,5,6,7,8,9,0,13]);
                        data2.addRow(['Vita',2,3,4,5,6,7,8,9,10,11,0]);
            if (vm.gtype2 == '1'){
                
                      var table2 = new google.visualization.Table(document.getElementById('chart2'));
                      table2.draw(data2, {showRowNumber: true, width: '100%', height: '100%'});
            }
        	else if(vm.gtype2 == '2'){
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
        	else if (vm.gtype2 == '3'){

        	}
        	else {
        		
        	}
            return response;
        });
        };
	}]);
})();