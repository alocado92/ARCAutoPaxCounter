 (function() {
	 var app = angular.module('export_app');
	 
    // Controller function and passing $http service and $scope var.
    	app.controller('exportController',['$http','$window',function($http,$window) {

    		var vm = this;

    		
            var data = {
                start_time: vm.start_time,
                end_time: vm.end_time
            };
      

      // calling our submit function.
        vm.export = function() {
        		var data = {
                start_time: vm.start_time,
                end_time: vm.end_time
            };
            if(vm.start_time && vm.end_time && (vm.start_time < vm.end_time )){
                $http.post("/download", data, {headers: {'Content-Type': 'application/json'} })
                    .then(function (response) {
                        
                        console.log(response.data);
                        //console.log('email');
                        
                        return response;
                    });
            }
            else{
                $window.alert('Please make sure both time input forms are set and start date is earlier than end date');
            }
        // Posting data to node.js server
        
        
        };
    }]);
    })();