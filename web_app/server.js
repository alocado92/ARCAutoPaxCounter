var express = require('express');
var nodemailer = require('nodemailer');
var app = express();
var path = require('path');
var bodyParser = require('body-parser');
var mysql = require('mysql');
var geolib = require('geolib');
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
var jsonfile = require('jsonfile');
var util = require('util');
var bunyan = require('bunyan');
var log = bunyan.createLogger({
	name: 'ARC AutoPaxCounter',
	streams: [{
        path: './log/login.log',
        // `type: 'file'` is implied
    }]
});
var distance = require('google-distance');

var session = require('express-session');

//API Key for on-remote server testing
distance.apiKey = 'AIzaSyCLWYwZfhXxvlaBHRTEEt40KooXr62LuxY';

//API Key for local server testing
/*distance.apiKey = 'AIzaSyC7ZVsNOFln4BjoOK998A2pODHq70QzDOY';
*/
//mysql create pool
//var trip = '';
//var pool = mysql.pool;
app.use(session({
  cookieName: 'session',
  secret: 'capstone alexis nestor xandel cristina rosedany',
  duration: 30 * 60 * 1000,
  activeDuration: 5 * 60 * 1000,
}));

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ 
   extended: true 
}));
var allowCrossDomain = function(req, res, next) {
	//res.header('Access-Control-Allow-Origin', '*');
	//res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');
	//res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization, Content-Length, X-Requested-With');
	//res.setHeader('Content-disposition', 'attachment; filename=data.json');

	// intercept OPTIONS method
	if ('OPTIONS' == req.method) {
		res.sendStatus(200);
	} else {
		next();
	}
};
app.use(allowCrossDomain);
app.use(express.static(path.join('./public')));
app.get('/download', function (req,res){
	res.download( __dirname+'/public/data.json');
});
app.post('/download', function (req,res){
	var start_time = req.body.start_time;
	var end_time = req.body.end_time;
	pool.getConnection(function (err, connection){
		console.log('Start time: '+start_time);
		console.log('End time: '+end_time);

		var query = 'Select entry_time, entry_latitude, entry_longitude, exit_time, exit_latitude, exit_longitude, distance, dest_stop, origin_stop, name , start_time, end_time, route_name from Passenger natural join Takes natural join Trip natural join Belongs natural join Route where (start_time >= "'+start_time+'" AND end_time <= "'+end_time+'") ';
		connection.query(query, function (err, rows){
			var result = [];
			for(var i=0;i<rows.length;i++){
				result.push({entry_time: rows[i].entry_time, entry_latitude: rows[i].entry_latitude, entry_longitude: rows[i].entry_longitude, exit_time: rows[i].exit_time, exit_latitude: rows[i].exit_latitude, exit_longitude: rows[i].exit_longitude, distance: rows[i].distance, dest_stop: rows[i].dest_stop, origin_stop: rows[i].origin_stop, study_name: rows[i].study_name, start_time: rows[i].start_time, end_time: rows[i].end_time, route_name: rows[i].route_name});
			}
			console.log('Results: '+result);
			var file = './public/data.json';
			jsonfile.writeFile(file, result, {spaces: 2}, function(err){
				
				res.send({redirect: '/download'});
			});
			connection.release();
		});
	});
});
app.get('/user', function (req, res) {
	var check = '';
	distance.get(
  {
     origins: [18.2098309+', '+-67.1399166],
  destinations: [18.2098309+', '+-67.1399136],
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
app.post('/graph2', function (req, res){
	var type = req.body.graph;
	var date_begin = req.body.sdate;
	var date_end = req.body.edate;
	var route = req.body.route;
	console.log('Route: '+route);
	var graph_type ='';

	if(type == '1'){
		//graph_type = '';
		console.log('Route: '+route);

		pool.getConnection(function (err, connection){
			var where_time = ' start_time >= "'+ date_begin+'" AND end_time <= "' + date_end+'")';
			var route1 = route;
			connection.query('Select name from Stop natural join Linked_to natural join Route where ? ORDER By (name)',{route_name: route1.toString()}, function (err, rows){
				var result = [];
				var stops_name =[]
				for(var i=0;i<rows.length;i++){
					var fila = [];
					stops_name.push(rows[i].name);
					/*for(var j=0;rows.length;j++){
						fila.push(0);
					}*/
					//result.push(fila);
				}
				//console.log('Initialized result: '+result);
				console.log('Sdate: '+ date_begin);
				console.log('Edate: '+ date_end);
				console.log('Route name: '+route1);
				connection.query('select passenger_ID from Passenger NATURAL JOIN Takes NATURAL JOIN Trip NATURAL JOIN Belongs NATURAL JOIN Route where start_time >= "'+date_begin+'" AND end_time <= "'+date_end+'" AND route_name= "' +route1+'"',function (err,rows){
						var pass_id = '';
						console.log('Rows length: '+rows);
						for (var i=0; i< rows.length; i++){
							if(i == rows.length-1){
								pass_id += " passenger_ID = '"+rows[i].passenger_ID+"'";
							}
							else{
								pass_id += "passenger_ID = '"+rows[i].passenger_ID + "' OR ";
							}	
						}
						console.log('Content of pass_ID: '+pass_id);
						connection.query('select name from Stop NATURAL JOIN Linked_to NATURAL JOIN Route where route_name ="'+route1+'"',function (err,rows){
							var parada_id = '';
							var parada_id1 = '';
							for (var i=0; i< rows.length; i++){
								if(i == rows.length-1){
									parada_id += " origin_stop = '"+rows[i].passenger_ID+"'";
									parada_id1 += " dest_stop = '"+rows[i].passenger_ID+"'";
								}
								else{
									parada_id += "origin_stop = '"+rows[i].passenger_ID + "' OR ";
									parada_id += "dest_stop = '"+rows[i].passenger_ID + "' OR ";
								}	
							}
							/*console.log('Parada_id: '+parada_id);
							console.log('Parada_id1: '+parada_id1);
							console.log('Pass_id: '+pass_id);*/
							connection.query('SELECT COUNT(origin_stop) as "Origin", origin_stop, dest_stop FROM Passenger WHERE ('+pass_id+')  Group By (dest_stop) ORDER By (origin_stop)', function (err, rows){
								/*for(var a=0;a<stops_name.length;a++){
									for(var b=0; b<stops_name.length;b++){
										if(stops_name[a] == rows[b].origin_stop){
											if(stops_name[b]==rows[b].dest_stop){
												result[a][b] = rows[b].Origin;
											}
										}
									}
								}*/if(typeof rows != 'undefined'){
								console.log('Rows length c: '+rows.length);
								for(var c=0; c<rows.length;c++){
									result.push({count:rows[c].Origin, origin: rows[c].origin_stop, dest: rows[c].dest_stop});
								}
								console.log('Rows: '+ rows.length);
								console.log('Finished result[0]: '+result[0].count);
								res.send({data: result, stops: stops_name});
								connection.release();}
								else{
									console.log('Unmatched query');
									res.send({data: result, stops: stops_name});
								connection.release();
								}
							});

							
						});	
				});
			});
		});

	}
	else if (type == '2'){
		pool.getConnection(function (err, connection){
			connection.query('Select name from Stop natural join Linked_to natural join Route where ?',{route_name: route},function (err,rows){
				var result = [];
				console.log('Size of row: '+rows.length);
				for(var k=0; k<rows.length; k++){
					result.push({stop: rows[k].name, origin: 0, destination: 0});
				}

				var query = "SELECT COUNT(origin_stop) as 'net_origin', origin_stop, COUNT(dest_stop) as 'net_dest', dest_stop FROM Passenger natural join Takes natural join Trip natural join Belongs natural join Route WHERE route_name = '" + route+"' AND start_time >= '"+date_begin+"' AND end_time <= '"+date_end+"' GROUP By origin_stop" ;
				
				connection.query(query, function (err,rows){
					if (rows.length >0){

						console.log('Route name in query: '+route);
						console.log('Rows length: '+rows.length);

						for(var a=0;a<result.length;a++){
							for(var b=0;b<rows.length;b++){
								if(rows[b].origin_stop == result[a].stop){
									result[a].origin += rows[b].net_origin;
									//break;
								}
							}
							for(var c=0;c<rows.length;c++){
								if(rows[c].dest_stop == result[a].stop){
									result[a].destination += rows[c].net_dest;
									//break;
								}
							}
						}
						console.log(result);
						res.send({data: result});
						connection.release();
					}
					else{
						res.send({data: result});
						connection.release();
					}
				});
			});
		});
	}
	else {
		//graph_type = '';
	}

	var query = '';
	
});
app.post('/graph1', function (req, res){
	var type = req.body.graph;
	var date_begin = req.body.sdate;
	var date_end = req.body.edate;
	var route = req.body.route;
	console.log('Route: '+route);
	var graph_type ='';

	if(type == '1'){
		//graph_type = '';
		console.log('Route: '+route);

		pool.getConnection(function (err, connection){
			var where_time = ' start_time >= "'+ date_begin+'" AND end_time <= "' + date_end+'")';
			var route1 = route;
			connection.query('Select name from Stop natural join Linked_to natural join Route where ? ORDER By (name)',{route_name: route1.toString()}, function (err, rows){
				var result = [];
				var stops_name =[]
				for(var i=0;i<rows.length;i++){
					var fila = [];
					stops_name.push(rows[i].name);
					/*for(var j=0;rows.length;j++){
						fila.push(0);
					}*/
					//result.push(fila);
				}
				//console.log('Initialized result: '+result);
				console.log('Sdate: '+ date_begin);
				console.log('Edate: '+ date_end);
				console.log('Route name: '+route1);
				connection.query('select passenger_ID from Passenger NATURAL JOIN Takes NATURAL JOIN Trip NATURAL JOIN Belongs NATURAL JOIN Route where start_time >= "'+date_begin+'" AND end_time <= "'+date_end+'" AND route_name= "' +route1+'"',function (err,rows){
						var pass_id = '';
						console.log('Rows length: '+rows);
						for (var i=0; i< rows.length; i++){
							if(i == rows.length-1){
								pass_id += " passenger_ID = '"+rows[i].passenger_ID+"'";
							}
							else{
								pass_id += "passenger_ID = '"+rows[i].passenger_ID + "' OR ";
							}	
						}
						console.log('Content of pass_ID: '+pass_id);
						connection.query('select name from Stop NATURAL JOIN Linked_to NATURAL JOIN Route where route_name ="'+route1+'"',function (err,rows){
							var parada_id = '';
							var parada_id1 = '';
							for (var i=0; i< rows.length; i++){
								if(i == rows.length-1){
									parada_id += " origin_stop = '"+rows[i].passenger_ID+"'";
									parada_id1 += " dest_stop = '"+rows[i].passenger_ID+"'";
								}
								else{
									parada_id += "origin_stop = '"+rows[i].passenger_ID + "' OR ";
									parada_id += "dest_stop = '"+rows[i].passenger_ID + "' OR ";
								}	
							}
							/*console.log('Parada_id: '+parada_id);
							console.log('Parada_id1: '+parada_id1);
							console.log('Pass_id: '+pass_id);*/
							connection.query('SELECT COUNT(origin_stop) as "Origin", origin_stop, dest_stop FROM Passenger WHERE ('+pass_id+')  Group By (dest_stop) ORDER By (origin_stop)', function (err, rows){
								/*for(var a=0;a<stops_name.length;a++){
									for(var b=0; b<stops_name.length;b++){
										if(stops_name[a] == rows[b].origin_stop){
											if(stops_name[b]==rows[b].dest_stop){
												result[a][b] = rows[b].Origin;
											}
										}
									}
								}*/if(typeof rows != 'undefined'){
								console.log('Rows length c: '+rows.length);
								for(var c=0; c<rows.length;c++){
									result.push({count:rows[c].Origin, origin: rows[c].origin_stop, dest: rows[c].dest_stop});
								}
								console.log('Rows: '+ rows.length);
								console.log('Finished result[0]: '+result[0].count);
								res.send({data: result, stops: stops_name});
								connection.release();}
								else{
									console.log('Unmatched query');
									res.send({data: result, stops: stops_name});
								connection.release();
								}
							});

							
						});	
				});
			});
		});

	}
	else if (type == '2'){
		//graph_type = '';
		console.log('Route: '+route);
		
		pool.getConnection(function (err,connection){
			console.log('Route: '+route);
			console.log('Date Begin: ' +date_begin);
			console.log('Date End: '+date_end);
			var where = ' (route_name= "'+ route.toString() +'" AND start_time >= "'+ date_begin+'" AND end_time <= "' + date_end+'")';
			console.log('Where: '+where);
			var query = 'select distinct dest_stop from Passenger natural join Takes natural join Trip natural join Belongs natural join Route where '+where;
			var route1 = route;
			console.log('Route 1: '+route1);
			connection.query('Select name from Stop natural join Linked_to natural join Route where ?',{route_name: route1.toString()}, function (err, rows){
				var result = [];
				console.log('Size of row: '+rows.length);
				for(var k=0; k<rows.length; k++){
					result.push({stop: rows[k].name, origin: 0, destination: 0});
				}
				console.log('Query: '+ query);
				connection.query(query, function (err, rows){
					console.log('Size of row: '+ rows.length);
				if(rows.length > 0){
					var stops = '';
					for (var i=0; i< rows.length; i++){
						if(i == rows.length-1){
							stops += " dest_stop = '"+rows[i].dest_stop+"'";
						}
						else{
							stops += "dest_stop = '"+rows[i].dest_stop + "' OR ";
						}

					}
					console.log('Content of Stops: '+stops);
					var query2 = 'SELECT COUNT(origin_stop) as "Net_Traffic_Origin", origin_stop, COUNT(dest_stop) as "Net_Traffic_Dest", dest_stop FROM Passenger WHERE ('+stops+') GROUP By (origin_stop)';
					connection.query(query2, function (err, rows){
						console.log('Route name in query2: '+route);
						console.log('Rows length: '+rows.length);

						for(var a=0;a<result.length;a++){
							for(var b=0;b<rows.length;b++){
								if(rows[b].origin_stop == result[a].stop){
									result[a].origin += 1;
									break;
								}
							}
							for(var c=0;c<rows.length;c++){
								if(rows[c].dest_stop == result[a].stop){
									result[a].destination += 1;
									break;
								}
							}
						}
						console.log(result);
						res.send({data: result});
						connection.release();

						
					});
				}
				else{
					res.send({data: result});
				}
			});
			});
		});
	}
	else {
		//graph_type = '';
	}

	var query = '';
	
});


var sess;

app.post('/login', function (req, res){
	sess = req.session;
	console.log('wassap');
	var exists;
	var username = req.body.uName;
	var password = req.body.pword;
	var hashed = hash.Hash(password);
	var is_admin = -1;
	var name='';
	var mail='';
	log.info({User: username,Pass: hashed},'successful login detected!');
	pool.getConnection(function(err, connection) {
	  		// Use the connection
	  		connection.query( 'select count(email) as userCount, f_name, email, is_admin from User where username ="'+username+'" AND password ="'+hashed+'"', function (err, rows) {
	   			//manipulate rows
	   			console.log('Connected to db, expecting a 1 for matched user. Received a: '+rows[0].userCount);
	   			exists = rows[0].userCount;
	   			mail = rows[0].email;
	   			is_admin = rows[0].is_admin;
	   			name = rows[0].f_name;

	   			
	   			console.log(exists);
				if(exists){
					console.log('User session will be created here');
					sess.email = mail;
					sess.is_admin = is_admin;
					sess.fname = name;
					console.log('First name of session: '+sess.fname);
					res.send({redirect: '/home'});
				}
				else{
					//kick out
					console.log('Kick out');
				}
				connection.release();
		  		});
	   		// And done with the connection.
	   		console.log(exists);

	   		
	    });



	console.log('User: '+username +'\n'+'PW: '+hashed);
	
	
});
app.get('/remind', function (req,res){
	res.sendFile("public/forgot.html", {"root": __dirname});

});
app.get('/stops', function (req,res){
	
	if(sess.is_admin == 1){
		res.sendFile("public/addStop.html", {"root": __dirname});
	}
	else if(sess != null && sess.is_admin != 1){
		console.log('You are not admin. Get away.');
	}
	else{
		delete sess;
		res.redirect('/');
	}

});
app.get('/newUser', function (req,res){
	console.log(sess.is_admin);
	if(sess.is_admin == 1){
		res.sendFile("public/add_user.html", {"root": __dirname});
	}
	else if(sess != null && sess.is_admin != 1){
		console.log('You are not admin. Get away.');
	}
	else{
		delete sess;
		res.redirect('/');
	}
});
app.get('/export', function (req,res){
	
	if(sess != null ){
		res.sendFile("public/export.html", {"root": __dirname});
	}
	
	else{
		delete sess;
		res.redirect('/');
	}
});
app.get('/editUser', function (req,res){
	//res.sendFile("public/edit_user.html", {"root": __dirname});
	if(sess != null && sess.is_admin == 1){
		res.sendFile("public/edit_user.html", {"root": __dirname});
	}
	else if(sess != null && sess.is_admin != 1){
		console.log('You are not admin. Get away.');
	}
	else{
		delete sess;
		res.redirect('/');
	}
});
app.get('/editTrip', function (req,res){
	//res.sendFile("public/edit_trip.html", {"root": __dirname});
	if(sess != null && sess.is_admin == 1){
		res.sendFile("public/edit_trip.html", {"root": __dirname});
	}
	else if(sess != null && sess.is_admin != 1){
		console.log('You are not admin. Get away.');
	}
	else{
		delete sess;
		res.redirect('/');
	}
});
app.get('/deleteUser', function (req,res){
	//res.sendFile("public/delete_user.html", {"root": __dirname});
	if(sess != null && sess.is_admin == 1){
		res.sendFile("public/delete_user.html", {"root": __dirname});
	}
	else if(sess != null && sess.is_admin != 1){
		console.log('You are not admin. Get away.');
	}
	else{
		delete sess;
		res.redirect('/');
	}
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
app.get('/logout',function (req,res){
	if(sess != null ){
		req.session = null;
		res.sendFile("public/login.html", {"root": __dirname});
	}
	
	else{
		delete sess;
		res.redirect('/');
	}
});

app.post('/fetch_trip', function (req,res){
	var name = req.body.name;
	var id = -1;
	var start = '';
	pool.getConnection(function(err, connection) {
	  		// Use the connection
	  		connection.query( 'Select trip_ID, name, start_time from Trip where ?',{name: name} ,function (err, rows) {
	   			//manipulate rows
	   			console.log(rows.length);
	   			if(rows.length>0){
	   				id = rows[0].trip_ID;
	   				start = rows[0].start_time;
	   				name = rows[0].name;
	   				
	   			res.send({matched: true,name: name, start: start, id: id});}
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
app.post('/edit_trip', function (req,res){
	var name = req.body.name;
	var id = req.body.id;
	console.log('Editing trip with id: '+id);
	
	
	var post = {name: name};
	var where = {trip_ID: id};
	
	pool.getConnection(function(err, connection) {
	  		// Use the connection
	  		connection.query( "Update Trip SET ? where ?",[post,where], function (err, rows) {
	   			//manipulate rows
	   			
	   			console.log('edited trip successful');
	   			 
	   			connection.release();
	  		});
	   		// And done with the connection.
	    });
	
		console.log('Done editing trip');
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
	   			var transporter = nodemailer.createTransport({
			    service: 'Gmail',
				    auth: 
				    {
				        user: 'arc.innovations.group@gmail.com',
				        pass: 'AutoPaxCounter'
				    }
				});
	   			var mailOptions = {

				    from: 'arc.innovations.group@gmail.com', // sender address
				    to: email , // list of receivers
				    subject: 'Welcome to the ARC AutoPaxCounter Web Experience', // Subject line
				    text: "Hi "+fname+",\n\nYour account credentials for the AutoPaxCounter system are as follows. Username:"+ username+" and Password = "+password+". Use your username and updated password to access your AutoPaxCounter account at http://arcinnovations.ece.uprm.edu:3000/.\n\nBest Regards,\n\nARC Dev Team." 
				    
					};
					transporter.sendMail(mailOptions, function (error, info){
				    if(error){
				        console.log(error);
				    }else{
				        console.log('Message sent');
				        connection.release();
				    }
				});
	   			//connection.release();
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
		//var tempPass = 'epicMealTime';
		var transporter = nodemailer.createTransport({
	    service: 'Gmail',
		    auth: 
		    {
		        user: 'arc.innovations.group@gmail.com',
		        pass: 'AutoPaxCounter'
		    }
		});
		pool.getConnection(function (err, connection){
			var query = 'select username, count(*) as userCount from User where ?';
			var para = {email: email};
			console.log(query+ JSON.stringify(para));
			connection.query(query, para, function (err,rows){
				console.log('Searching for a valid email address. COUNT should be 1. Got: '+rows[0].userCount);

				exists = rows[0].userCount;
				var user = rows[0].username;
				if(exists == 1){
					var tempPass = 'epicMealTime';
					var hashedPass = hash.Hash(tempPass);
					connection.query( 'update User set ? where ?',[{password: hashedPass},{email: email}], function (err, rows) {
		   			//manipulate rows
		   			console.log('updated password for reset email');

		   			var mailOptions = {

					    from: 'arc.innovations.group@gmail.com', // sender address
					    to: email , // list of receivers
					    subject: 'Your forgotten credentials', // Subject line
					    text: "Hi User,\n your account credentials for the AutoPaxCounter system is as follows. Username:"+ user+" and Password = "+tempPass+". Use your username and updated password to access your AutoPaxCounter account at http://arcinnovations.ece.uprm.edu:3000/.\n Best Regards,\n ARC Dev Team." 
					    
						};
						transporter.sendMail(mailOptions, function (error, info){
					    if(error){
					        console.log(error);
					    }else{
					        console.log('Message sent');
					        connection.release();
					    }
					});

		   			
		  		});
				}
			});
		});
	});
app.get('/', function (req,res){
	delete sess;
	res.sendFile("public/login.html", {"root": __dirname});
});
app.get('/addRoute', function (req,res){
	
	
	if(sess != null && sess.is_admin == 1){
		res.sendFile("public/addRoute.html", {"root": __dirname});
	}
	else if(sess != null && sess.is_admin != 1){
		console.log('You are not admin. Get away.');
	}
	else{
		delete sess;
		res.redirect('/');
	}
});

app.post('/mobile', function (req,res){
	
	console.log(req.body);
	/*if(req.body){
		res.send('OK');
	}*/
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

	  		var timequery = 'Select DATE_ADD(start_time, INTERVAL 23 HOUR) as final_date, trip_ID from Trip where end_time is null';
	  			connection.query(timequery, function (err,rows){
//bregar			
					console.log('Dame el row:' + rows[0]);
					if(typeof rows[0] !== 'undefined'){
						var date = rows[0].final_date;
					console.log(date);

					//var time = rows[0].final_time
					var ID = rows[0].trip_ID;
					//var datetime = new Date(Date.parse(JSON.stringify(rows[0].final_date))).addHours(23);
					//console.log(datetime);

					var end_query = 'Update Trip SET ? WHERE ?';
					connection.query(end_query, [{end_time: date},{trip_ID: ID}], function (err,rows){
						connection.query( query,para, function (err, rows) {
	   			//manipulate rows
	   			
			   			console.log('Insert new trip successful');
			   			id = rows.insertId;
			   			console.log(id);
			   			//console.log("date: "+date);
			   			connection.query( 'Select route_ID from Route where (LOWER(route_name) = "'+route+'" OR route_name = "'+route+'")', function (err, rows) {
			   			//manipulate rows
				   			r_id = rows[0].route_ID;
				   			console.log('fetch route_ID successful ' + r_id);

				   			var query1 = 'Insert into Belongs SET ?';
					  		console.log('trip_ID: '+id +' route_ID: '+ r_id);
					  		var para1 = {trip_ID: id,route_ID: r_id };
					  		connection.query( query1,para1, function (err, rows) {
				   			//manipulate rows
				   			/*if(!rows){
				   				res.send('INVALID');
				   			}
				   			else{
				   				res.send('OK');
				   			}*/
				   				console.log('Insert new belongs successful');
				   			
				   			
				  			});
			   			
			  			});
			   			//console("time: "+time);
			   			
			   			
	  				});
			   				
			   			});
					}
					else{
						connection.query( query,para, function (err, rows) {
	   			//manipulate rows
	   			
			   			console.log('Insert new trip successful');
			   			id = rows.insertId;
			   			console.log(id);
			   			//console.log("date: "+date);
			   			connection.query( 'Select route_ID from Route where (LOWER(route_name) = "'+route+'" OR route_name = "'+route+'")', function (err, rows) {
			   			//manipulate rows
				   			r_id = rows[0].route_ID;
				   			console.log('fetch route_ID successful ' + r_id);

				   			var query1 = 'Insert into Belongs SET ?';
					  		console.log('trip_ID: '+id +' route_ID: '+ r_id);
					  		var para1 = {trip_ID: id,route_ID: r_id };
					  		connection.query( query1,para1, function (err, rows) {
				   			//manipulate rows
				   			/*if(!rows){
				   				res.send('INVALID');
				   			}
				   			else{
				   				res.send('OK');
				   			}*/
				   				console.log('Insert new belongs successful');
				   			
				   			
				  			});
			   			
			  			});
			   			//console("time: "+time);
			   			
			   			
	  				});
					}
					
	  				
	  			});
	  		

	  		//connection.release();
	  		
	   		// And done with the connection.
	   		res.send('OK');
	   		connection.release();
	    });
		break;
		case 'verify':
			var r_name = req.body.route;
			pool.getConnection(function(err, connection){
				connection.query('Select route_ID from Route where route_name = "'+r_name+'"',function(err,rows){
					if(!rows||rows.length <1){
						res.send('INVALID');
					}
					else{
						res.send('OK');
					}
					connection.release();
				});
			});
		break;
		case 'edit':
			
			var change_capacity = req.body.capacity;
			var change_study = req.body.study;
			var change_type = req.body.type;
			var time = req.body.dateTime;

			var query = 'Update from Trip SET ? where ?';
			var para = [{vehicle_type: change_type},{name: change_study, start_time: time}];
			pool.getConnection(function (err, connection){
				connection.query(query,para, function (err,rows){
					
					//change vehicle
					res.send('OK');


				});
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
	   			res.send('OK');
	   			connection.release();
	  		});
	  		});
	  		
	  	});
		break;
		case 'delete':
		//res.send('OK');
			var study_name = req.body.study;
			pool.getConnection(function (err,connection){
				console.log('Deleting data related to study: '+ study_name);
				connection.query('Select passenger_ID from Takes natural join Trip where (name = "'+study_name+'")',function (err, rows){
					var pass_ids = '';
					console.log('Passenger ids to be added: '+ rows.toString());
					for(var i=0;i<rows.length;i++){

						if(i == rows.length-1){
							pass_ids += 'passenger_ID = '+rows[i].passenger_ID;
						}
						else{
							pass_ids += "passenger_ID = "+rows[i].passenger_ID + " OR ";
						}
					}
					console.log('Pass_ids: '+pass_ids);
					connection.query('Delete from Passenger where ('+pass_ids+')',function (err,rows){
						console.log('On to delete from Trip with name: '+ study_name);
						connection.query('Delete from Trip where name = "'+study_name+'"', function (err,rows){
							console.log('Delete Trip should be successful');
							res.send('OK');
							connection.release();
						});
					});
				});
			});
		break;
		case 'diagnostic':
		//res.send('OK');
	  		pool.getConnection(function (err,connection){
	  			connection.query('Select * from Trip', function (err, rows){
	  				res.send('OK');
	  				connection.release();
	  			});
	  		});

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
				var loop = 1;
				var lim = req.body.length;
				pool.getConnection(function (err, connection){
				console.log("Request length: "+req.body.length);
				//for(var i=0; i<req.body.length;i++){
					//console.log("req.body[i].entry_lat: "+ req.body[i].entry_lat);
					function next(){
					if(loop < lim){
					var test = req.body[loop];
					console.log("ENtry lat: "+ test.entry_lat);
					//passengers.push(req.body[i]);
					//var il = i;
					var count = 1;
					
					
							var dest_name = '';
							var orig_name = '';
							connection.query('Select stop_ID from Stop natural join Linked_to natural join Route natural join Belongs inner join Trip where end_time is null', function (err, rows){
								//console.log("il: "+ il);
								//test.body[il]
								console.log("test.body.log: "+ test.entry_log);
								
								console.log("Rows after initial query "+rows);
								var ling_ling = '';
								for(var i=0; i<rows.length;i++){
									if(i == rows.length-1){
										ling_ling += " stop_ID = "+rows[i].stop_ID;
									}
									else{
										ling_ling += "stop_ID = "+rows[i].stop_ID + " OR ";
									}
								}
								console.log('ling_ling: '+ling_ling);
								connection.query('select name, stop_latitude, stop_longitude from Stop where ('+ ling_ling+')', function (err, rows){

									console.log("Rows after inner query "+rows);
									if(rows.length < 1){
										console.log('There are no active trips. Please add an active trip in order to register passengers');
									}
									else{
										console.log("test.body.log: "+ test.entry_lat);
										for(var j=0; j<rows.length;j++){
											
											var lat = test.entry_lat;
											var log = test.entry_log;
											var lat1 = rows[j].stop_latitude;
											var log1 = rows[j].stop_longitude;
											var dist = geolib.getDistance(
											    {latitude: lat , longitude: log},
											    {latitude: lat1, longitude: log1}
											);
											console.log("dist: "+dist);
											if(dist <= 45){
												console.log("name: "+ rows[j].name);
												orig_name = rows[j].name;
												break;
											}
										}
										for(var j=0; j<rows.length;j++){
											var dist = geolib.getDistance(
											    {latitude: test.exit_lat, longitude: test.exit_log},
											    {latitude: rows[j].stop_latitude, longitude: rows[j].stop_longitude}
											);
											if(dist <= 45){
												dest_name = rows[j].name;
												break;
											}
										}

										var queryval = {entry_latitude: test.entry_lat, entry_longitude: test.entry_log,entry_time: test.entry_time,exit_latitude: test.exit_lat,exit_longitude: test.exit_log,exit_time: test.exit_time, distance: geolib.getDistance(
										    {latitude: test.entry_lat, longitude: test.entry_log},
										    {latitude: test.exit_lat, longitude: test.exit_log}
										), dest_stop: dest_name, origin_stop: orig_name};
										console.log("Inserting "+ queryval);
								    	//var queryval = {entry_latitude: row.entry_latitude, entry_longitude: row.entry_longitude, entry_time: row.entry_time, exit_latitude: row.exit_latitude, exit_longitude: row.exit_longitude, exit_time: row.exit_time, distance: distance };
								    	connection.query('Insert into Passenger Set ?',queryval, function (err, rows){
								    		var pass_id = rows.insertId;
								    		//console.log("i = "+il);
								    		console.log("successfully inserted passenger with id: "+rows.insertId);
								    		connection.query('Select trip_ID from Trip where end_time is null', function (err,rows){
								    			console.log('Trip ID: '+ rows[0].trip_ID);
								    			console.log('Passenger ID: '+ pass_id);
								    			var relation = {passenger_ID: pass_id, trip_ID: rows[0].trip_ID};
								    			connection.query('Insert into Takes SET ?', relation , function (err, rows){
								    				/*count++;
								    				if(count >= lim){}*/
								    					loop++;
								    				next();
								    			});
								    		});
								    		

								    		
								    		
								    		
								    	});

									}
								});
							});
					    	
						}
						else{
							res.send('OK');
							connection.release();
						}
					}

					//first execution of next
					next();
				//}
				});		  		
	}

	
	
	
	
});
app.get('/home', function (req,res){
	if(sess != null){
		res.sendFile("public/home.html", {"root": __dirname});
	}
	else{
		delete sess;
		res.redirect('/');
	}
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
app.post('/fetchStops', function (req, res){
	pool.getConnection(function (err, connection){
		connection.query('select route_name from Route',function (err,rows){
			res.send(rows);
			connection.release();
		});
	});
});





app.post('/addStop', function (req,res){
		var routename = req.body.route_name.name;
		var stop = req.body.stop_name;
		var latitude = req.body.latitude;
		var longitude = req.body.longitude;
		console.log('Stop to be added:'+stop);
		console.log(routename);
		pool.getConnection(function (err, connection){

			connection.query('select route_ID from Route where ?',{route_name: routename}, function (err,rows){
				var rID = rows[0].route_ID;
				console.log('Found Route with ID: '+ rID);
				connection.query('insert into Stop SET ?',{stop_latitude: latitude, stop_longitude: longitude, name: stop}, function (err, rows){
					var stopID = rows.insertId;

					console.log('Created Stop with ID: ' + stopID);
					connection.query('insert into Linked_to SET ?',{stop_ID: stopID, route_ID: rID}, function (err, rows){
						console.log('Finished linking Route to created stop: YAY');
						res.send({redirect: '/stops'});
						connection.release();
					});

				});
			});
		});
});




app.post('/fetchRoute', function (req ,res){
	pool.getConnection(function (err, connection){
		connection.query('select stop_ID, stop_latitude, stop_longitude, name from Stop',function (err, rows){
			var result = [];
			console.log("Finished with Stop");
			for(var i=0; i< rows.length; i++){
				result.push({name: rows[i].name, latitude: rows[i].stop_latitude, longitude: rows[i].stop_longitude, id: rows[i].stop_ID});
			}
			console.log('Sending Result: ' + JSON.stringify(result));
			res.send({data: JSON.stringify(result)});
			connection.release();
		});
	});
});

app.post('/addRoutes', function (req,res){
	//var stops = req.body.stops;
	var route_name = req.body.route_name;
	//console.log('Stops to be tied to route: '+ JSON.stringify(stops));
	console.log('Route name for new route: '+ route_name);
	pool.getConnection(function (err, connection){

		connection.query('Insert into Route SET ?',{route_name: route_name}, function (err,rows){
			
			console.log('Inserted Route with name: '+route_name +' and ID: '+ rows.insertId);
				
			res.send({redirect: '/home'});
			connection.release();
		});
	});



});

app.get('/dropdown', function (req,res){
	pool.getConnection(function (err,connection){

		connection.query('select route_name from Route', function (err,rows){
			var result ='';

			for(var i=0; i<rows.length; i++){
				if (i < rows.length -1) {
					result+= rows[i].route_name+',';
				}
				else{
					result+= rows[i].route_name+' ';
				}
			}
			res.send(result);
			connection.release();
		});
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

