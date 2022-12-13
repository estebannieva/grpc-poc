FROM ubuntu:kinetic
WORKDIR /tmp
RUN apt update
RUN apt install curl unzip -y
RUN curl -L -o grpcwebproxy.zip https://github.com/improbable-eng/grpc-web/releases/download/v0.15.0/grpcwebproxy-v0.15.0-linux-x86_64.zip
RUN unzip grpcwebproxy.zip -d out
RUN mv out/dist/grpcwebproxy-v0.15.0-linux-x86_64 /usr/local/bin/grpcwebproxy
EXPOSE 9090
ENTRYPOINT ["grpcwebproxy", "--backend_addr=categories:50050", "--server_http_debug_port", "9090", "--run_tls_server=false", "--allow_all_origins"]
