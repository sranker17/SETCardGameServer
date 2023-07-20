FROM openjdk:11
EXPOSE 8080
RUN mkdir /opt/SET
WORKDIR /opt/SET
ADD target/set-card-game-server.jar set-card-game-server.jar
ENTRYPOINT ["java","-jar","./set-card-game-server.jar"]