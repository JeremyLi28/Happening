# Happening
![screenshot](https://raw.githubusercontent.com/JeremyLi28/Happening/master/Happening/public/images/screenshot.png)

## Intro

Realtime Tweets Sentiment Analysis Application built on [Apache Spark](http://spark.apache.org), [Play Framework](https://www.playframework.com) and [AngularJs](https://angularjs.org)

## Basic Design

+ Platform: Spark clustr with 2 AWS EC2 machines, 1 nanmenode and 1 datanode
+ Data source: Twitter streaming API
+ Realtime sentiment analysis: Spark
+ Backend: Play/Akka
+ Frontend: AngularJs

## Requirements

Java8 & sbt

## How to run

In Happening/Happening directory, run `sbt run`, then visit [localhost:9000](http://localhost:9000) in your browser.



