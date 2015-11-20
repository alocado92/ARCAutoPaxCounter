(function() {
	 var app = angular.module('edit_app');
	 
    // Controller function and passing $http service and $scope var.
    	app.controller('editController',['$http',function($http) {

    		var vm = this;
            var vm.canShow = false;
    		
            var data = {
                email: vm.email,
                //password: vm.pword,
                admin: vm.admin
            };
      

      // calling our submit function.
      vm.fetchUser = function() {
                var data1 = {
                
                //password: vm.pword,
                
                email: vm.email
            };
        // Posting data to node.js server
        $http.post("/fetch", data1, {headers: {'Content-Type': 'application/json'} })
        .then(function (response) {
            
            
            console.log('fetching a user');
            
            vm.fname = response.data.fname;
            vm.lname = response.data.lname;
            vm.isAdmin = response.data.isAdmin;
            vm.email = response.data.email;
            vm.canShow = true;
            return response;
        });
        
        };
        vm.editUser = function() {
        		var data2 = {
                email: vm.email,
                //password: vm.pword,
                
                admin: vm.admin
            };
        // Posting data to node.js server
        $http.post("/edit", data2, {headers: {'Content-Type': 'application/json'} })
        .then(function (response) {
        	
            
            console.log('edited user');
            
            console.log(response.data.redirect);
        	window.location = response.data.redirect;
            
            return response;
        });
        
        };
    }]);
    })();