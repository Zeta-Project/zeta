FROM couchbase/sync-gateway:1.3.1-community

COPY entrypoint.sh /tmp/entrypoint.sh
RUN chmod +x /tmp/entrypoint.sh

COPY config.json /home/sync_gateway/sync_gateway.json

ENTRYPOINT ["/bin/bash", "/tmp/entrypoint.sh"]
