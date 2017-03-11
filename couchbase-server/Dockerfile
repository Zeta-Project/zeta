FROM couchbase/server

COPY entrypoint.sh /tmp/entrypoint.sh
RUN chmod +x /tmp/entrypoint.sh

CMD ["/bin/bash", "/tmp/entrypoint.sh"]
