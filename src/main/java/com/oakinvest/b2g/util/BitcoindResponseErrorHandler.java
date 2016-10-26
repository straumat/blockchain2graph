package com.oakinvest.b2g.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * Response hander to deal with 500 error code from bitcoind.
 * Created by straumat on 01/09/16.
 */
public class BitcoindResponseErrorHandler implements ResponseErrorHandler {

	/**
	 * Indicates whether the given response has any errors.
	 * Implementations will typically inspect the {@link ClientHttpResponse#getStatusCode() HttpStatus}
	 * of the response.
	 *
	 * @param response the response to inspect
	 * @return {@code true} if the response has an error; {@code false} otherwise
	 * @throws IOException in case of I/O errors
	 */
	@Override
	public final boolean hasError(final ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = response.getStatusCode();
		return (HttpStatus.Series.SUCCESSFUL.equals(statusCode) || HttpStatus.Series.SERVER_ERROR.equals(statusCode));
	}

	/**
	 * Handles the error in the given response.
	 * This method is only called when {@link #hasError(ClientHttpResponse)} has returned {@code true}.
	 *
	 * @param response the response with the error
	 * @throws IOException in case of I/O errors
	 */
	@Override
	public void handleError(final ClientHttpResponse response) throws IOException {

	}

}
