FROM node:6.4

ENV WORKSPACE=/src \
    HOST=.

# set the workspace
WORKDIR ${WORKSPACE}

# add package.json file and npm install.
# so only changes to the package.json file will cause a rebuild
COPY ${HOST}/package.json ${WORKSPACE}/package.json
RUN npm install

ENV PATH ./node_modules/.bin:$PATH

# same for bower dependencies
COPY ${HOST}/bower.json ${WORKSPACE}/bower.json
RUN bower install --allow-root

# copy the complete project
COPY ${HOST} ${WORKSPACE}
