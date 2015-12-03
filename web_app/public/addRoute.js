 (function() {
	 var app = angular.module('route_app',[]);
	 
    // Controller function and passing $http service and $scope var.
    	app.controller('routeController',['$http','$window',function($http,$window) {

    		var vm = this;
            vm.name = '';
            vm.stops = [];
            vm.stop_name = [];
    		vm.results = [];
            var data = {
                start_time: vm.start_time,
                end_time: vm.end_time
            };
      vm.fetch = function(){

                        data1 = {data: 0};
                            $http.post("/fetchRoute", data1, {headers: {'Content-Type': 'application/json'} })
                    .then(function (response) {
                        
                        for(var i=0;i<response.data.stops.length; i++){
                                vm.stop.push({name: response.data.stops[i].name, lat: response.data.stops[i].latitude, long: response.data.stops[i].longitude, num : response.data.stops[i].id});
                        }
                        vm.canShow = true;
                        
                    });
                };


        vm.add = function(){
            if(vm.stop_name.indexOf(vm.name) < 0){
                vm.stop_name.push(vm.name);
            }
            else{
                $window.alert('That stop name is already stored the list or a name was provided that does not represent a valid stop. Please check your submission.');
            }
            
        };

      // calling our submit function.
        vm.submit = function() {
        		var data = [];

                for(var i=0; i<vm.stop_name.length; i++){
                    for( var j=0; j<vm.stop.length; j++){
                        if(vm.stop_name[i] == vm.stop[j].name){
                            data.push({stop_name: vm.stop[j].name, stop_ID: vm.stop[j].num});
                            break;
                        }
                    }
                }
                data1 = {stops: data, name: vm.route_name}
            $http.post("/addRoute", data1, {headers: {'Content-Type': 'application/json'} })
                    .then(function (response) {
                        console.log(response.data.redirect);
                        window.location = response.data.redirect;
                        
                    });
            
        // Posting data to node.js server
        
        
        };
    }]);
    })();



    //ctrl.submit()            ctrl.fetch()             ctrl.add()