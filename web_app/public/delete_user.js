(function() {
	 var app = angular.module('delete_app');
	 
    // Controller function and passing $http service and $scope var.
    	app.controller('deleteController',['$http','$window',function($http,$window) {

    		var vm = this;
            //var vm.canShow = false;
    		
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
            if((response.data.isAdmin == 1)){vm.isAdmin = 'Yes';}
            else {vm.isAdmin = 'No';}
            
            vm.email = response.data.email;
            if(response.data){vm.canShow = true;}
            else{$window.alert('Email address provided does not belong to a registered user');}
            
            return response;
        });
        
        };
        vm.deleteUser = function() {
        		var data2 = {
                email: vm.email
            };
        // Posting data to node.js server
        $http.post("/delete", data2, {headers: {'Content-Type': 'application/json'} })
        .then(function (response) {
        	
            
            console.log('deleted user');
            
            console.log(response.data.redirect);
        	window.location = response.data.redirect;
            
            return response;
        });
        
        };
    }]);
    })();