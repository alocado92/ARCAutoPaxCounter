var crypto = require('crypto');

function hashandmatch(){
	//create object. Empty constructor
};

hashandmatch.prototype.Hash = function(text){
	if(text == null || text === null){
		console.trace();
	}
	else{
		var hash = crypto.createHash('sha512').update(text).digest('base64');
		return hash;
	}	
};

hashandmatch.prototype.Match = function(text, hash){
	if(text == null || text === null || hash == null || hash === null){
		console.trace();
	}
	else{
		var hashedtext = this.Hash(text);
		if(hashedtext === hash){
			return true;
		}
		else{
			return false;
		}
	}
};

module.exports = hashandmatch;