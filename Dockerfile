FROM amazoncorretto:21.0.5-alpine@sha256:8b16834e7fabfc62d4c8faa22de5df97f99627f148058d52718054aaa4ea3674

ENV LANG en_US.UTF-8
ENV LC_ALL en_US.UTF-8

RUN apk --no-cache add tzdata
ENV TZ=America/Bogota
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
VOLUME /tmp

RUN adduser -D -g '' userApi
USER userApi

ADD ./build/libs/scheme_reactive.jar scheme_reactive.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Duser.timezone=America/Bogota","-jar","/scheme_reactive.jar"]
