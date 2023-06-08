package responces;

import exceptions.InvalidResponseException;

public class ResponseHandler {
    public String getText(Response response) throws InvalidResponseException {
        if(response != null) {
            return response.getText();
        } else {
            throw new InvalidResponseException();
        }
    }

    public boolean getInteractive(Response response) throws InvalidResponseException {
        if(response != null) {
            return response.isInteractive();
        } else {
            throw new InvalidResponseException();
        }
    }

    public boolean isReady(Response response) throws InvalidResponseException {
        if(response != null) {
            return response.isReady();
        } else {
            throw new InvalidResponseException();
        }
    }
}
