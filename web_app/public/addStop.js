 (function() {
	 var app = angular.module("stop_app");
	 
    // Controller function and passing $http service and $scope var.

    	app.controller('stopController',['$http','$window',function($http,$window) {

    		var vm = this;
            vm.name = '';
            vm.stop = [];
            vm.stop_name = [];
    		vm.results = [];
            vm.route_name = '';
            vm.parada = [];
            
        vm.init = function (){
            var data ={foo: ""};
            $http.post("/fetchStops", data, {headers: {'Content-Type': 'application/json'} })
                .then(function (response){
                    for(var i =0; i<response.data.length;i++){
                        vm.parada.push({name: response.data[i].route_name});
                    }
                });
        };

      // calling our submit function.
        vm.submit = function() {
        		var data = {route_name: vm.route_name};

                
            $http.post("/addStop", data, {headers: {'Content-Type': 'application/json'} })
                    .then(function (response) {
                        console.log(response.data.redirect);
                        window.location = response.data.redirect;
                        
                    });
            
        // Posting data to node.js server
        
        
        };
    }]);
    })();