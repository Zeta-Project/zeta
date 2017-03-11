FROM node:6-alpine

ENV WORKSPACE=/src \
    HOST=.

# set the workspace
WORKDIR ${WORKSPACE}

# add package.json file and npm install.
# so only changes to the package.json file will cause a rebuild
COPY ${HOST}/package.json ${WORKSPACE}/package.json
RUN npm install

#ENV PATH $PATH:/node_modules/.bin

# copy the complete project
COPY ${HOST} ${WORKSPACE}

CMD ["node", "server.js"]
