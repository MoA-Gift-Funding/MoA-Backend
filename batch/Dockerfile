FROM amazoncorretto:21

WORKDIR /app

COPY ./batch/build/libs/batch.jar /app/moa-batch.jar

ENV TZ=Asia/Seoul

CMD ["java", "-jar", "moa-batch.jar"]
