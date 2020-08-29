FROM node:10.19.0
WORKDIR /
ARG YFILES_ACCESS
RUN if [ "$YFILES_ACCESS" = "" ] ; \
        then \
            echo '\033[0;31m'TOKEN Environment Variable not provided && \
            echo '\033[1;33m'Add an .env file to your root directory and insert TOKEN={your generated github oAuthToken} && \
            echo For further Information please read https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line && \
            -1 ; \
    fi
RUN if [ "$YFILES_ACCESS" != "" ] ; \
        then \
            git clone https://$YFILES_ACCESS@github.com/Zeta-Project/yFiles-dev-key.git && \
            git clone https://$YFILES_ACCESS@github.com/Zeta-Project/yFiles-for-html.git ; \
    fi
WORKDIR /zeta/frontend
COPY . .
RUN yarn install
EXPOSE 8080
CMD ["yarn", "run", "serve"]
