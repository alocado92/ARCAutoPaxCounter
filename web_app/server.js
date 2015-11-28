var express = require('express');
var nodemailer = require('nodemailer');
var app = express();
var path = require('path');
var bodyParser = require('body-parser');
var mysql = require('mysql');
var pool  = mysql.createPool({
	connectionLimit : 100,
    host     : 'localhost',
    user     : 'root',
    password : 'Kie2iedu',
    database : 'capstone'
});
var md5 = require('md5');
var hasher = require('./hashandmatch.js');
var hash = new hasher();

var bunyan = require('bunyan');
var log = bunyan.createLogger({
	name: 'ARC AutoPaxCounter',
	streams: [{
        path: './log/login.log',
        // `type: 'file'` is implied
    }]
});
var distance = require('google-distance');

//API Key for on-remote server testing
distance.apiKey = 'AIzaSyCLWYwZfhXxvlaBHRTEEt40KooXr62LuxY';

//API Key for local server testing
/*distance.apiKey = 'AIzaSyC7ZVsNOFln4BjoOK998A2pODHq70QzDOY';
*/
//mysql create pool
var trip = '';
//var pool = mysql.pool;
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ 
   extended: true 
}));
var allowCrossDomain = function(req, res, next) {
	res.header('Access-Control-Allow-Origin', '*');
	res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');
	res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization, Content-Length, X-Requested-With');

	// intercept OPTIONS method
	if ('OPTIONS' == req.method) {
		res.sendStatus(200);
	} else {
		next();
	}
};
app.use(allowCrossDomain);
app.use(express.static(path.join('./public')));


app.get('/user', function (req, res) {
	var check = '';
	distance.get(
  {
     origins: ['San Francisco, CA'],
  destinations: ['San Diego, CA'],
    mode: 'driving',
    units: 'imperial'
  },
  function(err, data) {
    if (err) return console.log(err);
    console.log(data);
    if (data.distanceValue >= 100000){
    	check = 'True';

    }
    else {
    	check = 'False';
    }
    res.sendStatus(check);
});
  
 
});
app.post('/graph1', function (req, res){
	var type = req.body.type;
	var date_begin = req.body.date1;
	var date_end = req.body.date2;
	var route = req.body.route;
	var graph_type ='';

	if(type == 'Origin-Destination Matrix'){
		graph_type = '';
	}
	else if (type == 'Net Passenger Flow'){
		graph_type = '';
	}
	else {
		graph_type = '';
	}

	var query = '';
	
});
app.post('/login', function (req, res){
	
	console.log('wassap');
	var exists = 0;
	var username = req.body.uName;
	var password = req.body.pword;
	var hashed = hash.Hash(password);
	log.info({User: username,Pass: hashed},'successful login detected!');
	pool.getConnection(function(err, connection) {
	  		// Use the connection
	  		connection.query( 'select count(*) as userCount from User where username ="'+username+'" AND password ="'+hashed+'"', function (err, rows) {
	   			//manipulate rows
	   			console.log('Connected to db, expecting a 1 for matched user. Received a: '+rows[0].userCount);
	   			exists = rows[0].userCount;
	   			connection.release();
	  		});
	   		// And done with the connection.
	    });
	if(exists == 1){
		console.log('User session will be created here');
	}	
	console.log('User: '+username +'\n'+'PW: '+hashed);
	res.send({redirect: '/home'});
	
});
app.get('/remind', function (req,res){
	res.sendFile("public/forgot.html", {"root": __dirname});
});
app.get('/newUser', function (req,res){
	res.sendFile("public/add_user.html", {"root": __dirname});
});
app.get('/editUser', function (req,res){
	res.sendFile("public/edit_user.html", {"root": __dirname});
});
app.get('/deleteUser', function (req,res){
	res.sendFile("public/delete_user.html", {"root": __dirname});
});
app.post('/fetch', function (req,res){
	var email = req.body.email;
	var fName = '';
	var lName = '';
	var isAdmin = -1;
	pool.getConnection(function(err, connection) {
	  		// Use the connection
	  		connection.query( 'Select f_name,l_name,(select count(is_admin) from User where email = "'+email+'" AND is_admin = 1) as admin from User where email ="'+email+'"', function (err, rows) {
	   			//manipulate rows
	   			console.log(rows.length);
	   			if(rows.length>0){isAdmin = rows[0].admin;
	   			fName = rows[0].f_name;
	   			lName = rows[0].l_name;
	   			console.log(rows);
	   			console.log(fName);
	   			console.log(lName);
	   			console.log(isAdmin);
	   			res.send({matched: true,email: email, fname: fName, lname: lName, isAdmin: isAdmin});}
	   			else{
	   				console.log('Unmatched query');
	   				res.send({matched: false});
	   			}
	   			
	   			connection.release();
	  		});
	   		// And done with the connection.
	    });
	//data = {email: email, fname: fname, lname: lname, isAdmin: isAdmin};
	
});

