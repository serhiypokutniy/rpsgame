(docker-compose down && docker-compose build && docker-compose up&)
(cd spsgame;./gradlew -stop && ./gradlew bootRun&)
(cd frontend;npm install && ng serve)