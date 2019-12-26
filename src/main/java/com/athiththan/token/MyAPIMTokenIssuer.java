package com.athiththan.token;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.Application;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.keymgt.issuers.APIMTokenIssuer;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;

public class MyAPIMTokenIssuer extends APIMTokenIssuer {

    private static final Log log = LogFactory.getLog(MyAPIMTokenIssuer.class);

    @Override
    public String accessToken(OAuthTokenReqMessageContext tokReqMsgCtx) throws OAuthSystemException {
        // generate the access token using super method
        String accessToken = super.accessToken(tokReqMsgCtx);

        // retrieve client id from the token request to retrieve the related application
        String clientId = tokReqMsgCtx.getOauth2AccessTokenReqDTO().getClientId();
        Application application;
        try {
            // retrieve the application using the client id
            application = APIUtil.getApplicationByClientId(clientId);

            // if the applciation is not null then it will append the devhash value or else
            // it will generate an oridinary opaque access token
            if (application != null) {

                // retrieve the token type of the application
                String tokenType = application.getTokenType();
                if (!APIConstants.JWT.equals(tokenType)) {

                    // retrieve all request params
                    RequestParameter[] reqParams = tokReqMsgCtx.getOauth2AccessTokenReqDTO().getRequestParameters();
                    for (int i = 0; i < reqParams.length; i++) {
                        // check for devhash value
                        if ("devhash".equals(reqParams[i].getKey())) {
                            accessToken += reqParams[i].getValue()[0];
                            break;
                        }
                    }
                }
            }
        } catch (APIManagementException e) {
            log.error("Exception Occured during access token generation ", e);
        }

        return accessToken;
    }
}
