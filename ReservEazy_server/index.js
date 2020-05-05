// This file is part of ReservEazy.

//     ReservEazy is free software: you can redistribute it and/or modify
//     it under the terms of the GNU General Public License as published by
//     the Free Software Foundation, either version 3 of the License, or
//     (at your option) any later version.

//     ReservEazy is distributed in the hope that it will be useful,
//     but WITHOUT ANY WARRANTY; without even the implied warranty of
//     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//     GNU General Public License for more details.

//     You should have received a copy of the GNU General Public License
//     along with ReservEazy.  If not, see <https://www.gnu.org/licenses/>.


var firebase = require('firebase');
var admin = require('firebase-admin');

//////////////////
//
//
// Notifications
//
//
//////////////////
var serviceAccount = require('./reserveazy-firebase-adminsdk-se19t-bf9b5cfa68.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://reserveazy.firebaseio.com"
});

var token = "DEVICE_TOKEN";

var payload = {
  notification: {
    title: "Time to leave your house!",
    body: "Your slot is in 15 minutes."
  }
};

var options = {
  priority: "high",
  timeToLive: 60 * 60 * 24
};

function sendNotification(token_new){
  admin.messaging().sendToDevice(token_new, payload, options)
  .then(function(response){
    console.log("Successful", response)
  })
  .catch(function(response){
    console.log('Error', response)
  });
}


///////////////////////
//
//
// Database - Deleting
//
//
///////////////////////



var config = {
    apiKey: "API-KEY",
    authDomain: "reserveazy.firebaseapp.com",
    databaseURL: "https://reserveazy.firebaseio.com",
    storageBucket: "reserveazy.appspot.com"
  };
firebase.initializeApp(config);


// Database

var database = firebase.database();
var numOfSeconds = 24 * 60 * 60 * 1000;
var starCountRef = database.ref().child('/customer_bookings');


setInterval(() => {
    starCountRef.on('value', function(snapshot) {
        var reqValue;
        reqValue = snapshot.val();
        deleteOldPosts(reqValue);
    });
}, numOfSeconds);

function deleteOldPosts(slots){
    var entries = Object.entries(slots);
    var values = Object.values(entries);
    for(var i = 0; i < values.length; i++){
      var currVal = Object.values(values[i][1]);
      var currKey = Object.keys(values[i][1]);
      for(var j = 0; j < currVal.length; j++){
        var currDate = currVal[j].date;
        if((new Date() - new Date(currDate)) > 2 * numOfSeconds){
          var addressStr = '/customer_bookings/' + values[i][0] + "/" + currKey[j]
          console.log(addressStr);
          firebase.database().ref(addressStr).remove();
        }
        if(new Date() - new Date(currDate) < numOfSeconds){
          var d = new Date();
          s = currVal[j].slot;
          // console.log(s);
          parts = s.match(/(\d+)\:(\d+)(\w+)/);
          // console.log(parts);
          hours = /am/i.test(parts[3]) ? parseInt(parts[1], 10) : parseInt(parts[1], 10) + 12;
          minutes = parseInt(parts[2], 10);

          d.setHours(hours, minutes,0,0); // As suggested by @RobG
          setTimeout(()=>{
            var customers = database.ref().child('/Customers');
            var token = Object.values(customers)[i].notification;
            sendNotification(token)
          }, d - new Date());
        }
      }
    }
}


var merchants = database.ref().child('/merchant_user_detail');
setInterval(() => {
  merchants.on('value', function(snapshot) {
    var merchantBookings = snapshot.val();
    deletePrevBookings(merchantBookings);
  })
}, numOfSeconds);


function deletePrevBookings(log){
  var entries = Object.entries(log);
  var keys = Object.keys(log);
  for(var i = 0; i < entries.length; i++){
    var currObj = keys[i];
    firebase.database().ref('/merchant_user_detail/' + keys[i] + '/day_0').remove();
    log[currObj]['day_0'] = log[currObj]['day_1'];
    log[currObj]['day_1'] = log[currObj]['day_2']
    delete log[currObj]['day_2'];
    console.log(log[currObj]);
    firebase.database().ref('/merchant_user_detail/' + keys[i]).set(log[currObj]);

  }
}
