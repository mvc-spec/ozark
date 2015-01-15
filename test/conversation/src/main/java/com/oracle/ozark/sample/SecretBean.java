package com.oracle.ozark.sample;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Class SecretBean.
 *
 * @author Santiago Pericas-Geertsen
 */
@Named("bean")
@ConversationScoped
public class SecretBean implements Serializable {

    private static final long serialVersionUID = 1234567891234567890L;

    @Inject
    private Conversation conversation;

    private String secret;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void beginConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    public void endConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }

    public String getId() {
        return conversation.getId();
    }
}
