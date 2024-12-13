package pcd.ass02.part2.common;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

public class FetchPageLib {

	static public String fetchPage(String uri, long timeout) throws PageFetchException {
		HttpClient client = null;
		HttpRequest request = null;
		try {			
			client = HttpClient.newHttpClient();
			request = HttpRequest.newBuilder()
			          .uri(URI.create(uri))
			          .timeout(Duration.ofMillis(timeout))
			          .build();
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			return response.body();
		} catch (Exception ex) {
			ex.printStackTrace();			
			throw new PageFetchException();
		} finally {
			if (client != null) {
				client.close();
			}			
		}
		
	}
}
