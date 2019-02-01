# Activeeon - SSL Certificate Generator for ProActive

This project includes utility tools to generate trusted SSL certificates and keystores to be used by 
[ProActive](https://proactive.activeeon.com/),  in order to establish secure SSL communications with client hosts. In particular, this project includes two tools, namely: (1) a shell script to generate SSL certificates for localhost (when running ProActive locally), and (2) a Java client to generate SSL certificates for a specific domain.

## 1.  SSL certificate for localhost 
To generate a trusted SSL certificate for localhost, we create a shell script called `generate_trusted_cert_and_keystore_for_localhost.sh`, which is located under the folder `ssl-certificate-for-localhost`. The script proceed through the following steps:
- First, it considers a local Certification Authority (CA) called **ActiveeonCA**. 
- It generates a private key (*`ActiveeonCA.key`*) a local root certificate (*`ActiveeonRootCA.crt`*) for ActiveeonCA.
- After that, the script generates a private key (*`localhost.key`*) and a certificate (*`localhost.crt`*) for localhost.  The localhost certificate is signed by the  local root certificate *`ActiveeonRootCA.crt`*.
- Then, the script stores *`localhost.key`* and *`localhost.crt`* in a key store called *`keystore`*.
- The generated *`keystore`* is to be used by ProActive for SSL communication and must be placed under `${ProActive_Home}/config/web`.
- To make the certificate used by ProActive trusted by the client host:
	- Either add *`ActiveeonRootCA.crt`* to the OS trust store ([as explained here](https://manuals.gfi.com/en/kerio/connect/content/server-configuration/ssl-certificates/adding-trusted-root-certificates-to-the-server-1605.html))
	- Or  add *`ActiveeonRootCA.crt`* to the client web browser (for instance, In Mozilla Firefox, click  Tools  >  Options  >  Advanced. Scroll down, click  Manage Certificates, click  Authorities and then import *ActiveeonRootCA.crt*).

The script `generate_trusted_cert_and_keystore_for_localhost.sh` uses two files: (i) `v3.ext`, used to create *localhost.crt*, and (ii) `secret`, which contains a single password used to secure all private keys and keystores.

## 2. SSL certificate for specific domain (*e.g., try.activeeon.com*)
We extend an existing [ Java client](https://github.com/shred/acme4j) to generate SSL certificates for a specific domain.
The java client uses the ACME ([Automatic Certificate Management Environment](https://tools.ietf.org/html/draft-ietf-acme-acme)) protocol to connect to an ACME server, notably _Let's Encrypt_ server. It performs all necessary steps to generate SSL certificates. Our Java client mainly contains three classes:
a) **SSLCertificateGenerator**: Its role is twofold: (i) it creates a domain challenge, i.e., a specific static web resource that sould be accessible under the considered domain, and (ii) generate SSL certificate for the considered domain one the ACME challenge is met.
b) **WebResource**: It creates the static web resource needed to meet the ACME challenge.
c) **EmbeddedJetty**: It runs an embedded web application server (Jetty) that serves the created static web resource.

As an example of ACME challenge (required to generate the SSL certificate for a given domain)
- The following resource  must be created:
*https://domain/.well-know/acme-challenge/kJmvx69CEVe5uEPMwNqRiHXyni__7t4sp-aJaBTaFIE*
- It must contain the following content:
*kJmvx69CEVe5uEPMwNqRiHXyni__7t4sp-aJaBTaFIE.XMOaXhDazt2A3R42GzSEFIOFs7jsiUgY6xc1N2iL0MY*

### Build Jar artefact
Execute the following command to produce a jar file called *`ssl-certificate-generator-all-xxxxVersion.jar`*
```
./gradlew shadowJar
```
### Generate certificate
```
java -jar ssl-certificate-generator-all-xxxxVersion.jar -d domain_hostname
```
e.g., *java -jar ssl-certificate-generator-all-xxxxVersion.jar -d try.activeeon.com -p 8080*

The jar takes further arguments as input. To get more help about these arguments run, e.g.,:
*java -jar ssl-certificate-generator-all-xxxxVersion.jar -d try.activeeon.com -h*

##### Meet ACME challenge when using a web server to access the considered domain
When the domain (concerned by the SSL certificate) is accessible via a web server like Apache or Nginx, the static resources created to meet the ACME challenge cannot be served by the web server in a straightforward manner. To cope with this issue, two solutions are proposed:
i) Place and run *`ssl-certificate-generator-all-xxxxVersion.jar`* under the resources directory of the web server, e.g., under `/var/www/html` when using `nginx`.
ii) Add the location (where *`ssl-certificate-generator-all-xxxxVersion.jar`*  is placed) to the configuration of the web server. For instance, when using `nginx` add the following block:
```
location ^~ /.well-known/acme-challenge/ {
    allow  all;
    root  path_to_where_to_run_ssl-certificate-generator-all-xxxxVersion.jar;
}
```
This is particularly useful when generating SSL certificates for try.activeeon.com, tryqa.activeeon.com and trydev.activeeon.com.