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
      twitterService.tweets = JSON.parse(event.data);
      console.log(JSON.parse(event.data))
    });
  };

  return twitterService;
});


app.controller("AppCtrl", function ($scope, leafletData, Twitter, $http) {

  $scope.init = function() {
    leafletData.getMap().then(function (map) {
      $scope.map = map;
      // map.setView([33.643175, -117.841176], 15);
      map.setView([39.5, -96.35],4);
    })
  };

  $scope.tweets = [];
  $scope.markers = [];
  $scope.heat = [];
  $scope.count = 0;

  $scope.$watch(
    function() {
      return Twitter.tweets;
    },
    function(tweets) {
      if(tweets == null || tweets.length==0)
          return;

      tweets.location[0] = parseFloat(tweets.location[0]);
      tweets.location[1] = parseFloat(tweets.location[1]);


      $scope.tweets.push(tweets);

      if($scope.heatLayer) {
        $scope.map.removeLayer($scope.heatLayer);
        tweets.location.push((Math.random() * -0.2) + 0.1 );
        $scope.heat.push(tweets.location);
        $scope.count += 1;
        if($scope.count%10 == 0)
          $scope.heatLayer = L.heatLayer($scope.heat,{minOpacity: 0.5, max: 2.0});
          $scope.map.addLayer($scope.heatLayer);
      }
      $scope.markers.push({
        lng: parseFloat(tweets.location[1]),
        lat: parseFloat(tweets.location[0]),
        message: tweets.text[0],
        focus: true
      });
    }
  );

  $scope.search = function () {
    Twitter.query($scope.place);
    $scope.map.fitBounds([[33.75174787568194, -117.66820907592773],[33.637489243170826, -117.96175003051756]]);
    $http.get("assets/data/irvine_sentiment.json")
      .success(function(data) {
        angular.forEach(data.data, function(d) {
          var tmp = d["geo[coordinates]"];
          tmp.push(d.score);
          $scope.heat.push(tmp);
        });

        $scope.heatLayer = L.heatLayer($scope.heat,{minOpacity: 0.5, max: 2.0});
        $scope.map.addLayer($scope.heatLayer);
      })
      .error(function(data) {
        console.log("Load sentiment data failure");
      });
  }

});