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
            $http.post("/graph1", data, {headers: {'Content-Type': 'application/json'} })
        .then(function (response) {
            console.log('Dimelo: '+response.data);
            
            var res = response.data;
            var stop = res.stops;
            var table = new Array();
            var data2 = new google.visualization.DataTable();
                      // Add columns

            if (vm.gtype2 == '1'){
                  data2.addColumn('string', 'Origin/Destination');
                      for(var d=0;d<stop.length;d++){
                        data2.addColumn('number', stop[d].toString());
                      }
                      
                      /*data2.addColumn('number', 'Fisica');
                      data2.addColumn('number', 'PatioCentral');
                      data2.addColumn('number', 'Biblioteca');
                      data2.addColumn('number', 'CentroEstudiante');
                      data2.addColumn('number', 'EdificioTPinero');
                      data2.addColumn('number', 'TownCenter');
                      data2.addColumn('number', 'GimnasioEspada');
                      data2.addColumn('number', 'CITAI');
                      data2.addColumn('number', 'EdificioA');
                      data2.addColumn('number', 'Empresas');
                      data2.addColumn('number', 'Vita');*/
                      for(var e=0;e<stop.length;e++){
                        var row = [stop[e].toString()];
                        for(var f=1;f<stop.length+1;f++){
                          //row.push(0);
                          if(typeof res.data[f-1] != 'undefined'){
                            if(stop[e] == res.data[f-1].origin ){
                              for(var x=1; x<stop.length;x++){
                                if(data2.getColumnLabel(x) == res.data[f-1].dest){
                                  row.push(res.data[f-1].count);
                                }
                                else{
                                  row.push(0);
                                }
                              }
                            }
                            
                          }
                          else{
                            row.push(0);
                          }
                        }
                        data2.addRow(row);
                      }
                      /*data2.addRow(['Fisica',0,2,3,4,5,6,7,8,9,12,13]);
                        data2.addRow(['PatioCentral',4,0,3,6,4,5,4,6,422,34,55]);
                        data2.addRow(['Biblioteca',11,22,0,44,55,66,77,88,5,3,5]);
                        data2.addRow(['CentroEstudiante',21,11,22,0,4,5,6,7,8,9,7]);
                        data2.addRow(['EdificioTPinero',23,11,12,13,0,15,16,5,6,5,5]);
                        data2.addRow(['TownCenter',1,2,3,4,5,0,7,8,9,10,11]);
                        data2.addRow(['GimnasioEspada',10,9,8,7,6,5,0,3,2,1,22]);
                        data2.addRow(['CITAI',1,2,3,4,5,6,7,0,8,9,11]);
                        data2.addRow(['EdificioA',2,3,4,5,6,7,8,9,0,12,11]);
                        data2.addRow(['Empresas',1,2,3,4,5,6,7,8,9,0,13]);
                        data2.addRow(['Vita',2,3,4,5,6,7,8,9,10,11,0]);*/
                      var table2 = new google.visualization.Table(document.getElementById('chart1'));
                      table2.draw(data2, {showRowNumber: true, width: '100%', height: '100%'});
            }
            else if(vm.gtype2 == '2'){
                table.push(['Stop','Origin','Destination']);
            for (var i=0;i<res.data.length;i++){
                console.log(res.data[i].stop);
              table.push([res.data[i].stop.toString(),res.data[i].origin,res.data[i].destination]);
            }
            console.log(table.toString());
            var graphData = new google.visualization.arrayToDataTable(table);
            var options = {
          
          chart: {
            title: 'Passenger Net Flow by stops',
            subtitle: 'Passenger Origin on left, Passenger Destination on right'
          },
           // Required for Material Bar Charts.
          series: {
            0: { axis: 'Origin' }, // Bind series 0 to an axis named 'distance'.
            1: { axis: 'Destination' } // Bind series 1 to an axis named 'brightness'.
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
            $http.post("/graph1", data, {headers: {'Content-Type': 'application/json'} })
        .then(function (response) {
        	console.log(response.data);
        	
        	var res = response.data;
          var stop = res.stops;
        	var table = new Array();
            var data2 = new google.visualization.DataTable();
                     
            if (vm.gtype2 == '1'){
                      data2.addColumn('string', 'Origin/Destination');
                      for(var d=0;d<stop.length;d++){
                        data2.addColumn('number', stop[d].toString());
                        console.log('Columns: '+stop[d].toString());
                      }
                      
                      
                      for(var e=0;e<stop.length;e++){
                        var row = [stop[e].toString()];
                        for(var f=1;f<stop.length+1;f++){
                          //row.push(0);
                          if(typeof res.data[f-1] != 'undefined'){
                            if(stop[e] == res.data[f-1].origin ){
                              for(var x=1; x<stop.length;x++){
                                if(data2.getColumnLabel(x) == res.data[f-1].dest){
                                  row.push(res.data[f-1].count);
                                }
                                else{
                                  row.push(0);
                                }
                              }
                            }
                            
                          }
                          else{
                            row.push(0);
                          }
                        }
                        data2.addRow(row);
                      }
                      var table2 = new google.visualization.Table(document.getElementById('chart2'));
                      table2.draw(data2, {showRowNumber: true, width: '100%', height: '100%'});
            }
        	else if(vm.gtype2 == '2'){
                table.push(['Stop','Origin','Destination']);
            for (var i=0;i<res.data.length;i++){
                console.log(res.data[i].stop);
              table.push([res.data[i].stop.toString(),res.data[i].origin,res.data[i].destination]);
            }
            console.log(table.toString());
            var graphData = new google.visualization.arrayToDataTable(table);
            var options = {
          
          chart: {
            title: 'Passenger Net Flow by stops',
            subtitle: 'Passenger Origin on left, Passenger Destination on right'
          },
           // Required for Material Bar Charts.
          series: {
            0: { axis: 'Origin' }, // Bind series 0 to an axis named 'distance'.
            1: { axis: 'Destination' } // Bind series 1 to an axis named 'brightness'.
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