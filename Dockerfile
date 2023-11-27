# TODO: ask andrew
# ? where does this image actually come from? I thought it would be
# ? https://hub.docker.com/_/adoptopenjdk/tags?page=1&name=openjdk16, but I can't find an "openjdk16"
# ? specific tag
# ! actually it seems to be this:
# ? https://hub.docker.com/layers/adoptopenjdk/openjdk16/x86_64-alpine-jre-16.0.1_9/images/sha256-ad16db09e28e91d17412eaf8ec021f5b7e2db3c6a05d80185f47e8e3f3802226?context=explore
# ? why pick this specific image?
FROM adoptopenjdk/openjdk16:alpine-jre

COPY build/libs/matrix-animator-api-0.0.1-SNAPSHOT-plain.jar /app.jar

CMD ["java", "-jar", "app.jar"]
