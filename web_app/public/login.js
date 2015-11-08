 (function() {
	 var app = angular.module('app');
	 
    // Controller function and passing $http service and $scope var.
    	app.controller('postController',['$http',function($http) {

    		var vm = this;

    		

      // create a blank object to handle form data.
      	var data = {
                uName: vm.username,
                pword: vm.password
            };
        vm.user = {};

      // calling our submit function.
        vm.submitForm = function() {
        		var data = {
                uName: vm.username,
                pword: vm.password
            };
        // Posting data to node.js server
        $http.post("/login", data, {headers: {'Content-Type': 'application/json'} })
        .then(function (response) {
        	
            window.location = response.data.redirect;
            console.log('Win');
        	console.log(response.data.redirect);
            return response;
        });
        
        };
    }]);
    })();