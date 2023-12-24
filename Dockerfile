ARG ES_VERSION
FROM docker.elastic.co/elasticsearch/elasticsearch:$ES_VERSION as builder

USER root
ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update -y && apt-get install -y software-properties-common build-essential
RUN gcc --version
RUN apt-get update -y && apt-get install -y make cmake pkg-config wget git

ENV JAVA_HOME=/usr/share/elasticsearch/jdk
ENV PATH=$JAVA_HOME/bin:$PATH

# Build analysis-khmerlang
RUN echo "analysis-khmerlang..."
WORKDIR /tmp
RUN wget https://dlcdn.apache.org/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz \
    && tar xvf apache-maven-3.8.8-bin.tar.gz
ENV MVN_HOME=/tmp/apache-maven-3.8.8
ENV PATH=$MVN_HOME/bin:$PATH

COPY . /tmp/elasticsearch-analysis-khmerlang
WORKDIR /tmp/elasticsearch-analysis-khmerlang
RUN mvn verify clean --fail-never
RUN mvn --batch-mode -Dmaven.test.skip -e package

FROM docker.elastic.co/elasticsearch/elasticsearch:$ES_VERSION
ARG ES_VERSION

COPY --from=builder /tmp/elasticsearch-analysis-khmerlang/target/releases/elasticsearch-analysis-khmerlang-$ES_VERSION.zip /
RUN echo "Y" | /usr/share/elasticsearch/bin/elasticsearch-plugin install --batch file:///elasticsearch-analysis-khmerlang-$ES_VERSION.zip
