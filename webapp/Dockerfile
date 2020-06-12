FROM node:10.6.0
WORKDIR /
ARG YFILES_ACCESS
RUN if [ "$YFILES_ACCESS" = "" ] ; \
        then \
            echo '\033[0;31m'TOKEN Environment Variable not provided && \ 
            echo '\033[1;33m'Add an .env file to your root directory and insert TOKEN={your generated github oAuthToken} && \
            echo For further Information please read https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line && \
            echo '\033[0;31m'Skipping Step for cloning yfiles repository && \
            echo If you havent already cloned \
                '\033[0;34m'yfiles-for-html'\033[0;31m' and \
                '\033[0;34m'yfiles-dev-key'\033[0;31m' \
                '\033[0;31m'in the webapp/ direcotry this image wont work'\033[0m'; \
        else \
            git clone https://$YFILES_ACCESS@github.com/Zeta-Project/yFiles-dev-key.git && \
            git clone https://$YFILES_ACCESS@github.com/Zeta-Project/yFiles-for-html.git ; \
    fi
WORKDIR /src
COPY . .
RUN yarn install
EXPOSE 8080
CMD ["yarn", "dev", "run"]
