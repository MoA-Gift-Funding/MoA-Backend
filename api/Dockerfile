FROM amazoncorretto:21

WORKDIR /app

COPY ./api/build/libs/api.jar /app/moa-api.jar

ENV TZ=Asia/Seoul

CMD ["java", "-jar", "moa-api.jar"]
