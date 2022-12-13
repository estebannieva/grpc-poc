FROM ubuntu:jammy
WORKDIR /home/ubuntu
RUN apt update
RUN apt install -y curl
RUN curl -L -o ghz-linux-x86_64.tar.gz https://github.com/bojand/ghz/releases/download/v0.109.0/ghz-linux-x86_64.tar.gz
RUN tar -xzf ghz-linux-x86_64.tar.gz
RUN mv ghz /usr/local/bin/ghz
RUN mv ghz-web /usr/local/bin/ghz-web
RUN rm ghz-linux-x86_64.tar.gz LICENSE
