FROM amazoncorretto:21

WORKDIR /app

COPY ./build/libs/moa.jar /app/moa.jar

ENV TZ=Asia/Seoul

CMD ["java", "-jar", "moa.jar"]
