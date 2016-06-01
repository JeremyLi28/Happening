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
      console.log(event.data);
      twitterService.tweets = JSON.parse(event.data).statuses;
    });
  };

  return twitterService;
});


app.controller("AppCtrl", function ($scope, leafletData, Twitter) {

  $scope.init = function() {
    leafletData.getMap().then(function (map) {
      $scope.map = map;
      map.setView([33.643175, -117.841176], 15);
    })
  };

  $scope.tweets = [];

  $scope.$watch(
    function() {
      return Twitter.tweets;
    },
    function(tweets) {
      $scope.tweets = tweets;
    }
  );

  $scope.search = function () {
    Twitter.query($scope.place);
  }

});