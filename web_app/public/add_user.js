 (function() {
	 var app = angular.module('add_app');
	 
    // Controller function and passing $http service and $scope var.
    	app.controller('addController',['$http',function($http) {

    		var vm = this;

    		
            var data = {
                email: vm.email,
                user: vm.user,
                password: vm.pword,
                fname: vm.fname,
                lname: vm.lname,
                company: vm.company,
                admin: vm.admin
            };
      

      // calling our submit function.
        vm.addUser = function() {
        		var data = {
                email: vm.email,
                user: vm.user,
                password: vm.pword,
                fname: vm.fname,
                lname: vm.lname,
                company: vm.company,
                admin: vm.admin
            };
        // Posting data to node.js server
        $http.post("/add", data, {headers: {'Content-Type': 'application/json'} })
        .then(function (response) {
        	
            
            console.log('added new user');
            console.log('Win');
            console.log(response.data.redirect);
        	window.location = response.data.redirect;
            
            return response;
        });
        
        };
    }]);
    })();