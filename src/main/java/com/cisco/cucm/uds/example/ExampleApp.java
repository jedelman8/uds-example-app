package com.cisco.cucm.uds.example;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.cisco.cucmws.pub.jaxb.clusteruser.get.JAXBGetClusterUser_ClusterUser;
import com.cisco.cucmws.pub.jaxb.clusteruser.get.JAXBGetClusterUser_HomeCluster;
import com.cisco.cucmws.pub.jaxb.servers.get.JAXBGetServers_Servers;

public class ExampleApp {
	private static final String UDS_HOST = "se034a-58-240";
	private static final String UDS_SERVICE_URL = "https://" + UDS_HOST + ":8443/cucm-uds";

	private static final String CLUSTER_USER_URI = "/clusterUser";
	private static final String USERNAME = "username";

	public static void main(final String[] args) {
		String username = args[0];

		Client client = ClientBuilder.newBuilder()
				.sslContext(getSSLContext())
				.hostnameVerifier(getHostnameVerifier())
				.build();

		// Perform a "cluster user" lookup to determine this user's home cluster
		WebTarget clusterUserTarget = client.target(UDS_SERVICE_URL).path(CLUSTER_USER_URI).queryParam(USERNAME, username);
		JAXBGetClusterUser_ClusterUser clusterUserJaxb = clusterUserTarget.request(MediaType.APPLICATION_XML).get(JAXBGetClusterUser_ClusterUser.class);
		if (clusterUserJaxb.getResult().isFound()) {
			JAXBGetClusterUser_HomeCluster homeClusterJaxb = clusterUserJaxb.getHomeCluster();
			System.out.println("Found user " + username + " on cluster " + homeClusterJaxb.getValue());

			// Look up the full list servers in the user's home cluster
			String serversUri = homeClusterJaxb.getServersUri();
			WebTarget serversTarget = client.target(serversUri);
			JAXBGetServers_Servers serversJaxb = serversTarget.request(MediaType.APPLICATION_XML).get(JAXBGetServers_Servers.class);

			System.out.println("Servers in home cluster:");
			for (String server : serversJaxb.getServers()) {
				System.out.println("\t* " + server);
			}
		} else {
			System.out.println("Unable to find user " + username);
		}
	}

	/**
	 * Provides an SSLContext that does not validate SSL certificates; useful for self-signed certs (no trusted CA authority)
	 * @return SSLContext with validation disabled
	 */
	private static SSLContext getSSLContext() {
		TrustManager[ ] certs = new TrustManager[ ] {
				new X509TrustManager() {
					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					@Override
					public void checkClientTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
						// Skip cert check

					}
					@Override
					public void checkServerTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
						// Skip server trust check
					}
				}
		};
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
			ctx.init(null, certs, new SecureRandom());
		} catch (GeneralSecurityException ex) {
		}
		return ctx;
	}

	/**
	 * Provides a HostnameVerifier that ignores whether the hostname matches the SSL certificate; useful for self-signed certs.
	 * @return HostnameVerifier instance which does not verify that hostname matches SSL certificate.
	 */
	private static HostnameVerifier getHostnameVerifier() {
		return new HostnameVerifier() {
			@Override
			public boolean verify(final String hostname, final SSLSession session) {
				return true;
			}
		};
	}
}
