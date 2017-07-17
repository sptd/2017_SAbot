package sabot;

import java.io.PrintWriter;
import java.io.StringWriter;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import sabot.eventhandler.EventHandlerException;
import sabot.eventhandler.FailedResponseHandler;
import sabot.eventhandler.LexEventHandler;
import sabot.lexevent.LexInputEvent;
import sabot.lexevent.LexResponse;

public class LambdaFunctionHandler implements RequestHandler<Object, LexResponse> {
	static String BUCKET_NAME;
	public static LexInputEvent lexEvent;
	static LambdaLogger lambdaLogger;

	@Override
	public LexResponse handleRequest(Object input, Context context) {
		lambdaLogger = context.getLogger();
		BUCKET_NAME = System.getenv("BUCKET_NAME");
		lambdaLogger.log("bucketName:" + BUCKET_NAME + "\n");
		try {
			lambdaLogger.log("Input:" + input + "\n");
			lambdaLogger.log("Input information is reading......\n");
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			lambdaLogger.log(mapper.writeValueAsString(input));
			lexEvent = new ObjectMapper().readValue(mapper.writeValueAsString(input), LexInputEvent.class);
		} catch (Exception e) {
			lambdaLogger.log("ERROR!! LexInputEvent cannot read correctly.\n");
			try {
				return new FailedResponseHandler(null).getJsonString();
			} catch (Exception e1) {
				loggingException(e1);
			}
		}

		if (lexEvent == null) {
			lambdaLogger.log("ERROR!! LexInputEvent cannot read correctly.\n");
			try {
				return new FailedResponseHandler(null).getJsonString();
			} catch (Exception e) {
				loggingException(e);
			}
		}

		lambdaLogger.log("Input information has been read successfully.\n");

		try {
			lambdaLogger.log("LexEventHandler create.\n");
			LexEventHandler handler = new LexEventHandler(lexEvent, BUCKET_NAME);
			lambdaLogger.log("Return Response for LexEventHandler.\n");
			return getResponse(handler);

		} catch (AmazonServiceException ase) {
			loggingException(ase);
			lambdaLogger.log("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon S3, but was rejected with an error response for some reason.\n");
			lambdaLogger.log("Error Message: " + ase.getMessage());
			lambdaLogger.log("HTTP Status Code: " + ase.getStatusCode());
			lambdaLogger.log("AWS Error Code: " + ase.getErrorCode());
			lambdaLogger.log("Error Type: " + ase.getErrorType());
			lambdaLogger.log("Request ID: " + ase.getRequestId());

		} catch (AmazonClientException ace) {
			loggingException(ace);
			lambdaLogger.log("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with S3, "
					+ "such as not being able to access the network.\n");
			lambdaLogger.log("Error Message: " + ace.getMessage() + "\n");
		} catch (Exception e) {
			loggingException(e);
		}

		try {
			return new FailedResponseHandler(null).getJsonString();
		} catch (JsonProcessingException | EventHandlerException e) {
			loggingException(e);
			return null;
		}
	}

	private LexResponse getResponse(LexEventHandler handler) throws JsonProcessingException {
		System.out.println("getResponse()");
		LexResponse response = handler.getJsonString();
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		lambdaLogger.log(mapper.writeValueAsString(response));
		return response;
	}

	private void loggingException(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		lambdaLogger.log(e.getClass().getName() + "is  occurred!!\n");
		lambdaLogger.log("1. Message...\n" + e.getMessage() + "\n");
		lambdaLogger.log("2. StackTrace...\n" + sw.toString() + "\n");
	}

}
