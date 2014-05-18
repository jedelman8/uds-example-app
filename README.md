uds-example-app
===============

Example app built on top of CUCM UDS

Caveats:
- This example is not an official product from Cisco and comes with no warranty, expressed or implied. It is provided "as-is" to demonstrate how you might build an application on top of UDS.

- Although this example uses Jersey (and Java), UDS uses standard HTTP protocols and patterns, so you should be able to successfully create a client application in almost any language. I just happen to like Java and Jersey.

- This example intentionally disables SSL certificate validation for demonstation purposes; DO NOT DISABLE SSL CERTIFICATE VALIDATION IN PRODUCTION.

Building:
- Compile with "gradle compileJava"
- Create runnable app with "gradle installApp"
- Run with "build/install/uds-example-app/bin/uds-example-app <username>"
- The gradle os-package-plugin works great for RPM/DEB package types.
