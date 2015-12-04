 (function() {
	 var app = angular.module("route_app");
	 
    // Controller function and passing $http service and $scope var.
    	app.controller('routeController',['$http','$window',function($http,$window) {

    		var vm = this;
            vm.name = '';
            vm.stop = [];
            vm.stop_name = [];
    		vm.results = [];
            vm.route_name = '';
            
      

      // calling our submit function.
        vm.submit = function() {
        		var data = [route_name: vm.route_name];

                
            $http.post("/addRoutes", data, {headers: {'Content-Type': 'application/json'} })
                    .then(function (response) {
                        console.log(response.data.redirect);
                        window.location = response.data.redirect;
                        
                    });
            
        // Posting data to node.js server
        
        
        };
    }]);
    })();



    //ctrl.submit()            ctrl.fetch()             ctrl.add()