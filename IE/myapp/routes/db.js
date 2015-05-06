var mongoose = require('mongoose');

mongoose.connect("mongodb://localhost:27017/Parkoosh");
db = mongoose.connection;
db.on('error', function(err){
    return console.log("uncaught error: "+ err);
});

db.on('open', function(err) {
    console.log("conected to test db");
    //defining schema
    var carSchema = mongoose.Schema({
        _id: String
    });

    var reserveSchema = mongoose.Schema({
        _id: Number,
        slot_no: Number,
        car_id: String
    });


    var Car = mongoose.model('Cars', carSchema);
    var Reserve = mongoose.model('Reserves', reserveSchema);

});

module.exports = {mongoose: mongoose, carSchema: db.carSchema, reserveSchema: db.reserveSchema}
