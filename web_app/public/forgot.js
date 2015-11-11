 (function() {
	 var app = angular.module('forgot_app');
	 
    // Controller function and passing $http service and $scope var.
    	app.controller('forgotController',['$http',function($http) {

    		var vm = this;

    		
            var data = {
                email: vm.email
            };
      

      // calling our submit function.
        vm.forgot = function() {
        		var data = {email: vm.email};
        // Posting data to node.js server
        $http.post("/forgot", data, {headers: {'Content-Type': 'application/json'} })
        .then(function (response) {
        	
            
            console.log('email');
        	
            return response;
        });
        
        };
    }]);
    })();