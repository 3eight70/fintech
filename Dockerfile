FROM openjdk:17-jdk-oracle
ENV SPRING_PROFILES_ACTIVE=production
WORKDIR /app
COPY build/libs/fintech-0.0.1-SNAPSHOT.jar /app
CMD ["java", "-jar", "fintech-0.0.1-SNAPSHOT.jar"]