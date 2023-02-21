package org.ekstep.language.measures.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.ekstep.language.measures.ParagraphMeasures;
import org.ekstep.language.measures.WordMeasures;
import org.ekstep.language.measures.dto.Request;
import org.ekstep.language.measures.dto.RequestParams;
import org.ekstep.language.measures.dto.Response;
import org.ekstep.language.measures.dto.ResponseParams;
import org.ekstep.language.measures.enums.LanguageParams;
import org.ekstep.language.measures.enums.ResponseCode;
import org.ekstep.language.measures.enums.TaxonomyErrorCodes;
import org.ekstep.language.measures.exception.ClientException;
import org.ekstep.language.measures.exception.MiddlewareException;
import org.ekstep.language.measures.exception.ResourceNotFoundException;

import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@Slf4j
@RequestMapping("v1/language/tools")
public class ToolController {

    protected ObjectMapper mapper = new ObjectMapper();

    // {"request":{"language_id":"hi","text":"आपसे मिलकर अच्छा लगा"}}
    @RequestMapping(value = "/complexityMeasures/text", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Response> computeTextComplexity(@RequestBody Map<String, Object> map) {
        String apiId = "language.text.complexity.info";
        Request request = getRequest(map);
        String language = request.get(LanguageParams.language_id.name());
        String text = request.get(LanguageParams.text.name());

        /*request.setManagerName(LanguageActorNames.LEXILE_MEASURES_ACTOR.name());
        request.setOperation(LanguageOperations.computeTextComplexity.name());*/
        //request.getContext().put(LanguageParams.language_id.name(), language);
        log.info("List | Request: {}", request);
        try {
            Map<String, Object> result;
            if (StringUtils.isNotEmpty(text)) {
                result = mapper.readValue(mapper.writeValueAsString(WordMeasures.getWordComplexity(language, text)), new TypeReference<>() {
                });
            } else {
                result = new HashMap<>();
            }
            Response response = new Response();
            response.setVer(getAPIVersion());
            response.setResult(result);

            return getResponseEntity(response, apiId,
                    (null != request.getParams()) ? request.getParams().getMsgid() : null);
        } catch (Exception e) {
            log.error("List | Exception: {}", e.getMessage(), e);
            return getExceptionResponseEntity(e, apiId,
                    (null != request.getParams()) ? request.getParams().getMsgid() : null);
        }
    }

    @RequestMapping(value = "/wordComplexity/{languageId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Response> computeWordComplexityV2(@PathVariable(value = "languageId") String languageId,
                                                            @RequestBody Map<String, Object> map) {
        String apiId = "language.word.complexity.info";
        Request request = getRequest(map);
        String language = request.get(LanguageParams.language_id.name());
        String text = request.get(LanguageParams.text.name());
        try {
            Map<String, Object> result;

            if (StringUtils.isNotEmpty(text)) {
                String _wordList = request.get(LanguageParams.wordList.name());
                List<Map<String, Object>> wordList = StringUtils.isNotEmpty(_wordList) ?
                        mapper.readValue(_wordList, new TypeReference<>() {
                        }) : null;

                result = mapper.readValue(mapper.writeValueAsString(ParagraphMeasures.getTextComplexity(language, text, wordList)), new TypeReference<>() {
                });
            } else {
                result = new HashMap<>();
            }
            Response response = new Response();
            response.setVer(getAPIVersion());
            response.setResult(result);

            return getResponseEntity(response, apiId,
                    (null != request.getParams()) ? request.getParams().getMsgid() : null);
        } catch (Exception e) {
            log.error("List | Exception: {}", e.getMessage(), e);
            return getExceptionResponseEntity(e, apiId,
                    (null != request.getParams()) ? request.getParams().getMsgid() : null);
        }
    }

    protected ResponseEntity<Response> getExceptionResponseEntity(Exception e, String apiId, String msgId) {
        HttpStatus status = getHttpStatus(e);
        Response response = getErrorResponse(e);
        setResponseEnvelope(response, apiId, msgId);
        return new ResponseEntity<Response>(response, status);
    }

    protected HttpStatus getHttpStatus(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (e instanceof ClientException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (e instanceof ResourceNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        }
        return status;
    }

    protected Response getErrorResponse(Exception e) {
        Response response = new Response();
        ResponseParams resStatus = new ResponseParams();
        String message = setMessage(e);
        resStatus.setErrmsg(message);
        resStatus.setStatus(ResponseParams.StatusType.failed.name());
        if (e instanceof MiddlewareException) {
            MiddlewareException me = (MiddlewareException) e;
            resStatus.setErr(me.getErrCode());
            response.setResponseCode(me.getResponseCode());
        } else {
            resStatus.setErr(TaxonomyErrorCodes.SYSTEM_ERROR.name());
            response.setResponseCode(ResponseCode.SERVER_ERROR);
        }
        response.setParams(resStatus);
        return response;
    }

    protected String setMessage(Exception e) {
        if (e instanceof MiddlewareException) {
            return e.getMessage();
        } else {
            return "Something went wrong in server while processing the request";
        }
    }

    protected ResponseEntity<Response> getResponseEntity(Response response, String apiId, String msgId) {
        int statusCode = response.getResponseCode().code();
        HttpStatus status = getStatus(statusCode);
        setResponseEnvelope(response, apiId, msgId);
        return new ResponseEntity<Response>(response, status);
    }

    protected HttpStatus getStatus(int statusCode) {
        HttpStatus status = null;
        try {
            status = HttpStatus.valueOf(statusCode);
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return status;
    }

    public static final String API_VERSION = "1.0";

    protected String getAPIVersion() {
        return API_VERSION;
    }

    private String getResponseTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'XXX");
        return sdf.format(new Date());
    }

    private String getUUID() {
        UUID uid = UUID.randomUUID();
        return uid.toString();
    }

    private void setResponseEnvelope(Response response, String apiId, String msgId) {
        if (null != response) {
            response.setId(apiId);
            response.setVer(getAPIVersion());
            response.setTs(getResponseTimestamp());
            ResponseParams params = response.getParams();
            if (null == params)
                params = new ResponseParams();
            if (StringUtils.isNotBlank(msgId))
                params.setMsgid(msgId);
            params.setResmsgid(getUUID());
            if (StringUtils.equalsIgnoreCase(ResponseParams.StatusType.successful.name(), params.getStatus())) {
                params.setErr(null);
                params.setErrmsg(null);
            }
            response.setParams(params);
        }
    }

    protected Request getRequest(Map<String, Object> requestMap) {
        Request request = new Request();
        if (null != requestMap && !requestMap.isEmpty()) {
            String id = (String) requestMap.get("id");
            String ver = (String) requestMap.get("ver");
            String ts = (String) requestMap.get("ts");
            request.setId(id);
            request.setVer(ver);
            request.setTs(ts);
            Object reqParams = requestMap.get("params");
            if (null != reqParams) {
                try {
                    RequestParams params = (RequestParams) mapper.convertValue(reqParams, RequestParams.class);
                    request.setParams(params);
                } catch (Exception e) {
                }
            }
            Object requestObj = requestMap.get("request");
            if (null != requestObj) {
                try {
                    String strRequest = mapper.writeValueAsString(requestObj);
                    Map<String, Object> map = mapper.readValue(strRequest, Map.class);
                    if (null != map && !map.isEmpty())
                        request.setRequest(map);
                } catch (Exception e) {
                }
            }
        }
        return request;
    }
}
