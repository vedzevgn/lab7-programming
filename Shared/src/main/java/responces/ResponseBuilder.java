package responces;


import responces.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResponseBuilder {
    private static final Logger logger = LogManager.getLogger();
    public Response createResponse(String text, boolean interactive, boolean ready){
        logger.info("Создан ответ.");
        return new Response(text, interactive, ready);
    }
}
