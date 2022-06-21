package pro.sbmn.android.cloudstodo.concrete;

import com.google.gson.annotations.SerializedName;

public class SigninData {
    @SerializedName("code")
    private String code;
    @SerializedName("message")
    private String message;
    @SerializedName("uid")
    private String uid;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