app.post('/delete', function (req,res){
	var email = req.body.email;
	console.log('Deleting user with email: '+email);
	var where = {email: email};
	pool.getConnection(function(err, connection) {
	  		// Use the connection
	  		connection.query( "Delete from User where ?",[where], function (err, rows) {
	   			//manipulate rows
	   			
	   			console.log('Delete user was successful');
	   			 
	   			connection.release();

	  		});
	   		// And done with the connection.
	    });
	res.send({redirect: '/home'});
});
app.post('/edit', function (req,res){
	var email = req.body.email;
	console.log('Editing user with email: '+email);
	var admin = req.body.admin;
	var isAdmin = -1;
	if(admin == 'Yes'){
		isAdmin = 1;
	}
	else if(admin =='No'){
		isAdmin = 0;
	}
	var post = {is_admin: isAdmin};
	var where = {email: email};
	var completed = 0;
	pool.getConnection(function(err, connection) {
	  		// Use the connection
	  		connection.query( "Update User SET ? where ?",[post,where], function (err, rows) {
	   			//manipulate rows
	   			
	   			console.log('edited user successful');
	   			 completed =1;
	   			connection.release();
	  		});
	   		// And done with the connection.
	    });
	
		console.log('Done editing user');
		res.send({redirect: '/home'});
	
});
app.post('/add', function (req,res){
	console.log('Adding a new user');
	var email = req.body.email;
	var username = req.body.user;
	var password = req.body.password;
	var fname = req.body.fname;
	var lname = req.body.lname;
	var admin = req.body.admin;
	var company = req.body.company;
	var hashedPass = hash.Hash(password);
	var isAdmin = -1;
	var completed = 0;
	var is_active =1;
	if(admin == 'Yes'){
		isAdmin = 1;
	}
	else if(admin =='No'){
		isAdmin = 0;
	}
	var post = {username: username, password: hashedPass, email: email, f_name: fname, l_name: lname, is_admin: isAdmin, company: company,is_active:is_active };

	pool.getConnection(function(err, connection) {
	  		// Use the connection
	  		connection.query( "INSERT INTO User SET ?",post, function (err, rows) {
	   			//manipulate rows
	   			
	   			console.log('Insert new user successful');
	   			completed =1;
	   			connection.release();
	  		});
	   		// And done with the connection.
	    });
	if(completed ==1){
		console.log('Done adding new user');
		res.send({redirect: '/home'});
	}
});
app.post('/forgot', function (req,res){
	console.log('entered send email server handler: '+req.body.email);
	var email = req.body.email;
	var exists = 0;
	var user = '';
	var tempPass = 'epicMealTime';
	var transporter = nodemailer.createTransport({
    service: 'Gmail',
	    auth: 
	    {
	        user: 'arc.innovations.group@gmail.com',
	        pass: 'AutoPaxCounter'
	    }
	});
	/*pool.getConnection(function(err, connection) {
	  		// Use the connection
	  		connection.query( 'select count(*) as userCount from User where email ="'+email+'"', function (err, rows) {
	   			//manipulate rows
	   			
	   			exists = rows[0].userCount;
	   			connection.release();
	  		});
	   		// And done with the connection (for now...).
	    });
	if (exists == 1){
		//send email with updated credentials
		var hashedPass = hash.Hash(tempPass);
		pool.getConnection(function(err, connection) {
	  		// Use the connection
	  		connection.query( 'update User set password ="'+hashedPass+'" where email ="'+email+'"', function (err, rows) {
	   			//manipulate rows
	   			console.log('updated password for reset email');
	   			connection.release();
	  		});

	   		// And done with the connection (for now...).
	    });*/
	    var mailOptions = {

    from: 'arc.innovations.group@gmail.com', // sender address
    to: 'alexis.figueroa4@upr.edu' , // list of receivers
    subject: 'Your forgotten credentials', // Subject line
    text: "Hi User, your account credentials for the AutoPaxCounter system is as follows. Username: Tester1 and Password = "+tempPass+". Use your username and updated password to access your AutoPaxCounter account." 
    
	};
	transporter.sendMail(mailOptions, function (error, info){
    if(error){
        console.log(error);
    }else{
        console.log('Message sent: ' + info.response);
    }
});
	/*}
	else {
		//error message sent to client
		console.log('Email did not match');
		
	}*/
	
});
app.get('/', function (req,res){
	
	res.sendFile("public/login.html", {"root": __dirname});
});
app.post('/mobile', function (req,res){
	
	console.log(req.body);
	if(req.body){
		res.send('OK');
	}
	var option = req.body.action;
	
	//Parse which transaction mobile is sending
	switch(option){
		case 'create':
			var route = req.body.route;
			var begin_date = req.body.dateTime;
			var t_name = req.body.study;
			var capacity = req.body.capacity;
			var type = req.body.type;
			var para = {vehicle_type: type, start_time: begin_date, name: t_name};
			var query = 'Insert into Trip SET ?';
			var id = 0;
			var r_id = 0;
			pool.getConnection(function(err, connection) {
	  		// Use the connection
	  		connection.query( query,para, function (err, rows) {
	   			//manipulate rows
	   			
	   			console.log('Insert new trip successful');
	   			id = rows.insertId;
	   			console.log(id);

	   			connection.query( 'Select route_ID from Route where route_name = "'+route+'"', function (err, rows) {
	   			//manipulate rows
	   			r_id = rows[0].route_ID;
	   			console.log('fetch route_ID successful ' + r_id);

	   			var query1 = 'Insert into Belongs SET ?';
	  		console.log('trip_ID: '+id +' route_ID: '+ r_id);
	  		var para1 = {trip_ID: id,route_ID: r_id };
	  		connection.query( query1,para1, function (err, rows) {
	   			//manipulate rows
	   			
	   			console.log('Insert new belongs successful');
	   			
	   			
	  		});
	   			
	  		});
	  		});
	  		

	  		//connection.release();
	  		
	   		// And done with the connection.
	   		//res.send('OK');
	   		connection.release();
	    });
		break;
		case 'stop':
		var end_date = req.body.dateTime;
		query = 'update Trip SET ? where ?';
		var t_ID = 0;
		

		pool.getConnection(function(err, connection) {
	  		// Use the connection
	  		connection.query( 'Select trip_ID from Trip where end_time is NULL', function (err, rows) {
	   			//manipulate rows
	   			t_id = rows[0].trip_ID;
	   			var para = {trip_ID: t_id};
	   			console.log('Stop study update successful');
	   			//connection.release();

	   			connection.query( query,[{end_time: end_date},para], function (err, rows) {
	   			//manipulate rows
	   			
	   			console.log('Stop study update successful');
	   			//res.send('OK');
	   			connection.release();
	  		});
	  		});
	  		
	  	});
		break;
		case 'delete':
		//res.send('OK');
		break;
		case 'diagnostic':
		//res.send('OK');
	  		

		break;
		default:
			//console.log('Something went wrong with the options');
			//res.send('OK');
				var passengers =[];
				var stops = [];
				var distances = [];
				var origin_dest = [];
				var insert_rows = [];
				var insert_scans = [];

				for(var i=0; i<req.body.length;i++){
					console.log("req.body[i].entry_lat: "+ req.body[i].entry_lat);
					//passengers.push(req.body[i]);
					distance.get(
					  {
					     origins: [this.req.body[i].entry_lat +','+ this.req.body[i].entry_log],
					  destinations: [ this.req.body[i].exit_lat +','+ this.req.body[i].exit_log],
					    mode: 'driving',
					    units: 'metric'
					  },
					  function(err, data, req.body[i]) {
					    if (err) return console.log(err);
					    console.log(data.distanceValue);
					    var distance = data.distanceValue;
					    var queryval = {entry_latitude: req.body[i].entry_lat, entry_longitude: req.body[i].entry_log,entry_time: req.body[i].entry_time,exit_latitude: req.body[i].exit_lat,exit_longitude: req.body[i].exit_log,exit_time: req.body[i].exit_time, distance: distance};
					    pool.getConnection(function (err,connection){
					    	console.log("Inserting "+ queryval);
					    	//var queryval = {entry_latitude: row.entry_latitude, entry_longitude: row.entry_longitude, entry_time: row.entry_time, exit_latitude: row.exit_latitude, exit_longitude: row.exit_longitude, exit_time: row.exit_time, distance: distance };
					    	connection.query('Insert into Passenger Set ?',queryval, function (err, rows){
					    		console.log("successfully inserted passenger with id: "+rows.insertId);
					    		connection.release();
					    	});
					    });
					    

					});
					
				}
				//console.log("passenger req.body: "+ passengers);
				//console.log("passengers: "+ passengers[0]);
				

				

				
				

				/*pool.getConnection(function(err, connection) {
	  		// Use the connection
		  			console.log(insert_rows);
		  			console.log(insert_scans);

			  		connection.query( 'Insert into Passenger (entry_latitude, entry_longitude, entry_time, exit_latitude, exit_longitude, exit_time) VALUES ?',insert_rows, function (err, rows) {
			   			//manipulate rows
			   			
			   			console.log('Insert new passengers successful');

			   			connection.query( 'Insert into Scan (tag_ID, scan_time) Values ?',insert_scans, function (err, rows) {
			   			//manipulate rows
			   			
				   			console.log('Insert new scans successful');
				   			
				   			connection.release();
			  			});
			   			
			   			
			  		});
	   		// And done with the connection.
	    		});*/

				/*for(var i=0;i<passengers.length;i++){
					distance.get(
					  {
					     origins: [passengers[i].entry_lat +','+ passengers[i].entry_log],
					  destinations: [ passengers[i].exit_lat +','+ passengers[i].exit_log],
					    mode: 'driving',
					    units: 'metric'
					  },
					  function(err, data) {
					    if (err) return console.log(err);
					    console.log(data.distanceValue);
					    distances.push(data.distanceValue);
					    if(i==passengers.length -1){
						//res.send('OK');
					}

					});

				}*/

				/*pool.getConnection(function(err, connection) {

				});*/
	  
	  		
	  		
	}

	
	//console.log("Processed mobile data successfully");
	
	
});
app.get('/home', function (req,res){
	res.sendFile("public/home.html", {"root": __dirname});
	
});
app.get('/admins',function (req,res){
		var query = 'Select * from User';
		pool.getConnection(function(err, connection) {
	  		// Use the connection
	  		connection.query( 'Select * from User', function(err, rows) {
	   			//manipulate rows
	   			console.log("I'm in!");
	  		});
	   		// And done with the connection.
	    	connection.release();
	    	res.sendStatus(200);
	    	// Don't use the connection here, it has been returned to the pool.
});
});
app.post('/view1', function (req,res){
	console.log(req.body);
	var datas = '{"data": [' + '{"name": "1", "IN": 25, "OUT": 24},' + '{"name": "2", "IN": 25, "OUT": 24},' +'{"name": "3", "IN": 25, "OUT": 24}]}';
	console.log("Sending data to graph: "+ datas);
	res.send(datas);
});

var server = app.listen(3000, function () {
  var host = server.address().address;
  var port = server.address().port;
  console.log('Example app listening at http://%s:%s', host, port);
});

