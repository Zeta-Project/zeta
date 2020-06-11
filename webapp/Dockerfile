FROM node:10.6.0
WORKDIR /
ARG YFILES_ACCESS
RUN git clone https://${YFILES_ACCESS}@github.com/Zeta-Project/yFiles-dev-key.git
RUN git clone https://${YFILES_ACCESS}@github.com/Zeta-Project/yFiles-for-html.git
WORKDIR /src
COPY . .
RUN yarn install
EXPOSE 8080
CMD ["yarn", "dev", "run"]
