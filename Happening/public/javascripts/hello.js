var app = angular.module("Happening", ['leaflet-directive']);

app.factory('Twitter', function($http) {

  var ws = new WebSocket("ws://localhost:9000/ws");

  var twitterService = {
    tweets: [],
    query: function (query) {
      ws.send(JSON.stringify({query: query}));
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

});