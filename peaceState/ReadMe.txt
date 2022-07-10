Hello ! 
This is the beginning of the project : we only did the consumer and the producer part. 
The others are in progress. 

Ps: 
1/ Don't forget to start zookeeper : bin/zookeeper-server-start.sh config/zookeeper.properties
2/ and then the kafka broker : bin/kafka-server-start.sh config/server.properties
3/ Create a topic 'peace' if you haven't already : bin/kafka-topics.sh --create --topic peace --bootstrap-server localhost:9092

Now you are ready to run our program.