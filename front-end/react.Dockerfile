FROM node:19.0.1-alpine
WORKDIR /app
ENV SRC=.
ENV DESTN=/app
COPY ${SRC}/public ${DESTN}/public
COPY ${SRC}/src ${DESTN}/src
COPY ${SRC}/package.json ${DESTN}/package.json
COPY ${SRC}/package-lock.json ${DESTN}/package-lock.json
COPY ${SRC}/postcss.config.js ${DESTN}/postcss.config.js
COPY ${SRC}/tailwind.config.js ${DESTN}/tailwind.config.js
EXPOSE 3000
RUN npm i
ENTRYPOINT ["npm", "start"]
