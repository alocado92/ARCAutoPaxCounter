(function() {
	 var app = angular.module('trip_app');
	 
    // Controller function and passing $http service and $scope var.
    	app.controller('tripController',['$http','$window',function($http,$window) {

    		var vm = this;
            //var vm.canShow = false;
    		
            
      

      // calling our submit function.
      vm.fetchTrip = function() {
                var data1 = {
                
                //password: vm.pword,
                
                name: vm.name
            };
        // Posting data to node.js server
        $http.post("/fetch_trip", data1, {headers: {'Content-Type': 'application/json'} })
        .then(function (response) {
            
            
            console.log('fetching a user');
            
            
            if(response.data.matched){
                vm.canShow = true;
                    vm.name = response.data.name;
                    vm.start = response.data.start;
                    vm.id = response.data.id;
                   
                }
            else{$window.alert('There are no trips with such name, please enter another trip name');}
            return response;
        });
        
        };
        vm.editTrip = function() {
        		var data2 = {
                name: vm.new_name,
                id: vm.id
                
            };
        // Posting data to node.js server
        $http.post("/edit_trip", data2, {headers: {'Content-Type': 'application/json'} })
        .then(function (response) {
        	
            
            console.log('edited trip successfully');
            
            console.log(response.data.redirect);
        	window.location = response.data.redirect;
            
            return response;
        });
        
        };
    }]);
    })();