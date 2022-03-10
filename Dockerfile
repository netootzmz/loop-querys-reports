FROM openjdk:8-alpine
VOLUME /tmp
EXPOSE 8085
ADD ./target/queries-reports-1.0.jar queries-reports-1.0.jar
ENTRYPOINT ["sh", "-c", "java -jar -Dspring.datasource.url=$DB_URL -Dspring.datasource.username=$DB_USER -Dspring.datasource.password=$DB_PASSWORD /queries-reports-1.0.jar"]