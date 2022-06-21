package pro.sbmn.android.cloudstodo.concrete;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListFidData {
    @SerializedName("code")
    private String code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<DataDTO> data;

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

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public static class DataDTO {
        @SerializedName("LID")
        private String lid;
        @SerializedName("User_ID")
        private String userId;
        @SerializedName("Remind_time")
        private Object remindTime;
        @SerializedName("List_title")
        private String listTitle;
        @SerializedName("List_content")
        private String listContent;
        @SerializedName("List_level")
        private String listLevel;
        @SerializedName("Valid")
        private String valid;

        public String getLid() {
            return lid;
        }

        public void setLid(String lid) {
            this.lid = lid;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Object getRemindTime() {
            return remindTime;
        }

        public void setRemindTime(Object remindTime) {
            this.remindTime = remindTime;
        }

        public String getListTitle() {
            return listTitle;
        }

        public void setListTitle(String listTitle) {
            this.listTitle = listTitle;
        }

        public String getListContent() {
            return listContent;
        }

        public void setListContent(String listContent) {
            this.listContent = listContent;
        }

        public String getListLevel() {
            return listLevel;
        }

        public void setListLevel(String listLevel) {
            this.listLevel = listLevel;
        }

        public String getValid() {
            return valid;
        }

        public void setValid(String valid) {
            this.valid = valid;
        }
    }
}
