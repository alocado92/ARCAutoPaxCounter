var mysql = require('mysql');
var pool  = mysql.createPool({
	connectionLimit : 100,
    host     : 'localhost',
    user     : 'root',
    password : 'Kie2iedu',
    database : 'capstone'
});

exports.pool = pool;