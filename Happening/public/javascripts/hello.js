var app = angular.module("Happening", ['leaflet-directive']);

app.factory('Twitter', function($http, $timeout) {

  var ws = new WebSocket("ws://localhost:9000/ws");

  var twitterService = {
    tweets: [],
    query: function (place) {
      ws.send(JSON.stringify({place: place}));
    }
  };

  ws.onmessage = function(event) {
    $timeout(function() {
      twitterService.tweets = JSON.parse(event.data).statuses;
    });
  };

  return twitterService;
});


app.controller("AppCtrl", function ($scope, leafletData, Twitter) {

  $scope.init = function() {
    leafletData.getMap().then(function (map) {
      $scope.map = map;
      // map.setView([33.643175, -117.841176], 15);
      map.setView([39.5, -96.35],4);
    })
  };

  $scope.tweets = [];
  $scope.markers = [];

  $scope.$watch(
    function() {
      return Twitter.tweets;
    },
    function(tweets) {
      if(tweets == null)
          return;
      $scope.tweets = tweets;

      $scope.markers = tweets.map(function(tweet) {
        // console.log(tweet.coordinates);
        return {
          lng: tweet.coordinates[0],
          lat: tweet.coordinates[1],
          message: tweet.text,
          focus: true
        }
      });
    }
  );

  $scope.search = function () {
    Twitter.query($scope.place);
    $scope.map.fitBounds([[33.654339, -117.859957],[33.632010, -117.822395]]);
  }

});