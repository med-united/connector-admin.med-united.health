# Git Proxy Checkout

To use a proxy to checkout this repository you have to do the following on the machine that you checkout:

```
# Replace http://proxyUsername:proxyPassword@proxy.server.com:port with your proxy
git config --global http.https://github.com http://proxyUsername:proxyPassword@proxy.server.com:port
# Disable SSL verify because the proxy breaks it
git config --global http.https://github.com.sslVerify false
```

Afterwards you can just do a checkout with the following commands:

```
git clone https://github.com/med-united/connector-admin.med-united.health.git
```

https://git-scm.com/docs/git-config#EXAMPLES